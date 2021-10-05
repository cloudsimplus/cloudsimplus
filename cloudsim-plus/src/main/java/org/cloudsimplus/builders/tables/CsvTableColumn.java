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
 * A column of an CSV table. The class generates the CSV code
 * that represents a column in a CSV table.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CsvTableColumn extends AbstractTableColumn {
    /**
     * A format used to print data followed by the column separator.
     */
    public static final String DATA_COL_SEPARATOR_FORMAT = "%s%s";

    public CsvTableColumn(final String title, final String subTitle) {
        this(null, title, subTitle);
    }

    public CsvTableColumn(final String title) {
        this(null, title, "");
    }

    public CsvTableColumn(final Table table, final String title, final String subTitle) {
        super(table, title, subTitle);
    }

    public CsvTableColumn(final Table table, final String title) {
        super(table, title);
    }

    @Override
    protected String generateHeader(final String str) {
        if(isLastColumn()) {
            return str;
        }

        return String.format(DATA_COL_SEPARATOR_FORMAT, str, getTable().getColumnSeparator());
    }

    @Override
    public String generateData(final Object data) {
        if(isLastColumn()) {
            return super.generateData(data);
        }

        return String.format(DATA_COL_SEPARATOR_FORMAT, super.generateData(data), getTable().getColumnSeparator());
    }

    /**
     * Align a string to the right side, based on the length of the title
     * header of the column.
     * @param str the string to be aligned
     * @return the aligned string
     */
    protected String alignStringRight(final String str) {
        return alignStringRight(str, generateTitleHeader().length());
    }

    public static String alignStringRight(final String str, final int size) {
        final String fmt = String.format("%%%ds", size);
        return String.format(fmt, str);
    }
}
