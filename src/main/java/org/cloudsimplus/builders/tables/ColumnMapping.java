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
package org.cloudsimplus.builders.tables;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A record that creates a mapping for adding a column into a table later on.
 * That is used by {@link TableBuilderAbstract} objects.
 *
 * @param <T>          the type of objects printed into the table
 * @param col          the column to add
 * @param dataFunction a function that receives an object T and returns the data to be printed from that object
 * @param index        the index of the column in the table
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.3.1
 */
record ColumnMapping<T>(TableColumn col, Function<T, Object> dataFunction, int index) {
    ColumnMapping(TableColumn col, Function<T, Object> dataFunction) {
        this(col, dataFunction, Integer.MAX_VALUE);
    }

    ColumnMapping {
        requireNonNull(col);
        requireNonNull(dataFunction);
    }

    public Object getColData(final T object){
        return dataFunction.apply(object);
    }
}
