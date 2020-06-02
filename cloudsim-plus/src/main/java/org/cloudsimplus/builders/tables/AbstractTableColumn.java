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

import org.apache.commons.lang3.StringUtils;

/**
 * A column of a table to be generated using a {@link Table} class.
 * @author Manoel Campos da Silva Filho
 */
public abstract class AbstractTableColumn implements TableColumn {
    /**
     * @see #getTitle()
     */
    private String title;

    /**
     * @see #getSubTitle()
     */
    private String subTitle;

    /**
     * @see #getFormat()
     */
    private String format;

    /**
     * @see #getTable()
     */
    private Table table;

    /**
     * Creates a column with a specific title.
     * @param table The table that the column belongs to.
     * @param title The column title.
     */
    public AbstractTableColumn(final Table table, final String title) {
        this(table, title, "");
    }

    /**
     * Creates a column with a specific title and sub-title.
     * @param title The column title.
     * @param subTitle The column sub-title.
     */
    public AbstractTableColumn(final String title, final String subTitle) {
        this(null, title, subTitle);
    }

    /**
     * Creates a column with a specific title and sub-title for a given table.
     * @param title The column title.
     * @param subTitle The column sub-title.
     */
    public AbstractTableColumn(final Table table, final String title, final String subTitle) {
        this.table = table;
        this.title = title;
        this.setFormat("");
        this.subTitle = subTitle;
    }

    /**
     *
     * @return The title to be displayed at the top of the column.
     */
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public AbstractTableColumn setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     *
     * @return The subtitle to be displayed below the title of the column (optional).
     */
    @Override
    public String getSubTitle() {
        return subTitle;
    }

    @Override
    public AbstractTableColumn setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    /**
     *
     * @return The format to be used to display the content of the column,
     * according to the {@link String#format(java.lang.String, java.lang.Object...)} (optional).
     */
    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public final AbstractTableColumn setFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    /**
     *
     * @return The table that the column belongs to.
     */
    @Override
    public Table getTable() {
        return table;
    }


    @Override
    public AbstractTableColumn setTable(Table table) {
        this.table = table;
        return this;
    }

    /**
     * Generates the string that represents the data of the column,
     * formatted according to the {@link #getFormat() format}.
     * @param data The data of the column to be formatted
     * @return a string containing the formatted column data
     */
    @Override
    public String generateData(final Object data){
        if(StringUtils.isBlank(format)) {
            return String.valueOf(data);
        }

        return String.format(format, data);
    }

    /**
     * Generates a header for the column, either for the title or subtitle header.
     *
     * @param str header title or subtitle
     * @return the generated header string
     */
    protected abstract String generateHeader(String str);

    @Override
    public String generateTitleHeader() {
        return generateHeader(title);
    }

    @Override
    public String generateSubtitleHeader() {
        return generateHeader(subTitle);
    }

    /**
     *
     * @return The index of the current column into the
     * column list of the {@link #getTable() Table}.
     */
    protected int getIndex() {
        return table.getColumns().indexOf(this);
    }

    /**
     * Indicates if the current column is the last one
     * in the column list of the {@link #getTable() Table}.
     * @return true if it is the last column, false otherwise.
     */
    protected boolean isLastColumn() {
        return getIndex() == getTable().getColumns().size()-1;
    }
}
