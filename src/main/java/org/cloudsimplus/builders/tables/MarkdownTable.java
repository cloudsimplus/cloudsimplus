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

/**
 * Prints a Markdown table from a given data set.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.4.3
 */
public class MarkdownTable extends CsvTable {
    public MarkdownTable() {
        this("");
    }

    /**
     * Creates a Table
     * @param title the title of the table
     */
    public MarkdownTable(final String title) {
        super(title);
        setColumnSeparator("|");
    }

    @Override
    protected String rowOpening() {
        return getColumnSeparator();
    }

    @Override
    public void printTitle() {
        if(StringUtils.isNotBlank(getTitle())){
            getPrintStream().println(getCentralizedString(getTitle()));
        }
    }

    @Override
    public void printTableOpening() {
        getPrintStream().println();
    }

    @Override
    protected String subtitleHeaderOpening() {
        return createHorizontalLine(true);
    }

    /**
     * There is no need for a table closing in Markdown.
     */
    @Override
    public void printTableClosing() {/**/}

    @Override
    public String getLineSeparator() {
        return "-";
    }

    @Override
    public TableColumn newColumn(final String title, final String subtitle, final String format) {
        return new MarkdownTableColumn(title, subtitle, format);
    }
}
