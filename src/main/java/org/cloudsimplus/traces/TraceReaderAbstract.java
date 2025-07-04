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
package org.cloudsimplus.traces;

import lombok.NonNull;
import lombok.experimental.Accessors;

/// An abstract class to implement trace file readers for specific file formats.
///
/// ## NOTES
///
/// - This class can only read trace files in the following format: **ASCII text, zip, gz.**
/// - If you need to load multiple trace files, create multiple instances of this class.
/// - If the size of the trace reader is huge or contains lots of traces, please
///   increase the JVM heap size accordingly by using **java -Xmx** option
///   when running the simulation. For instance, you can use **java -Xmx200M**
///   to define the JVM heap size will be 200MB.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
@Accessors
public abstract non-sealed class TraceReaderAbstract extends FileReader implements TraceReader {

    /**
     * Regular expression to check if a String corresponds to an integer number.
     */
    private static final String INT_REGEX = "^-?\\d+$";

    /** @see #getLastParsedLineArray() */
    private String[] lastParsedLineArray;

    /**
     * Create a TraceReader object.
     *
     * @param filePath the workload trace file path in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @throws IllegalArgumentException when the workload trace file name is null or empty; or the resource PE mips is less or equal to 0
     * @throws IllegalArgumentException when the workload trace file name is null or empty
     */
    public TraceReaderAbstract(@NonNull final String filePath) {
        super(filePath);
        if (filePath.isBlank()) {
            throw new IllegalArgumentException("Trace file name cannot be blank.");
        }

        this.setMaxLinesToRead(Integer.MAX_VALUE);
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as String.
     *
     * @param field an enum value representing the index of the field to get the value
     * @return the field value as String
     */
    public <T extends Enum> String getFieldValue(final T field){
        return lastParsedLineArray[field.ordinal()];
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as double.
     *
     * @param field an enum value representing the index of the field to get the value
     * @return the field value as double
     */
    public <T extends Enum> double getFieldDoubleValue(final T field){
        return Double.parseDouble(getFieldValue(field));
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as double.
     *
     * @param field an enum value representing the index of the field to get the value
     * @param defaultValue the default value to be returned if the field value is not a number
     * @return the field value as double
     */
    public <T extends Enum> double getFieldDoubleValue(final T field, final double defaultValue){
        final String value = getFieldValue(field);
        return  value.matches("^-?\\d+(\\.?\\d+)?$") ? Double.parseDouble(value) : defaultValue;
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as an int.
     *
     * @param field an enum value representing the index of the field to get the value
     * @return the field value as int
     */
    public <T extends Enum> int getFieldIntValue(final T field){
        return Integer.parseInt(getFieldValue(field));
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as an int.
     *
     * @param field an enum value representing the index of the field to get the value
     * @param defaultValue the default value to be returned if the field value is not an int
     * @return the field value as int
     */
    public <T extends Enum> int getFieldIntValue(final T field, final int defaultValue){
        final String value = getFieldValue(field);
        return  value.matches(INT_REGEX) ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as an int.
     *
     * @param field an enum value representing the index of the field to get the value
     * @return the field value as long
     */
    public <T extends Enum> long getFieldLongValue(final T field){
        return Long.parseLong(getFieldValue(field));
    }

    /**
     * Gets a field's value from the {@link #getLastParsedLineArray() last parsed line} as an int.
     *
     * @param field an enum value representing the index of the field to get the value
     * @param defaultValue the default value to be returned if the field value is not an int
     * @return the field value as long
     */
    public <T extends Enum> long getFieldLongValue(final T field, final long defaultValue){
        final String value = getFieldValue(field);
        return  value.matches(INT_REGEX) ? Long.parseLong(value) : defaultValue;
    }

    /**
     * @return an array containing the field values from the last parsed trace line.
     */
    protected String[] getLastParsedLineArray() {
        return lastParsedLineArray;
    }

    /**
     * Sets an array containing the field values from the last parsed trace line.
     * @param lastParsedLineArray the field values from the last parsed trace line
     */
    protected void setLastParsedLineArray(@NonNull final String[] lastParsedLineArray) {
        this.lastParsedLineArray = lastParsedLineArray;
    }
}
