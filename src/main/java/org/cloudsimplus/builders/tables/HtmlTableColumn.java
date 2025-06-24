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
 * A column of an {@link HtmlTable}. The class generates the HTML code
 * that represents a column in the table.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class HtmlTableColumn extends AbstractTableColumn {
    public HtmlTableColumn(final String title, final String subTitle) {
        this(title, subTitle, "");
    }

    public HtmlTableColumn(final String title) {
        this(title, "", "");
    }

    public HtmlTableColumn(final Table table, final String title) {
        super(table, title);
    }

    public HtmlTableColumn(final Table table, final String title, final String subTitle) {
        super(table, title, subTitle);
    }

    public HtmlTableColumn(final String title, final String subTitle, final String format) {
        super(title, subTitle, format);
    }

    private String indentLine(final int columnIndex) {
        return columnIndex == 0 ? "    " : "";
    }

    @Override
    protected String generateHeader(final String str) {
        final int index = getTable().getColumns().indexOf(this);
        return "%s<th>%s</th>".formatted(indentLine(index), str);
    }

    @Override
    public String generateData(final Object data) {
        final int index = getTable().getColumns().indexOf(this);
        return "%s<td>%s</td>".formatted(indentLine(index), super.generateData(data));
    }

}
