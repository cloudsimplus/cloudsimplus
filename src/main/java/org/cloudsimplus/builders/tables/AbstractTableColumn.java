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

import lombok.Getter;

import static java.util.Objects.requireNonNullElse;

/**
 * An abstract column of a table to be generated using a {@link Table} class.
 * @author Manoel Campos da Silva Filho
 */
@Getter
public non-sealed abstract class AbstractTableColumn implements TableColumn {
    /**
     * The title to be displayed at the top of the column.
     */
    private String title;

    /**
     * The subtitle to be displayed below the title of the column (optional).
     */
    private String subTitle;

    /**
     * The format to be used to display the content of the column,
     * according to the {@link String#format(java.lang.String, java.lang.Object...)} (optional).
     */
    private String format;

    /**
     * The table that the column belongs to.
     */
    private Table table;

    /**
     * Creates a column with a specific title.
     * @param table the table that the column belongs to.
     * @param title the column title.
     */
    public AbstractTableColumn(final Table table, final String title) {
        this(table, title, "");
    }

    /**
     * Creates a column with a specific title, subtitle and format.
     * @param title the column title.
     * @param subTitle the column subtitle.
     * @param format the column format.
     */
    public AbstractTableColumn(final String title, final String subTitle, final String format) {
        this.title = title;
        this.subTitle = subTitle;
        this.setFormat(format);
    }

    /**
     * Creates a column with a specific title and subtitle for a given table.
     * @param title the column title.
     * @param subTitle the column subtitle.
     */
    public AbstractTableColumn(final Table table, final String title, final String subTitle) {
        this(title, subTitle);
        this.table = table;
    }

    /**
     * Creates a column with a specific title and subtitle.
     * @param title the column title.
     * @param subTitle the column subtitle.
     */
    public AbstractTableColumn(final String title, final String subTitle) {
        this(title, subTitle, "");
    }

    @Override
    public AbstractTableColumn setTitle(final String title) {
        this.title = requireNonNullElse(title, "");
        return this;
    }

    @Override
    public AbstractTableColumn setSubTitle(final String subTitle) {
        this.subTitle = requireNonNullElse(subTitle, "");
        return this;
    }

    @Override
    public final AbstractTableColumn setFormat(String format) {
        this.format = requireNonNullElse(format, "");
        return this;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public AbstractTableColumn setTable(Table table) {
        this.table = table;
        return this;
    }

    /**
     * Generates the string that represents the data of the column,
     * formatted according to the {@link #getFormat() format}.
     * @param data the data of the column to be formatted
     * @return a string containing the formatted column data
     */
    @Override
    public String generateData(final Object data){
        if(format.isBlank()) {
            return String.valueOf(data);
        }

        return format.formatted(data);
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

    @Override
    public int getIndex() {
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
