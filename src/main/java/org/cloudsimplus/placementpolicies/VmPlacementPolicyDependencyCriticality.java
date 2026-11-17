/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.placementpolicies;

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Processor;
import org.cloudsimplus.vms.Vm;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.cloudsimplus.resources.Processor.cores;

/// A VmPlacementPolicy that considers what would break if a Host failed,
/// using a [VmDependencyGraph] that says which services each VM runs
/// and which services depend on each other.
///
/// For each suitable Host, it computes the cost of placing the VM as
/// `lambda * energy + (1 - lambda) * risk`, and chooses the Host with the lowest cost:
///
/// - `energy` is the extra power the VM needs on that Host, as a fraction of the
///   Host maximum power. It includes the Host static power when the Host has no VMs yet,
///   so filling Hosts that are already in use costs less;
/// - `risk` is the criticality, as a fraction of the criticality of the VM application,
///   that the VM would newly break if that Host failed
///   (see [VmDependencyGraph#brokenServices(java.util.Collection)]).
///   It is 0 unless the Host would then hold every working replica of the VM service.
///
/// Hosts that fail together, such as the Hosts of a rack, can be grouped with
/// [#setFaultDomain(Function)]. The risk is then the mean of what a failure of the Host
/// and a failure of its whole fault domain would newly break, so replicas are kept
/// in different domains when possible, and on different Hosts otherwise.
///
/// Each service weighs 1 by default, so the risk counts broken services
/// (see [VmDependencyGraph#useCriticalityIndex()] for weighted services).
/// A low `lambda` spreads replicas and dependency hubs over more Hosts.
/// A high `lambda` packs VMs into fewer Hosts, like [VmPlacementPolicyBestFit].
/// The default of 0.1 gave the fewest broken services in a sweep of lambda from 0 to 1,
/// without using more Hosts at the end of the placement, since a Host opened early is filled later.
/// Ties are broken as in [VmPlacementPolicyBestFit]: active Hosts first,
/// then Hosts with fewer free cores, then the lowest Host id.
///
/// VMs that aren't in the graph are placed by the energy term alone.
///
/// Placing a VM costs O(H) to filter and compare the Hosts, plus, for each fault domain
/// with a suitable Host, the graph work described in
/// [VmDependencyGraph#addedRiskIfDomainFails(java.util.Collection, Vm)] over the VMs in that domain.
/// That is only paid by simulations that use this policy.
///
/// **NOTE: This policy doesn't perform optimization of VM allocation by means of VM migration.**
///
/// @author Prawal Pokharel
/// @since CloudSim Plus 9.0.0
public class VmPlacementPolicyDependencyCriticality extends VmPlacementPolicyAbstract {
    /// The default weight of the energy term.
    public static final double DEF_LAMBDA = 0.1;

    /// Fraction of the Host power assumed to be static when the Host has no power model.
    private static final double DEF_STATIC_POWER_FRACTION = 0.5;

    /// The graph describing the services of the VMs and their dependencies.
    @Getter
    private final VmDependencyGraph graph;

    /// The weight of the energy term, in \[0, 1\].
    @Getter
    private final double lambda;

    /// Maps a Host to its fault domain (such as its rack), or null if each Host is its own domain.
    private @Nullable Function<? super Host, ?> faultDomain;

    /// The Hosts of each fault domain, built when first needed.
    private @Nullable Map<Object, List<Host>> domainHosts;

    /// The number of Hosts when [#domainHosts] was built, to rebuild it if Hosts are added.
    private int domainHostCount;

    /// Whether the missing power model warning was already logged.
    private boolean warnedNoPowerModel;

    /// Creates a policy using the default energy weight [#DEF_LAMBDA].
    /// @param graph the graph describing the services of the VMs and their dependencies
    public VmPlacementPolicyDependencyCriticality(@NonNull final VmDependencyGraph graph) {
        this(graph, DEF_LAMBDA);
    }

    /// Creates a policy.
    /// @param graph the graph describing the services of the VMs and their dependencies
    /// @param lambda the weight of the energy term, in \[0, 1\]
    public VmPlacementPolicyDependencyCriticality(@NonNull final VmDependencyGraph graph, final double lambda) {
        super();
        if (lambda < 0 || lambda > 1) {
            throw new IllegalArgumentException("lambda must be in [0, 1]");
        }

        this.graph = graph;
        this.lambda = lambda;
    }

    /// Groups Hosts that fail together (for example, Hosts in the same rack), so the policy
    /// considers the failure of a whole group. By default each Host is its own fault domain.
    /// @param faultDomain a function that returns the same key for Hosts in the same domain
    ///                    (such as a rack number), or null to make each Host its own domain
    /// @return this policy
    public VmPlacementPolicyDependencyCriticality setFaultDomain(@Nullable final Function<? super Host, ?> faultDomain) {
        this.faultDomain = faultDomain;
        this.domainHosts = null;
        return this;
    }

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final boolean inGraph = graph.contains(vm);
        final var fn = faultDomain;
        if (fn != null) {
            /* Built here, before the Host search that may run in parallel, which only reads it. */
            buildDomainHosts(fn);
        }

