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
package org.cloudsimplus.traces;

import org.cloudbus.cloudsim.util.TraceReaderAbstract;

import java.io.InputStream;
import java.util.Objects;

/**
 * An abstract class providing additional features for subclasses implementing trace file
 * readers for specific file formats.
 *
 * <p>Check important details at {@link TraceReaderAbstract}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public abstract class TraceReaderBase extends TraceReaderAbstract {
    /**
     * Regular expression to check if a String corresponds to an integer number.
     */
    private static final String INT_REGEX = "^-?\\d+$";

    /** @see #getLastParsedLineArray() */
    private String[] lastParsedLineArray;

    protected TraceReaderBase(final String filePath, final InputStream reader) {
        super(filePath, reader);
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as String.
     *
     * @param field a enum value representing the index of the field to get the value
     * @return
     */
    protected <T extends Enum> String getFieldValue(final T field){
        return lastParsedLineArray[field.ordinal()];
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as double.
     *
     * @param field a enum value representing the index of the field to get the value
     * @return
     */
    protected <T extends Enum> double getFieldDoubleValue(final T field){
        return Double.parseDouble(getFieldValue(field));
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as double.
     *
     * @param field a enum value representing the index of the field to get the value
     * @param defaultValue the default value to be returned if the field value is not a number
     * @return
     */
    protected <T extends Enum> double getFieldDoubleValue(final T field, final double defaultValue){
        final String value = getFieldValue(field);
        return  value.matches("^-?\\d+(\\.?\\d+)?$") ? Double.parseDouble(value) : defaultValue;
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as an int.
     *
     * @param field a enum value representing the index of the field to get the value
     * @return
     */
    protected <T extends Enum> int getFieldIntValue(final T field){
        return Integer.parseInt(getFieldValue(field));
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as an int.
     *
     * @param field a enum value representing the index of the field to get the value
     * @param defaultValue the default value to be returned if the field value is not an int
     * @return
     */
    protected <T extends Enum> int getFieldIntValue(final T field, final int defaultValue){
        final String value = getFieldValue(field);
        return  value.matches(INT_REGEX) ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as an int.
     *
     * @param field a enum value representing the index of the field to get the value
     * @return
     */
    protected <T extends Enum> long getFieldLongValue(final T field){
        return Long.parseLong(getFieldValue(field));
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as an int.
     *
     * @param field a enum value representing the index of the field to get the value
     * @param defaultValue the default value to be returned if the field value is not an int
     * @return
     */
    protected <T extends Enum> long getFieldLongValue(final T field, final long defaultValue){
        final String value = getFieldValue(field);
        return  value.matches(INT_REGEX) ? Long.parseLong(value) : defaultValue;
    }

    /**
     * Gets an array containing the field values from the last parsed trace line.
     * @return
     */
    protected String[] getLastParsedLineArray() {
        return lastParsedLineArray;
    }

    /**
     * Sets an array containing the field values from the last parsed trace line.
     * @param lastParsedLineArray the field values from the last parsed trace line
     */
    protected void setLastParsedLineArray(final String[] lastParsedLineArray) {
        this.lastParsedLineArray = Objects.requireNonNull(lastParsedLineArray);
    }
}
