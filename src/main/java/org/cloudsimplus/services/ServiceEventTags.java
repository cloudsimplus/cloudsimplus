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

import org.cloudsimplus.core.CloudSimTag;

/**
 * Custom event tags used by the {@link ServiceBrokerSimple} to drive the
 * service call graph. Stays well above the
 * {@link CloudSimTag CloudSim reserved range} (&lt; 300 and 9600).
 */
public final class ServiceEventTags {
    private ServiceEventTags() {}

    /**
     * A self-event sent by the broker to advance a {@link ServiceCall} after a pure
     * network/delay step that has no associated cloudlet. The event payload is an
     * internal record.
     */
    public static final int CALL_ADVANCE = 9700;
}
