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

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

/**
 * Prints a table from a given data set, using a Comma Separated Text (CSV) format.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CsvTable extends AbstractTable {
    public CsvTable() {
        this("");
    }

    public CsvTable(final String title) {
        super(title);
        this.setColumnSeparator(";");
    }

    /**
     * CSV files doesn't have a title.
     */
    @Override
    public void printTitle() {/**/}

    /**
     * CSV files doesn't have a table opening line.
     */
    @Override
    public void printTableOpening() {/**/}

    /**
     * CSV files doesn't have a table closing line.
     */
    @Override
    public void printTableClosing() {/**/}

    /**
     * CSV files doesn't have a row opening line.
     * @return
     */
    @Override
    protected String rowOpening() { return ""; }

    @Override
    protected String rowClosing() {
        return "%n";
    }

    @Override
    protected String subtitleHeaderOpening() {
        return "";
    }

    /**
     * Creates a horizontal line with the same width of the table.
     * @return The string containing the horizontal line
     */
    protected String createHorizontalLine(final boolean includeColSeparator) {
        if(includeColSeparator){
            final StringBuilder sb = new StringBuilder(rowOpening());
            final String row =
                getColumns()
                        .stream()
                        .map(col -> stringRepeat(getLineSeparator(), col.getTitle().length()))
                        .collect(Collectors.joining(getColumnSeparator()));
            return sb.append(row)
                     .append(rowClosing())
                     .toString();
        }

        return stringRepeat(getLineSeparator(), getLengthOfColumnHeadersRow()) + "%n";
    }

    /**
     * Creates a copy of the a string repeated a given number of times.
     * @param str The string to repeat
     * @param timesToRepeat The number of times to repeat the string
     * @return The string repeated the given number of times
     */
    protected final String stringRepeat(final String str, final int timesToRepeat) {
        return new String(new char[timesToRepeat]).replace("\0", str);
    }

    /**
     * Gets the number of characters of the column headers row.
     *
     * @return the number of characters of column headers row
     */
    protected final int getLengthOfColumnHeadersRow(){
        return getColumns().stream().mapToInt(col -> col.generateTitleHeader().length()).sum();
    }

    /**
     * Gets a given string and returns a formatted version of it
     * that is centralized in the table width.
     * @param str The string to be centralized
     * @return The centralized version of the string
     */
    protected String getCentralizedString(final String str) {
        final int indentationLength = (getLengthOfColumnHeadersRow() - str.length())/2;
        return String.format("%n%s%s%n", StringUtils.repeat(" ", indentationLength), str);
    }

    public String getLineSeparator() {
        return "";
    }

    @Override
    public TableColumn addColumn(int index, String columnTitle) {
        final TableColumn col = new CsvTableColumn(this, columnTitle);
        getColumns().add(index, col);
        return col;
    }
}
