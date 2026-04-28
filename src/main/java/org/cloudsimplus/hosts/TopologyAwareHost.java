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
package org.cloudsimplus.hosts;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.resources.Pe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link HostSimple} extended with topology and economic metadata used by
 * {@code VmAllocationPolicyTopologyAware}: rack id, availability zone,
 * geographic region, hourly cost, and a per-host latency table.
 *
 * <p>The latency table maps a peer host id to a one-way latency in milliseconds.
 * It is intentionally sparse — missing entries default to
 * {@link #DEFAULT_INTER_REGION_LATENCY_MS} when computing distance to a peer
 * in another region, otherwise {@link #DEFAULT_INTRA_REGION_LATENCY_MS}.</p>
 */
@Getter @Setter @Accessors(chain = true)
public class TopologyAwareHost extends HostSimple {
    public static final double DEFAULT_INTRA_REGION_LATENCY_MS = 1.0;
    public static final double DEFAULT_INTER_REGION_LATENCY_MS = 80.0;

    /** Identifier of the rack this host is mounted on (e.g. {@code "rack-A12"}). */
    private String rackId = "";

    /** Identifier of the availability zone (e.g. {@code "us-east-1a"}). */
    private String availabilityZone = "";

    /** Geographic region (e.g. {@code "us-east"}, {@code "eu-west"}). */
    private String region = "";

    /** Cost in arbitrary currency units per simulated hour of usage. */
    private double costPerHour;

    /** Sparse latency table: peer host id → one-way latency in ms. */
    private final Map<Long, Double> latencyToHost = new HashMap<>();

    public TopologyAwareHost(final List<Pe> peList) {
        super(peList);
    }

    public TopologyAwareHost(final long ram, final long bw, final long storage, final List<Pe> peList) {
        super(ram, bw, storage, peList);
    }

    /**
     * Returns the latency from this host to the given peer.
     * Falls back to a region-based default when no explicit entry is set.
     */
    public double latencyTo(final Host peer) {
        if (peer == this || peer == null || peer == Host.NULL) {
            return 0.0;
        }

        final Double explicit = latencyToHost.get(peer.getId());
        if (explicit != null) {
            return explicit;
        }

        if (peer instanceof TopologyAwareHost t && !region.isEmpty() && region.equals(t.region)) {
            return DEFAULT_INTRA_REGION_LATENCY_MS;
        }
        return DEFAULT_INTER_REGION_LATENCY_MS;
    }

    public TopologyAwareHost putLatency(final long peerHostId, final double ms) {
        latencyToHost.put(peerHostId, ms);
        return this;
    }
}
