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

/**
 * A generator of HTML tables.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class HtmlTable extends AbstractTable {
    public HtmlTable() {
        super();
    }

    /**
     * Creates an Table
     * @param title Title of the table
     */
    public HtmlTable(final String title) {
        super(title);
    }

    @Override
    protected void printTableOpening() {
        getPrintStream().println("\n<table>");
    }

    @Override
    protected void printTitle() {
        getPrintStream().printf("  <caption>%s</caption>\n", getTitle());
    }

    @Override
    protected void printRowOpening() {
        getPrintStream().println("  <tr>");
    }

    @Override
    protected void printRowClosing() {
        getPrintStream().println("\n  </tr>");
    }

    @Override
    protected void printTableClosing() {
        getPrintStream().println("</table>\n");
    }

    @Override
    public TableColumn addColumn(int index, String columnTitle) {
        final TableColumn col = new HtmlTableColumn(this, columnTitle);
        getColumns().add(index, col);
        return col;
    }
}
