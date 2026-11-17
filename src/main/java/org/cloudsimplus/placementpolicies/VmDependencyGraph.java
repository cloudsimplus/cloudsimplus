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

import lombok.NonNull;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/// Describes how the VMs of one or more applications depend on each other,
/// so that a placement policy can estimate what breaks when a Host fails.
///
/// Each VM belongs to a service, and a service may run in several replica VMs.
/// A dependency `caller -> callee` means the caller needs the callee to work.
/// A service is **down** when none of its replicas is available,
/// and it is **broken** when it is down or when a service it depends on,
/// directly or not, is down.
///
/// Each service has a weight used to add up what breaks. By default every service
/// weighs 1, so the risk of a failure is the number of services it breaks.
/// A weight can be set for a service with [#setCriticality(String, double)], or
/// computed for all services by [CriticalityIndex] after [#useCriticalityIndex()],
/// from each service's position in the graph, the entropy of its demand history
/// ([#setDemandHistory(String, double...)]) and a criticality declared by
/// the operator ([#setDeclaredCriticality(String, double)]).
///
/// The graph must be fully built before the VMs are placed.
/// Changing it afterwards discards the computed scores.
///
/// @author Prawal Pokharel
/// @since CloudSim Plus 9.0.0
/// @see VmPlacementPolicyDependencyCriticality
public class VmDependencyGraph {
    private final Map<String, Integer> serviceIndex = new LinkedHashMap<>();
    private final List<String> services = new ArrayList<>();
    private final List<List<Integer>> callees = new ArrayList<>();
    private final List<List<Vm>> replicas = new ArrayList<>();
    private final List<Double> declared = new ArrayList<>();
    private final List<double[]> demandHistory = new ArrayList<>();
    private final Map<Integer, Double> criticalityOverride = new HashMap<>();
    /// Uses identity, since the `equals()`/`hashCode()` of a VM change when its broker is set.
    private final Map<Vm, Integer> vmService = new IdentityHashMap<>();

    private double alpha = CriticalityIndex.DEF_ALPHA;
    private double beta = CriticalityIndex.DEF_BETA;
    private double gamma = CriticalityIndex.DEF_GAMMA;

    /// Whether the weights come from [CriticalityIndex] (true) or every service weighs 1 (false).
    private boolean criticalityIndex;

    /// Criticality of each service, computed lazily. Null when it must be recomputed.
    private double @Nullable [] criticality;

    /// For each service, the services broken when it is down (itself and every transitive caller).
    private BitSet @Nullable [] brokenWhenDown;

    /// For each service, the index of its application (weakly connected component).
    private int @Nullable [] application;

    /// Sum of the criticality of the services of each application.
    private double @Nullable [] applicationCriticality;

    /// Adds a service if it isn't in the graph yet.
    /// @param service the service name, unique in the graph
    /// @return this graph
    public VmDependencyGraph addService(@NonNull final String service) {
        indexOf(service);
        return this;
    }

    /// Adds a VM as a replica of a service, adding the service if needed.
    /// @param service the service the VM runs
    /// @param vm the VM to add
    /// @return this graph
    public VmDependencyGraph addVm(@NonNull final String service, @NonNull final Vm vm) {
        final int idx = indexOf(service);
        if (vmService.putIfAbsent(vm, idx) != null) {
            throw new IllegalArgumentException(vm + " was already added to the graph");
        }

        replicas.get(idx).add(vm);
        return this;
    }

    /// Adds a dependency, meaning the caller needs the callee to work.
    /// Services that aren't in the graph are added.
    /// @param caller the service that depends on the callee
    /// @param callee the service the caller depends on
    /// @return this graph
    public VmDependencyGraph addDependency(@NonNull final String caller, @NonNull final String callee) {
        final int from = indexOf(caller);
        final int to = indexOf(callee);
        if (from == to) {
            throw new IllegalArgumentException("A service cannot depend on itself: " + caller);
        }

        if (!callees.get(from).contains(to)) {
            callees.get(from).add(to);
            invalidate();
        }

        return this;
    }

    /// Sets the criticality the operator declares for a service (the governance term),
    /// such as 1 for a service that holds state or takes payments.
    /// @param service the service name
    /// @param value a value in \[0, 1\]
    /// @return this graph
    public VmDependencyGraph setDeclaredCriticality(@NonNull final String service, final double value) {
        declared.set(indexOf(service), checkFraction(value, "declared criticality"));
        invalidate();
        return this;
    }

    /// Sets the demand history of a service, used for the entropy term.
    /// @param service the service name
    /// @param utilization utilization values in \[0, 1\] over a time window
    /// @return this graph
    public VmDependencyGraph setDemandHistory(@NonNull final String service, final double... utilization) {
        demandHistory.set(indexOf(service), utilization.clone());
        invalidate();
        return this;
    }

    /// Sets the criticality score of a service directly, instead of computing it.
    /// @param service the service name
    /// @param value a non-negative score
    /// @return this graph
    public VmDependencyGraph setCriticality(@NonNull final String service, final double value) {
        if (value < 0) {
            throw new IllegalArgumentException("criticality cannot be negative");
        }

        criticalityOverride.put(indexOf(service), value);
        invalidate();
        return this;
    }

