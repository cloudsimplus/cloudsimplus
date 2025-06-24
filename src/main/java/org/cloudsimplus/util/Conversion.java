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
 * Utility class that provides a set of methods for general data conversion.
 *
 * @author Manoel Campos da Silva Filho
 * @see TimeUtil
 * @see MathUtil
 * @see BytesConversion
 */
public final class Conversion {
    /**
     * A value that represents 100% in a scale from 0 to 1.
     */
    public static final double HUNDRED_PERCENT = 1.0;

    /** One million in absolute value,
     * usually used to convert to and from
     * Number of Instructions (I) and Million Instructions (MI) units. */
    public static final int MILLION = 1_000_000;

    /**
     * A private constructor to avoid class instantiation.
     */
    private Conversion(){/**/}

    /**
     * Converts a boolean value to int
     * @param bool the boolean value to convert
     * @return 1 if the boolean value is true, 0 otherwise.
     */
    public static int boolToInt(final boolean bool){
        return bool ? 1 : 0;
    }
}
