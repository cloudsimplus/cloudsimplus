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

import java.util.List;

/**
 * An interface for classes that generate tables from a given data set to show simulation results,
 * following the Builder Design Pattern.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public sealed interface Table permits AbstractTable {
    /**
     * Adds a new empty row to the list of rows, so that data can be added to the row further.
     * @return the new empty row
     */
    List<Object> newRow();

    /**
     * Creates a column with a given title.
     * The created column is not added to the table.
     *
     * @param title The title of the column to create.
     * @return The created column
     * @see #newColumn(String, String)
     * @see #newColumn(String, String, String)
     */
    TableColumn newColumn(String title);

    /**
     * Creates a column with a given title and subtitle.
     * The created column is not added to the table.
     *
     * @param title The title of the column to be added.
     * @param subTitle The subtitle of the column to be added.
     * @return the created column
     * @see #newColumn(String)
     * @see #newColumn(String, String, String)
     */
    TableColumn newColumn(String title, String subTitle);

    /**
     * Cretes a column with a given title, subtitle and format.
     * The created column is not added to the table.
     *
     * @param title The title of the column to be added.
     * @param subtitle The subtitle of the column to be added.
     * @param format format to print the column data
     * @return the created column
     * @see #newColumn(String)
     * @see #newColumn(String, String)
     */
    TableColumn newColumn(String title, String subtitle, String format);

    /**
     * Adds a list of columns (with given titles) to the end of the
     * table's columns to be printed, where the column data
     * will be printed without a specific format.
     *
     * @param columnTitles The titles of the columns
     * @return the {@link Table} instance.
     * @see #newColumn(String)
     */
    Table addColumnList(String... columnTitles);

    /**
     * {@return the table title}
     */
    String getTitle();

    /**
     * {@return the table instance}
     * @param title the table title to set
     */
    Table setTitle(String title);

    /**
     * {@return the list of columns of the table}
     */
    List<TableColumn> getColumns();

    /**
     * {@return the number of columns}
     */
    int colCount();

    /**
     * {@return the string used to separate one column from another (optional)}
     */
    String getColumnSeparator();

    /**
     * Sets the string used to separate one column from another.
     * It's optional to set a column separator.
     * @param columnSeparator the separator
     * @return this table instance
     */
    Table setColumnSeparator(String columnSeparator);

    /**
     * Prints the table.
     */
    void print();
}
