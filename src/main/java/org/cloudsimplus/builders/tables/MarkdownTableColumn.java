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
 * A column of a {@link MarkdownTable}. The class generates the string
 * that represents a column in the table.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.4.3
 */
public class MarkdownTableColumn extends CsvTableColumn {
    public MarkdownTableColumn(final String title, final String subTitle) {
        this(title, subTitle, "");
    }

    public MarkdownTableColumn(final String title) {
        this(title, "", "");
    }

    public MarkdownTableColumn(final Table table, final String title, final String subTitle) {
        super(table, title, subTitle);
    }

    public MarkdownTableColumn(final Table table, final String title) {
        super(table, title);
    }

    public MarkdownTableColumn(final String title, final String subTitle, final String format) {
        super(title, subTitle, format);
    }

    @Override
    public String generateData(final Object data) {
        return alignStringRight(super.generateData(data));
    }

    @Override
    public String generateSubtitleHeader() {
        return alignStringRight(super.generateSubtitleHeader());
    }
}
