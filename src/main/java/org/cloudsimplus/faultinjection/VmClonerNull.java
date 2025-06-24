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
package org.cloudsimplus.faultinjection;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A class that implements the Null Object Design Pattern for {@link VmCloner} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @see VmCloner#NULL
 */
final class VmClonerNull implements VmCloner {
    @Override public int getClonedVmsNumber() {
        return 0;
    }
    @Override public Map.Entry<Vm, List<Cloudlet>> clone(Vm sourceVm) { return new HashMap.SimpleEntry<>(Vm.NULL, Collections.EMPTY_LIST); }
    @Override public VmCloner setVmClonerFunction(UnaryOperator<Vm> vmClonerFunction) { return this; }
    @Override public VmCloner setCloudletsClonerFunction(Function<Vm, List<Cloudlet>> cloudletsClonerFunction) { return this; }
    @Override public int getMaxClonesNumber() {
        return 0;
    }
    @Override public boolean isMaxClonesNumberReached() { return false; }
    @Override public VmCloner setMaxClonesNumber(int maxClonesNumber) { return this; }
}
