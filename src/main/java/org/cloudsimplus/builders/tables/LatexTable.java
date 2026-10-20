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

/// Generates tabular data in LaTeX format.
/// @author marwenbenhajhassine
public class LatexTable extends AbstractTable {
    public LatexTable() {
        super();
        setColumnSeparator(" & ");
    }

    public LatexTable(final String title){
        super(title);
    }

    @Override
    protected void printTableOpening(){
        getPrintStream().println("\\begin{table}[h]");
        getPrintStream().println("\\centering");
    }

    @Override
    protected void printTitle() {
        getPrintStream().printf("  \\caption{%s}%n", getTitle());
        printTabularStart();
    }

    protected void printTabularStart(){
        getPrintStream().print("  \\begin{tabular}{ ");
        colNumberSpecifier(); // number of cols must be specified before building the table
        getPrintStream().println(" }");
    }

    /// Adds column specifiers which creates columsn and define how they are aligned in the LaTex Table.
    public void colNumberSpecifier() {
        final String columnSpecs = "|c".repeat(this.colCount());
        getPrintStream().printf("%s|", columnSpecs);
    }

    @Override
    protected String rowClosing(){
        return " \\\\%n";
    }

    @Override
    protected void printTableClosing(){
        getPrintStream().println("  \\hline");
        getPrintStream().println("  \\end{tabular}");
        getPrintStream().println("\\end{table} ");
    }

    @Override
    public TableColumn newColumn(final String title, final String subtitle, final String format) {
        return new LatexTableColumn(title, subtitle, format);
    }

    @Override
    protected String rowOpening() {
        return "    \\hline ";
    }

    @Override
    protected String subtitleHeaderOpening() {
        return "";
    }
}
