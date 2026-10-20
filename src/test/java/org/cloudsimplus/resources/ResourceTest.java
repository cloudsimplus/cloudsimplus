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
package org.cloudsimplus.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ResourceTest {

    @Test
    public void testNullObject() {
        final ResourceManageable instance = ResourceManageable.NULL;
        assertAll(
            () -> assertFalse(instance.setCapacity(0)),
            () -> assertFalse(instance.allocateResource(0)),
            () -> assertFalse(instance.setAllocatedResource(0)),
            () -> assertFalse(instance.deallocateResource(0)),
            () -> assertEquals(0, instance.deallocateAllResources()),
            () -> assertFalse(instance.isResourceAmountBeingUsed(0)),
            () -> assertFalse(instance.isSuitable(0))
        );
    }

}