        /* The risk only depends on the fault domain, so it's computed once per domain. */
        final Map<Object, Double> riskByDomain = new ConcurrentHashMap<>();
        final Comparator<Candidate> comparator = Comparator.comparingDouble(Candidate::cost)
            .thenComparing(candidate -> candidate.host().isActive(), Comparator.reverseOrder())
            .thenComparingLong(candidate -> Processor.freeCores(candidate.host()))
            .thenComparingLong(candidate -> candidate.host().getId());

        final var stream = isParallelHostSearchEnabled() ? getHostList().stream().parallel() : getHostList().stream();
        return stream
            .filter(host -> host.isSuitableForVm(vm))
            .map(host -> new Candidate(host, cost(host, vm, inGraph, riskByDomain)))
            .min(comparator)
            .map(Candidate::host);
    }

    /// A suitable Host and the cost of placing the VM into it, computed once per Host.
    private record Candidate(Host host, double cost) {}

    /// {@return the cost of placing a VM into a Host}
    /// @param host the Host to evaluate
    /// @param vm the VM to place
    /// @param inGraph whether the VM is in the [#graph]
    /// @param riskByDomain the risk already computed for each fault domain during this placement
    double cost(final Host host, final Vm vm, final boolean inGraph, final Map<Object, Double> riskByDomain) {
        return lambda * addedPowerFraction(host, vm) + (1 - lambda) * (inGraph ? risk(host, vm, riskByDomain) : 0);
    }

    /// {@return the added risk of placing a VM into a Host}
    /// Without fault domains, it's the risk of the Host failing. With fault domains, a Host failure
    /// and a failure of its whole domain count equally, so replicas that must share a domain
    /// still go to different Hosts.
    private double risk(final Host host, final Vm vm, final Map<Object, Double> riskByDomain) {
        final double hostRisk = graph.addedRiskIfHostFails(host, vm);
        final var fn = faultDomain;
        if (fn == null) {
            return hostRisk;
        }

        final double domainRisk = riskByDomain.computeIfAbsent(
            domainKey(host), key -> graph.addedRiskIfDomainFails(hostsInDomainOf(host), vm));
        return (hostRisk + domainRisk) / 2;
    }

    private Object domainKey(final Host host) {
        final var fn = faultDomain;
        final Object key = fn == null ? null : fn.apply(host);
        return key == null ? host : key;
    }

    /// Groups the Hosts by fault domain, if not done yet or if Hosts were added.
    private void buildDomainHosts(final Function<? super Host, ?> fn) {
        final List<Host> hosts = getHostList();
        if (domainHosts != null && domainHostCount == hosts.size()) {
            return;
        }

        final Map<Object, List<Host>> groups = new HashMap<>();
        for (final Host h : hosts) {
            final Object key = fn.apply(h);
            groups.computeIfAbsent(key == null ? h : key, k -> new ArrayList<>()).add(h);
        }

        domainHosts = groups;
        domainHostCount = hosts.size();
    }

    /// {@return the Hosts in the same fault domain as a Host}
    /// The groups are built by [#buildDomainHosts(Function)] before the Host search.
    private List<Host> hostsInDomainOf(final Host host) {
        final var groups = domainHosts;
        return groups == null ? List.of(host) : groups.getOrDefault(domainKey(host), List.of(host));
    }

    /// {@return the extra power a VM needs on a Host, as a fraction of the Host maximum power}
    /// Assumes the VM uses all its cores, and adds the static power when the Host has no VMs.
    /// A Host without a power model is assumed to use half of its power when idle,
    /// and a warning is logged the first time that happens.
    double addedPowerFraction(final Host host, final Vm vm) {
        final var model = host.getPowerModel();
        final double maxPower = model.getPower(1);
        if (maxPower <= 0 && !warnedNoPowerModel) {
            warnedNoPowerModel = true;
            LOGGER.warn(
                "{}: {} has no power model. {} assumes Hosts without one use {}% of their power when idle.",
                vm.getSimulation().clockStr(), host, getClass().getSimpleName(), (int) (DEF_STATIC_POWER_FRACTION * 100));
        }

        final double staticFraction = maxPower > 0 ? model.getPower(0) / maxPower : DEF_STATIC_POWER_FRACTION;
        final double coresFraction = Math.min(1.0, cores(vm) / (double) cores(host));
        final double opening = host.getVmList().isEmpty() ? staticFraction : 0;
        return opening + (1 - staticFraction) * coresFraction;
    }
}
