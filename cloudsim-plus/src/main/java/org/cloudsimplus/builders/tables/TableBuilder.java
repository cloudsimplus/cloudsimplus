/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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

import java.util.List;

/**
 * An interface for classes that generate tables from a given data set,
 * following the Builder Design Pattern.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface TableBuilder {
    /**
     * Adds a new row to the list of rows containing the data to be printed.
     * @return
     */
    List<Object> newRow();

    /**
     * Adds a column to the table to be printed.
     *
     * @param columnTitle The title of the column to be added.
     * @return The created column.
     */
    TableColumn addColumn(final String columnTitle);

    /**
     * Adds a list of columns to the table to be printed, where the column data
     * will be printed without a specific format.
     *
     * @param columnTitles The titles of the columns
     * @return The {@link TableBuilder} instance.
     * @see #addColumn(String)
     */
    TableBuilder addColumnList(final String... columnTitles);

    /**
     *
     * @return the table title
     */
    String getTitle();

    /**
     *
     * @param title the table title to set
     * @return The TableBuilder instance
     */
    TableBuilder setTitle(final String title);

    /**
     * @return the list of columns of the table
     */
    List<TableColumn> getColumns();

    /**
     * Builds and prints the table.
     */
    void print();


}
