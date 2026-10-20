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
package org.cloudsimplus.power.models;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeasurement;

/// A class that implements the Null Object Design Pattern for [PowerModelHost] objects.
/// @since CloudSim Plus 6.0.0
final class PowerModelHostNull implements PowerModelHost {
    @Override public PowerMeasurement getPowerMeasurement() { return new PowerMeasurement(); }
    @Override public double getPower(double utilizationFraction) { return 0; }
    @Override public PowerModelHost setStartupPower(double power) { return this; }
    @Override public PowerModelHost setShutDownPower(double power) { return this; }
    @Override public void addStartupTotals() {/**/}
    @Override public void addShutDownTotals() {/**/}
    @Override public Host getHost() { return Host.NULL; }
    @Override public double getStartupPower() { return 0; }
    @Override public double getShutDownPower() { return 0; }
    @Override public double getTotalStartupPower() { return 0; }
    @Override public double getTotalShutDownPower() { return 0; }
    @Override public double getTotalStartupTime() { return 0; }
    @Override public double getTotalShutDownTime()  { return 0; }
    @Override public int getTotalStartups() { return 0; }
    @Override public PowerModelHost setHost(Host host) { return this; }
}
