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

import lombok.NonNull;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Drives a tree of {@link ServiceCall}s to completion on top of the regular
 * cloudlet/VM machinery.
 *
 * <p>For each call in the graph, the broker generates up to two cloudlets:
 * a <i>pre</i>-cloudlet ({@link ServiceCall#getLengthBeforeCalls()} MI) that
 * runs on the call's target service before any child is invoked, and a
 * <i>post</i>-cloudlet ({@link ServiceCall#getLengthAfterCalls()} MI) that runs
 * after all children have returned. Cloudlets with length 0 are skipped.</p>
 *
 * <p>The chain itself is implemented through cloudlet finish-listeners: when a
 * pre-cloudlet finishes, the broker either fires the first child (or the
 * post-cloudlet, if there are no children); when a child completes, the broker
 * fires the next sibling; and when the post-cloudlet finishes, the broker
 * notifies the parent so it can advance.</p>
 *
 * <p>Inter-service network latency can be modelled per call via
 * {@link ServiceCall#setNetworkDelay(double)}, which is mapped onto the cloudlet's
 * {@link Cloudlet#getSubmissionDelay() submission delay}.</p>
 *
 * @since CloudSim Plus 9.0.0
 */
public class ServiceBrokerSimple extends DatacenterBrokerSimple implements ServiceBroker {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceBrokerSimple.class.getSimpleName());

    private final List<Service> services = new ArrayList<>();
    private final List<ServiceRequest> requests = new ArrayList<>();
    private final List<ServiceRequest> pendingFireOnStart = new ArrayList<>();
    private long nextRequestId;

    public ServiceBrokerSimple(final CloudSimPlus simulation) {
        super(simulation);
    }

    public ServiceBrokerSimple(final CloudSimPlus simulation, final String name) {
        super(simulation, name);
    }

    // -------------- ServiceBroker API --------------

    @Override
    public ServiceBroker addService(@NonNull final Service service) {
        if (service == Service.NULL || services.contains(service)) {
            return this;
        }
        if (service.getId() < 0) {
            service.setId(services.size());
        }
        services.add(service);
        return this;
    }

    @Override
    public List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    @Override
    public ServiceBroker submitRequest(@NonNull final ServiceRequest request) {
        if (request.getId() < 0) {
            request.setId(nextRequestId++);
        }
        request.setSubmissionTime(getSimulation().clock());
        requests.add(request);

        if (isStarted()) {
            scheduleRequestStart(request);
        } else {
            pendingFireOnStart.add(request);
        }
        return this;
    }

    @Override
    public ServiceBroker submitRequests(final List<ServiceRequest> reqs) {
        reqs.forEach(this::submitRequest);
        return this;
    }

    @Override
    public List<ServiceRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    @Override
    public List<ServiceRequest> getFinishedRequests() {
        return requests.stream().filter(ServiceRequest::isFinished).toList();
    }

    // -------------- Lifecycle hooks --------------

    @Override
    public void startInternal() {
        super.startInternal();
        // Pending requests get fired only after VMs are created. We register a one-shot listener
        // that drains the pending queue as soon as all submitted VMs are up.
        addOnVmsCreatedListener(info -> drainPendingRequests());
    }

    private void drainPendingRequests() {
        if (pendingFireOnStart.isEmpty()) {
            return;
        }
        final var snapshot = new ArrayList<>(pendingFireOnStart);
        pendingFireOnStart.clear();
        snapshot.forEach(this::scheduleRequestStart);
    }

    private void scheduleRequestStart(final ServiceRequest req) {
        // Apply the request-level submissionDelay (e.g. arrival time) plus any per-call
        // network delay on the root call. The root call is then fired exactly like any
        // other call, attached to itself as a synthetic parent-less node.
        fireCall(req.getRootCall(), null, req, req.getSubmissionDelay());
    }

    // -------------- Call execution engine --------------

    /**
     * Fires {@code call} as part of {@code request}, using {@code parent} as its parent
     * in the runtime call tree. {@code extraDelay} is added to whatever {@link ServiceCall#getNetworkDelay()}
     * already specifies (used for the root call's submission delay).
     */
    private void fireCall(final ServiceCall call, final ServiceCall parent,
                          final ServiceRequest request, final double extraDelay) {
        call.setParent(parent);
        call.setRequest(request);

        final Vm vm = call.getService().selectVm();
        if (vm == Vm.NULL) {
            LOG.warn("{}: {}: No VM available for service '{}' (request {}). Aborting call.",
                getSimulation().clockStr(), getName(), call.getService().getName(), request.getId());
            completeCall(call); // best-effort: mark as completed and unwind
            return;
        }
        call.setAssignedVm(vm);

        if (call.isRoot()) {
            request.setStartTime(getSimulation().clock() + extraDelay);
        }

        runPrePhase(call, extraDelay);
    }

    private void runPrePhase(final ServiceCall call, final double extraDelay) {
        call.setState(ServiceCall.State.RUNNING_PRE);
        call.setStartTime(getSimulation().clock());

        final long len = call.getLengthBeforeCalls();
        final double delay = extraDelay + Math.max(call.getNetworkDelay(), 0);

        if (len <= 0 && delay <= 0) {
            // No pre-work and no delay: short-circuit straight to children/post.
            afterPrePhase(call);
            return;
        }

        if (len <= 0) {
            // Pure delay (network hop) with no compute: schedule a self-event.
            schedule(delay, ServiceEventTags.CALL_ADVANCE, new CallAdvance(call, Phase.AFTER_PRE));
            return;
        }

        final var pre = newCloudlet(call, len, call.getRequestBytes(), 1);
        if (delay > 0) {
            pre.setSubmissionDelay(delay);
        }
        call.setPreCloudlet(pre);
        pre.addOnFinishListener(info -> handleCloudletFinish(call, Phase.AFTER_PRE));
        submitCloudlet(pre);
    }

    private void runPostPhase(final ServiceCall call) {
        call.setState(ServiceCall.State.RUNNING_POST);
        final long len = call.getLengthAfterCalls();
        if (len <= 0) {
            completeCall(call);
            return;
        }

        final var post = newCloudlet(call, len, 1, call.getResponseBytes());
        call.setPostCloudlet(post);
        post.addOnFinishListener(info -> handleCloudletFinish(call, Phase.AFTER_POST));
        submitCloudlet(post);
    }

    /**
     * Called after the pre-cloudlet of {@code call} finishes.
     * Either dispatches the first child or jumps straight to the post-phase.
     */
    private void afterPrePhase(final ServiceCall call) {
        if (call.getChildren().isEmpty()) {
            runPostPhase(call);
        } else {
            call.setState(ServiceCall.State.WAITING_CHILD);
            call.setCurrentChildIndex(0);
            fireCall(call.getChildren().get(0), call, call.getRequest(), 0);
        }
    }

    /**
     * Called when a child of {@code parent} finishes. Advances to the next child
     * or runs the post-phase if there are no more.
     */
    private void onChildCompleted(final ServiceCall parent) {
        final int next = parent.getCurrentChildIndex() + 1;
        if (next < parent.getChildren().size()) {
            parent.setCurrentChildIndex(next);
            fireCall(parent.getChildren().get(next), parent, parent.getRequest(), 0);
        } else {
            runPostPhase(parent);
        }
    }

    private void completeCall(final ServiceCall call) {
        call.setState(ServiceCall.State.COMPLETED);
        call.setFinishTime(getSimulation().clock());

        if (call.isRoot()) {
            final var req = call.getRequest();
            req.setFinishTime(getSimulation().clock());
            LOG.info("{}: {}: Request {} (root service '{}') finished. Response time: {}s.",
                getSimulation().clockStr(), getName(), req.getId(),
                call.getService().getName(), formatTime(req.getResponseTime()));
            return;
        }
        onChildCompleted(call.getParent());
    }

    /**
     * Centralised dispatcher invoked whenever a cloudlet generated by this broker finishes.
     */
    private void handleCloudletFinish(final ServiceCall call, final Phase phase) {
        switch (phase) {
            case AFTER_PRE  -> afterPrePhase(call);
            case AFTER_POST -> completeCall(call);
        }
    }

    // -------------- Process incoming self-events --------------

    @Override
    public void processEvent(final SimEvent evt) {
        if (evt.getTag() == ServiceEventTags.CALL_ADVANCE && evt.getData() instanceof CallAdvance ca) {
            handleCloudletFinish(ca.call(), ca.phase());
            return;
        }
        super.processEvent(evt);
    }

    // -------------- Helpers --------------

    private Cloudlet newCloudlet(final ServiceCall call, final long lengthMI,
                                 final long fileSize, final long outputSize) {
        final var c = new CloudletSimple(lengthMI, call.getPesNumber());
        c.setFileSize(Math.max(1, fileSize));
        c.setOutputSize(Math.max(1, outputSize));
        c.setUtilizationModelCpu(new UtilizationModelFull());
        c.setVm(call.getAssignedVm());
        return c;
    }

    private static String formatTime(final double seconds) {
        return seconds < 0 ? "n/a" : "%.4f".formatted(seconds);
    }

    private enum Phase { AFTER_PRE, AFTER_POST }

    private record CallAdvance(ServiceCall call, Phase phase) {}
}
