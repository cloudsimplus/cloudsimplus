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
package org.cloudsimplus.allocationpolicies;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.TopologyAwareHost;
import org.cloudsimplus.vms.Vm;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link VmAllocationPolicy} that mimics a Kubernetes-style filter-and-score
 * scheduler over {@link TopologyAwareHost}s. The active {@link Policy}
 * controls how candidate hosts are scored after the suitability filter.
 *
 * <p>VMs are grouped into <em>replica sets</em> via
 * {@link #setReplicaSetOf(Function)}. Anti-affinity and spread policies use
 * the replica-set membership to evaluate co-location.</p>
 *
 * <p>Latency-aware placement uses {@link #setPeersOf(Function)} to discover
 * the peer VMs the candidate VM will talk to (typically the other VMs of its
 * service or its upstream/downstream services in the call graph).</p>
 */
@Getter @Setter
public class VmAllocationPolicyTopologyAware extends VmAllocationPolicyAbstract {

    public enum Policy {
        LATENCY_AWARE, COST_OPTIMIZED, AVAILABILITY_ZONE_SPREAD,
        RACK_ANTI_AFFINITY, GEOGRAPHIC_SPREAD
    }

    @NonNull
    private Policy policy = Policy.COST_OPTIMIZED;

    /** Resolves the replica-set tag of a VM. Empty string → not part of a replica set. */
    @NonNull
    private Function<Vm, String> replicaSetOf = vm -> "";

    /** Resolves the peers a VM will communicate with (used by LATENCY_AWARE). */
    @NonNull
    private Function<Vm, Collection<Vm>> peersOf = vm -> Collections.emptyList();

    public VmAllocationPolicyTopologyAware() {
        super();
    }

    public VmAllocationPolicyTopologyAware(final Policy policy) {
        super();
        this.policy = policy;
    }

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        // 1) FILTER: capacity + strict topological constraints
        final List<Host> candidates = getHostList().stream()
            .filter(h -> h.getSuitabilityFor(vm).fully())
            .filter(h -> passesStrictConstraints(vm, h))
            .toList();

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        // 2) SCORE: lower score wins
        return candidates.stream().min(Comparator.comparingDouble(h -> score(vm, h)));
    }

    /**
     * Strict (hard) filters that must be satisfied regardless of scoring.
     * Currently enforces RACK_ANTI_AFFINITY: no two VMs of the same replica set
     * may share a rack.
     */
    private boolean passesStrictConstraints(final Vm vm, final Host host) {
        if (policy != Policy.RACK_ANTI_AFFINITY || !(host instanceof TopologyAwareHost h)) {
            return true;
        }

        final String replicaSet = replicaSetOf.apply(vm);
        if (replicaSet.isEmpty()) {
            return true;
        }

        return placedHosts(replicaSet).values().stream()
            .filter(other -> other instanceof TopologyAwareHost)
            .map(other -> ((TopologyAwareHost) other).getRackId())
            .noneMatch(rack -> rack.equals(h.getRackId()));
    }

    private double score(final Vm vm, final Host host) {
        return switch (policy) {
            case COST_OPTIMIZED            -> costScore(host);
            case LATENCY_AWARE             -> latencyScore(vm, host);
            case AVAILABILITY_ZONE_SPREAD  -> spreadScore(vm, host, TopologyAwareHost::getAvailabilityZone);
            case GEOGRAPHIC_SPREAD         -> spreadScore(vm, host, TopologyAwareHost::getRegion);
            case RACK_ANTI_AFFINITY        -> spreadScore(vm, host, TopologyAwareHost::getRackId);
        };
    }

    private double costScore(final Host host) {
        return host instanceof TopologyAwareHost h ? h.getCostPerHour() : Double.MAX_VALUE;
    }

    private double latencyScore(final Vm vm, final Host host) {
        if (!(host instanceof TopologyAwareHost h)) {
            return Double.MAX_VALUE;
        }
        double total = 0.0;
        for (final Vm peer : peersOf.apply(vm)) {
            if (peer.isCreated() && peer.getHost() != Host.NULL) {
                total += h.latencyTo(peer.getHost());
            }
        }
        return total;
    }

    /**
     * Counts how many VMs of {@code vm}'s replica set already live in the same
     * topology bucket (AZ, region, or rack) as {@code host}. The fewer, the better.
     */
    private double spreadScore(final Vm vm, final Host host,
                               final Function<TopologyAwareHost, String> bucketOf) {
        if (!(host instanceof TopologyAwareHost h)) {
            return Double.MAX_VALUE;
        }
        final String replicaSet = replicaSetOf.apply(vm);
        if (replicaSet.isEmpty()) {
            return 0.0;
        }
        final String myBucket = bucketOf.apply(h);
        return placedHosts(replicaSet).values().stream()
            .filter(other -> other instanceof TopologyAwareHost)
            .map(other -> bucketOf.apply((TopologyAwareHost) other))
            .filter(myBucket::equals)
            .count();
    }

    /** Hosts that already run a VM of the given replica set in this datacenter. */
    private Map<Long, Host> placedHosts(final String replicaSet) {
        final Map<Long, Host> out = new HashMap<>();
        for (final Host h : getHostList()) {
            for (final Vm running : h.getVmList()) {
                if (replicaSet.equals(replicaSetOf.apply(running))) {
                    out.put(h.getId(), h);
                }
            }
        }
        return out.isEmpty() ? Collections.emptyMap() : out;
    }
}