    /// Weighs each service by its [CriticalityIndex] score, with the default weights of the
    /// centrality, entropy and declared criticality terms, instead of weighing every service 1.
    /// @return this graph
    public VmDependencyGraph useCriticalityIndex() {
        criticalityIndex = true;
        invalidate();
        return this;
    }

    /// Weighs each service by its [CriticalityIndex] score, with the given weights of the
    /// centrality, entropy and declared criticality terms.
    /// @return this graph
    public VmDependencyGraph setWeights(final double alpha, final double beta, final double gamma) {
        if (alpha < 0 || beta < 0 || gamma < 0) {
            throw new IllegalArgumentException("weights cannot be negative");
        }

        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        return useCriticalityIndex();
    }

    /// {@return the number of services in the graph}
    public int getServiceCount() {
        return services.size();
    }

    /// {@return true if the VM was added to the graph}
    /// @param vm the VM to check
    public boolean contains(final Vm vm) {
        return vmService.containsKey(vm);
    }

    /// {@return the service a VM belongs to, or an empty string if the VM isn't in the graph}
    /// @param vm the VM to get its service
    public String getService(final Vm vm) {
        final Integer idx = vmService.get(vm);
        return idx == null ? "" : services.get(idx);
    }

    /// {@return the replica VMs of a service}
    /// @param service the service name
    public List<Vm> getReplicas(final String service) {
        final Integer idx = serviceIndex.get(service);
        return idx == null ? List.of() : Collections.unmodifiableList(replicas.get(idx));
    }

    /// {@return the weight of a service (1 unless set or computed otherwise), or 0 if it isn't in the graph}
    /// @param service the service name
    public double getCriticality(final String service) {
        final Integer idx = serviceIndex.get(service);
        return idx == null ? 0 : criticality()[idx];
    }

