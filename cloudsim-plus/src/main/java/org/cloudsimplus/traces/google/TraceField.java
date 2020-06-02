/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.traces.google;

/**
 * An interface to be implemented by {@link Enum}s representing
 * a field in a Google Trace File.
 * Each enum instance is used to get values from fields
 * of the trace in the correct generic type T and possibly making
 * some unit conversions (if required by the specific field
 * represented by the enum instance).
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public interface TraceField <R extends GoogleTraceReaderAbstract>{
    /**
     * Gets the value (from a line read from a trace file) of the field associated to the enum instance.
     * @param reader the reader for the trace file
     * @param <T> the type to convert the value read from the trace to
     * @return the field value converted to a specific type
     */
    <T> T getValue(R reader);
}
