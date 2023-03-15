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
package org.cloudsimplus.autoscaling;

import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.vms.Vm;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A class that implements the Null Object Design Pattern for {@link HorizontalVmScaling}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see HorizontalVmScaling#NULL
 */
final class HorizontalVmScalingNull implements HorizontalVmScaling {
    @Override public Supplier<Vm> getVmSupplier() {
        return () -> Vm.NULL;
    }
    @Override public HorizontalVmScaling setVmSupplier(Supplier<Vm> supplier) { return this; }
    @Override public boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt) {
        return false;
    }
    @Override public Predicate<Vm> getOverloadPredicate() { return vm -> false; }
    @Override public HorizontalVmScaling setOverloadPredicate(Predicate<Vm> predicate) { return this; }
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public VmScaling setVm(Vm vm) { return this; }
}