    /// {@return the names of the services broken when the given VMs are unavailable}
    /// A service is down when all its replicas are in the given collection,
    /// and broken when it is down or depends on a service that is down.
    /// @param unavailableVms VMs that failed, were destroyed or were never placed
    public Set<String> brokenServices(final Collection<? extends Vm> unavailableVms) {
        final Set<Vm> unavailable = Collections.newSetFromMap(new IdentityHashMap<>());
        unavailable.addAll(unavailableVms);
        final var broken = new BitSet(services.size());
        for (int s = 0; s < services.size(); s++) {
            final var serviceReplicas = replicas.get(s);
            if (!serviceReplicas.isEmpty() && unavailable.containsAll(serviceReplicas)) {
                broken.or(brokenWhenDown()[s]);
            }
        }

        return broken.stream().mapToObj(services::get).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /// Computes how much criticality would break if a Host failed after a VM is placed into it,
    /// compared to before, as a fraction of the criticality of the VM application.
    ///
    /// @param host the Host that may receive the VM
    /// @param vm the VM to place, which must be in the graph
    /// @return a value in \[0, 1\]
    /// @see #addedRiskIfDomainFails(Collection, Vm)
    double addedRiskIfHostFails(final Host host, final Vm vm) {
        return addedRiskIfDomainFails(List.of(host), vm);
    }

    /// Computes how much criticality would break if a fault domain (a group of Hosts that fail
    /// together, such as a rack) failed after a VM is placed into one of its Hosts,
    /// compared to before, as a fraction of the criticality of the VM application.
    ///
    /// Only the service of the given VM changes state, so the result is the criticality
    /// of the services that the VM service would newly break, divided by the total
    /// criticality of its application. A service counts as down when the domain fails
    /// only if every replica that is still working is already placed and all of them
    /// would be in that domain. While some replica is not placed yet, the service is
    /// assumed to survive, since the policy places the remaining replicas later.
    ///
    /// Only VMs of the same application are looked at, since services of other applications
    /// can't break the services of this one. The cost is O(V + A/64 * D) for a domain with
    /// V VMs and D services of the application down in it, where A is the number of services.
    ///
    /// @param domainHosts the Hosts of the domain, one of which may receive the VM
    /// @param vm the VM to place, which must be in the graph
    /// @return a value in \[0, 1\]
    double addedRiskIfDomainFails(final Collection<? extends Host> domainHosts, final Vm vm) {
        final Integer svc = vmService.get(vm);
        if (svc == null || !isDownIfDomainFails(svc, domainHosts, vm)) {
            return 0;
        }

        final int app = application()[svc];
        final double total = applicationCriticality()[app];
        if (total <= 0) {
            return 0;
        }

        final var before = brokenIfDomainFails(domainHosts, app);
        final var added = (BitSet) brokenWhenDown()[svc].clone();
        added.andNot(before);
        final double[] crit = criticality();
        return Math.min(1.0, added.stream().mapToDouble(s -> crit[s]).sum() / total);
    }

    /// {@return the services of an application broken if a fault domain fails, given the current placement}
    private BitSet brokenIfDomainFails(final Collection<? extends Host> domainHosts, final int app) {
        final var broken = new BitSet(services.size());
        final var checked = new BitSet(services.size());
        final int[] appOf = application();
        for (final Host host : domainHosts) {
            for (final Vm hostVm : host.getVmList()) {
                final Integer s = vmService.get(hostVm);
                if (s == null || appOf[s] != app || checked.get(s)) {
                    continue;
                }

                checked.set(s);
                if (isDownIfDomainFails(s, domainHosts, null)) {
                    broken.or(brokenWhenDown()[s]);
                }
            }
        }

        return broken;
    }

    /// Checks if a service would be down after a fault domain fails.
    /// @param svc the service index
    /// @param domainHosts the Hosts that fail together
    /// @param extraVm a VM being placed into the domain (counted as placed there), or null
    private boolean isDownIfDomainFails(final int svc, final Collection<? extends Host> domainHosts,
                                        @Nullable final Vm extraVm)
    {
        boolean anyWorking = false;
        for (final Vm replica : replicas.get(svc)) {
            if (replica.isFailed()) {
                continue;
            }

            anyWorking = true;
            if (replica == extraVm) {
                continue;
            }

            final Host replicaHost = replica.getHost();
            if (replicaHost == Host.NULL || !containsHost(domainHosts, replicaHost)) {
                return false;
            }
        }

        return anyWorking;
    }

    /// Checks by identity if a Host is in a domain. Domains are small (a rack), so a scan is enough.
    private static boolean containsHost(final Collection<? extends Host> domainHosts, final Host host) {
        for (final Host h : domainHosts) {
            if (h == host) {
                return true;
            }
        }

        return false;
    }

    private int indexOf(final String service) {
        final Integer existing = serviceIndex.get(service);
        if (existing != null) {
            return existing;
        }

        final int idx = services.size();
        serviceIndex.put(service, idx);
        services.add(service);
        callees.add(new ArrayList<>());
        replicas.add(new ArrayList<>());
        declared.add(0.0);
        demandHistory.add(new double[0]);
        invalidate();
        return idx;
    }

    private void invalidate() {
        criticality = null;
        brokenWhenDown = null;
        application = null;
        applicationCriticality = null;
    }

    private double[] criticality() {
        final double[] cached = criticality;
        if (cached != null) {
            return cached;
        }

        final double[] centrality = criticalityIndex ? CriticalityIndex.centrality(callees) : new double[0];
        final var result = new double[services.size()];
        for (int s = 0; s < result.length; s++) {
            final Double override = criticalityOverride.get(s);
            if (override != null) {
                result[s] = override;
            } else if (criticalityIndex) {
                result[s] = CriticalityIndex.score(
                    centrality[s], CriticalityIndex.entropy(demandHistory.get(s)), declared.get(s), alpha, beta, gamma);
            } else {
                result[s] = 1;
            }
        }

        criticality = result;
        return result;
    }

    private BitSet[] brokenWhenDown() {
        final BitSet[] cached = brokenWhenDown;
        if (cached != null) {
            return cached;
        }

        final int n = services.size();
        final var callers = new ArrayList<List<Integer>>(n);
        for (int s = 0; s < n; s++) {
            callers.add(new ArrayList<>());
        }

        for (int s = 0; s < n; s++) {
            for (final int callee : callees.get(s)) {
                callers.get(callee).add(s);
            }
        }

        final var result = new BitSet[n];
        for (int s = 0; s < n; s++) {
            final var reached = new BitSet(n);
            final var stack = new ArrayList<Integer>();
            reached.set(s);
            stack.add(s);
            while (!stack.isEmpty()) {
                final int v = stack.removeLast();
                for (final int caller : callers.get(v)) {
                    if (!reached.get(caller)) {
                        reached.set(caller);
                        stack.add(caller);
                    }
                }
            }

            result[s] = reached;
        }

        brokenWhenDown = result;
        return result;
    }

    private int[] application() {
        final int[] cached = application;
        if (cached != null) {
            return cached;
        }

        final int n = services.size();
        final var parent = new int[n];
        for (int s = 0; s < n; s++) {
            parent[s] = s;
        }

        for (int s = 0; s < n; s++) {
            for (final int callee : callees.get(s)) {
                parent[find(parent, s)] = find(parent, callee);
            }
        }

        final var result = new int[n];
        for (int s = 0; s < n; s++) {
            result[s] = find(parent, s);
        }

        application = result;
        return result;
    }

    private static int find(final int[] parent, final int s) {
        int root = s;
        while (parent[root] != root) {
            root = parent[root];
        }

        return root;
    }

    private double[] applicationCriticality() {
        final double[] cached = applicationCriticality;
        if (cached != null) {
            return cached;
        }

        final int[] app = application();
        final double[] crit = criticality();
        final var result = new double[services.size()];
        for (int s = 0; s < result.length; s++) {
            result[app[s]] += crit[s];
        }

        applicationCriticality = result;
        return result;
    }

    private static double checkFraction(final double value, final String name) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException(name + " must be in [0, 1]");
        }

        return value;
    }
}
