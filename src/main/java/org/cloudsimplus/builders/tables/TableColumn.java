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

/**
 * An interface that represents a column of a table generated
 * using a {@link Table}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public sealed interface TableColumn permits AbstractTableColumn {
    /**
     * {@return the index of the column into the table}
     */
    int getIndex();

    /**
     * Generates the string that represents the data of the column,
     * formatted according to the {@link #getFormat() format}.
     * @param data the data of the column to be formatted
     * @return a string containing the formatted column data
     */
    String generateData(Object data);

    /**
     * Generates the string that represents the header of the column,
     * containing the column title.
     * @return the generated header string
     */
    String generateTitleHeader();

    /**
     * Generates the string that represents the sub-header of the column (if any),
     * containing the column subtitle.
     * @return the generated sub-header string
     */
    String generateSubtitleHeader();

    /**
     * @return the format to be used to display the content of the column,
     * according to the {@link String#format(java.lang.String, java.lang.Object...)} (optional).
     */
    String getFormat();

    /**
     * @return the subtitle to be displayed below the title of the column (optional).
     */
    String getSubTitle();

    /**
     * @return the table that the column belongs to.
     */
    Table getTable();

    /**
     * @return the title to be displayed at the top of the column.
     */
    String getTitle();

    /**
     * Sets the format to print the column data.
     * @param format the format to set
     * @return this column
     */
    TableColumn setFormat(String format);

    /**
     * Sets the subtitle of the table (optional).
     * @param subTitle the subtitle to set
     * @return this column
     */
    TableColumn setSubTitle(String subTitle);

    /**
     * Sets the table this column belongs to.
     * @param table the table to set
     * @return this column
     */
    TableColumn setTable(Table table);

    /**
     * Sets the title of the table (optional).
     * @param title the title to set
     * @return this column
     */
    TableColumn setTitle(String title);
}
