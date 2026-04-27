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
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Service} implementation that load-balances incoming
 * {@link ServiceCall}s across its registered {@link Vm}s using a
 * <a href="https://en.wikipedia.org/wiki/Round-robin_scheduling">round-robin</a>
 * policy.
 *
 * <p>Subclasses may override {@link #selectVm()} to implement other strategies
 * (least-loaded, hash-based, sticky, etc.).</p>
 *
 * @since CloudSim Plus 9.0.0
 */
public class ServiceSimple implements Service {
    private long id;
    private final String name;
    private final List<Vm> vms;
    private int nextVmIndex;

    /**
     * Creates a service with no backing VMs yet.
     * @param name the service name (used for logging and request tracing)
     */
    public ServiceSimple(@NonNull final String name) {
        this.id = -1;
        this.name = name;
        this.vms = new ArrayList<>();
        this.nextVmIndex = 0;
    }

    /**
     * Creates a service backed by the given list of VMs.
     * @param name the service name
     * @param vms the initial pool of VMs hosting this service
     */
    public ServiceSimple(final String name, final List<? extends Vm> vms) {
        this(name);
        vms.forEach(this::addVm);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Service setId(final long id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Vm> getVms() {
        return Collections.unmodifiableList(vms);
    }

    @Override
    public Service addVm(@NonNull final Vm vm) {
        if (vm == Vm.NULL) {
            return this;
        }
        for (final Vm existing : vms) {
            if (existing == vm) {
                return this;
            }
        }
        vms.add(vm);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>Cyclically returns the next VM from the pool (round-robin).</p>
     */
    @Override
    public Vm selectVm() {
        if (vms.isEmpty()) {
            return Vm.NULL;
        }
        final var vm = vms.get(nextVmIndex % vms.size());
        nextVmIndex = (nextVmIndex + 1) % vms.size();
        return vm;
    }

    @Override
    public String toString() {
        return "Service[%s]".formatted(name);
    }
}
