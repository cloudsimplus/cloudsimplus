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
package org.cloudsimplus.services;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A top-level request that enters the system and triggers a {@link ServiceCall}
 * tree (e.g. an end-user HTTP request that fans out into A → B, A → D → E).
 *
 * <p>The {@link ServiceBroker} drives the request to completion by running the
 * cloudlets associated with each {@link ServiceCall} on its target service's VMs,
 * threading them together through cloudlet-finish listeners. The
 * {@link #getFinishTime() finish time} reflects when the root call (and thus
 * the whole subtree) completed.</p>
 *
 * @since CloudSim Plus 9.0.0
 */
@Getter @Setter
public class ServiceRequest {
    private long id;

    @NonNull
    private final ServiceCall rootCall;

    /**
     * Optional submission delay (in seconds): the broker will wait this long
     * after the simulation starts before firing the root call.
     */
    private double submissionDelay;

    /** When the broker received the request (set by the broker). */
    private double submissionTime = -1;

    /** When the root call started running on its VM. */
    private double startTime = -1;

    /** When the root call (and the whole tree) finished. */
    private double finishTime = -1;

    private final List<Object> tags = new ArrayList<>();

    /**
     * Creates a request whose entrypoint is the given root call.
     *
     * @param id       the request id (used for logging/tracing)
     * @param rootCall the entrypoint call
     */
    public ServiceRequest(final long id, @NonNull final ServiceCall rootCall) {
        this.id = id;
        this.rootCall = rootCall;
    }

    /**
     * @return the total response time (in seconds) of the request, or a negative
     * value if not finished yet.
     */
    public double getResponseTime() {
        if (submissionTime < 0 || finishTime < 0) {
            return -1;
        }
        return finishTime - submissionTime;
    }

    /**
     * @return {@code true} when the root call has completed.
     */
    public boolean isFinished() {
        return finishTime >= 0;
    }

    /**
     * @return read-only list of arbitrary tags attached to this request
     * (handy for tracing or grouping in scenarios).
     */
    public List<Object> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Attaches an arbitrary tag (e.g. a user id, a request kind) to this request.
     */
    public ServiceRequest addTag(final Object tag) {
        tags.add(tag);
        return this;
    }

    @Override
    public String toString() {
        return "ServiceRequest[id=%d, root=%s, finished=%s]"
            .formatted(id, rootCall.getService().getName(), isFinished());
    }
}
