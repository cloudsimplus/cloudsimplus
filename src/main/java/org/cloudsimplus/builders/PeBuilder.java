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
package org.cloudsimplus.builders;

import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A Builder class to create {@link Pe} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class PeBuilder implements Builder {
    private Function<Double, Pe> peSupplier;

    public PeBuilder(){
        peSupplier = mips -> new PeSimple(mips, new PeProvisionerSimple());
    }

    public List<Pe> create(final int amount, final double peMips) {
        validateAmount(amount);
        final List<Pe> peList = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            peList.add(peSupplier.apply(peMips));
        }
        return peList;
    }

    /**
     * Sets a {@link Function} that is accountable to create {@link Pe}
     * by this builder.
     * The  {@link Function} receives the MIPS for each PE.
     *
     * @param peSupplier
     */
    public void setPeSupplier(final Function<Double, Pe> peSupplier) {
        this.peSupplier = Objects.requireNonNull(peSupplier);
    }
}
