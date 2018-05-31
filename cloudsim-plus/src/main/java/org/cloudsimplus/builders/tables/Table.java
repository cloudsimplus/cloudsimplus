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
package org.cloudsimplus.builders.tables;

import java.util.List;

/**
 * An interface for classes that generate tables from a given data set,
 * following the Builder Design Pattern.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Table {
    /**
     * Adds a new row to the list of rows containing the data to be printed.
     * @return
     */
    List<Object> newRow();

    /**
     * Adds a column with a given to the end of the table's columns to be printed.
     *
     * @param columnTitle The title of the column to be added.
     * @return The created column.
     */
    TableColumn addColumn(String columnTitle);

    /**
     * Adds a column with a given title to the end of the table's columns to be printed.
     *
     * @param index the position to insert the column into the column's list
     * @param columnTitle The title of the column to be added.
     * @return the created column
     */
    TableColumn addColumn(int index, String columnTitle);

    /**
     * Adds a column with a given title and sub-title to the end of the table's columns to be printed.
     *
     * @param columnTitle The title of the column to be added.
     * @param columnSubTitle The sub-title of the column to be added.
     * @return the created column
     */
    TableColumn addColumn(String columnTitle, String columnSubTitle);

    /**
     * Adds a column object to a specific position of the table's columns to be printed.
     *
     * @param index the position to insert the column into the column's list
     * @param column The column to be added.
     * @return the created column
     */
    TableColumn addColumn(int index, TableColumn column);

    /**
     * Adds a column object to the end of the table's columns to be printed.
     *
     * @param column The column to be added.
     * @return the created column
     */
    TableColumn addColumn(TableColumn column);

    /**
     * Adds a list of columns (with given titles) to the end of the
     * table's columns to be printed, where the column data
     * will be printed without a specific format.
     *
     * @param columnTitles The titles of the columns
     * @return the {@link Table} instance.
     * @see #addColumn(String)
     */
    Table addColumnList(String... columnTitles);

    /**
     *
     * @return the table title
     */
    String getTitle();

    /**
     *
     * @param title the table title to set
     * @return The Table instance
     */
    Table setTitle(String title);

    /**
     * @return the list of columns of the table
     */
    List<TableColumn> getColumns();

    /**
     * Gets the string used to separate one column from another (optional).
     * @return
     */
    String getColumnSeparator();

    /**
     * Sets the string used to separate one column from another (optional).
     * @param columnSeparator the separator to set
     * @return
     */
    Table setColumnSeparator(String columnSeparator);

    /**
     * Prints the table.
     */
    void print();
}
