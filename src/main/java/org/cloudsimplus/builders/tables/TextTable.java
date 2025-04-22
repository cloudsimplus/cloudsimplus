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
 * Prints a table from a given data set, using a simple delimited text format.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class TextTable extends CsvTable {
    public TextTable() {
        this("");
    }

    /**
     * Creates a Table
     * @param title the title of the table
     */
    public TextTable(final String title) {
        super(title);
        setColumnSeparator("|");
    }

    @Override
    public void printTitle() {
        if(!getTitle().isBlank()){
            getPrintStream().println(getCentralizedString(getTitle()));
        }
    }

    @Override
    public void printTableOpening() {
        getPrintStream().println();
    }

    @Override
    protected void printColumnHeaders() {
        super.printColumnHeaders();
        getPrintStream().printf(createHorizontalLine(false));
    }

    @Override
    public void printTableClosing() {
        getPrintStream().printf(createHorizontalLine(false));
    }

    @Override
    public String getLineSeparator() {
        return "-";
    }

    @Override
    public TableColumn newColumn(final String title, final String subtitle, final String format) {
        return new TextTableColumn(title, subtitle, format);
    }
}
