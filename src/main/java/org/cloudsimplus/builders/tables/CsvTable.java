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
        this.setColumnSeparator(",");
    }

    /**
     * CSV files don't have a title.
     */
    @Override
    public void printTitle() {/**/}

    /**
     * CSV files don't have a table opening line.
     */
    @Override
    public void printTableOpening() {/**/}

    /**
     * CSV files don't have a table closing line.
     */
    @Override
    public void printTableClosing() {/**/}

    /**
     * CSV files don't have a row opening line.
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
     * @return a horizontal line with the same width of the table
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
     * {@return a string repeated a given number of times}
     * @param str the string to repeat
     * @param timesToRepeat the number of times to repeat the string
     */
    protected final String stringRepeat(final String str, final int timesToRepeat) {
        return new String(new char[timesToRepeat]).replace("\0", str);
    }

    /**
     * @return the number of characters of the column headers row
     */
    protected final int getLengthOfColumnHeadersRow(){
        return getColumns().stream().mapToInt(col -> col.generateTitleHeader().length()).sum();
    }

    /**
     * Gets a string and returns a copy centralized along the table width.
     * @param str the string to be centralized
     * @return the centralized version of the string
     */
    protected String getCentralizedString(final String str) {
        final int indentationLength = (getLengthOfColumnHeadersRow() - str.length())/2;
        return "%n%s%s%n".formatted(StringUtils.repeat(" ", indentationLength), str);
    }

    public String getLineSeparator() {
        return "";
    }

    @Override
    public TableColumn newColumn(final String title, final String subtitle, final String format) {
        return new CsvTableColumn(title, subtitle, format);
    }
}
