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
package org.cloudsimplus.util;

/**
 * Utility class that provides a set of methods for power/energy conversion.
 *
 * @author Manoel Campos da Silva Filho
 * @see TimeUtil
 * @see MathUtil
 * @see BytesConversion
 */
public final class PowerConversion {
    private static final double KILO = 1000;
    private static final double MEGA = KILO * KILO;
    private static final double GIGA = MEGA * KILO;
    private static final double TERA = GIGA * KILO;

    /**
     * A private constructor to avoid class instantiation.
     */
    private PowerConversion(){/**/}

    /**
     * Converts watt-seconds (joule) to kWh.
     * @param power the power in Ws
     * @return the power in kWh
     */
    public static double wattSecondsToKWattHours(final double power) {
        return power * KILO / 3600.0;
    }

    public static double wattsToKilo(final double watts){ return watts / KILO; }

    public static double wattsToMega(final double watts){ return watts / MEGA; }

    public static double wattsToGiga(final double watts){ return watts / GIGA; }

    public static double wattsToTera(final double watts){ return watts / TERA; }

    public static double megaToGiga(final double mega){ return mega / KILO; }

    public static double megaToTera(final double mega){
        return mega / MEGA;
    }

    public static double gigaToMega(final double giga){
        return giga * KILO;
    }

    public static double teraToMega(final double tera){
        return teraToGiga(tera) * KILO;
    }

    public static double teraToGiga(final double tera){
        return tera * KILO;
    }
}
