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
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An abstract base class for implementing data tables to present simulation results.
 *
 * @author Manoel Campos da Silva Filho
 */
@Getter
public non-sealed abstract class AbstractTable implements Table {
    /**
     * The {@link PrintStream} used to print the generated table.
     */
    @Setter
    private PrintStream printStream;

    /**
     * {@return the list} of columns of the table
     */
    private final List<TableColumn> columns;

    /**
     * {@return the list} of columns of the table
     */
    private String title;

    /** @see #getRows() */
    private final List<List<Object>> rows;

    /** @see #getColumnSeparator() */
    private String columnSeparator;

    public AbstractTable(){
        this("");
    }

    /**
     * Creates a Table
     * @param title Title of the table
     */
    public AbstractTable(final String title){
        this.printStream = System.out;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        setTitle(title);
    }

    @Override
    public int colCount() {
        return columns.size();
    }

    @Override
    public final Table setTitle(@NonNull final String title) {
        this.title = title;
        return this;
    }

    @Override
    public final Table setColumnSeparator(@NonNull String columnSeparator) {
        this.columnSeparator = columnSeparator;
        return this;
    }

    /**
     * {@return the data to be printed}, where each row contains
     * a list of columns data.
     */
    protected List<List<Object>> getRows() {
        return rows;
    }

    @Override
    public List<Object> newRow(){
        final var row = new ArrayList<>();
        rows.add(row);
        return row;
    }

    /**
     * {@return true if there is at least a column with a subtitle, false if no column has a subtitle}
     */
    private boolean isThereAnySubtitledColumn(){
        return columns.stream().anyMatch(col -> !col.getSubTitle().isBlank());
    }

    /**
     * Prints a row of the table.
     * @param row a row containing the data for each column.
     */
    private void printRow(final List<Object> row) {
        printStream.printf(rowOpening());
        final List<TableColumn> cols =
            columns.stream()
                .limit(Math.min(columns.size(), row.size()))
                .toList();

        final String rowData = IntStream.range(0, cols.size())
                                        .mapToObj(i -> cols.get(i).generateData(row.get(i)))
                                        .collect(Collectors.joining(columnSeparator));
        printStream.print(rowData);
        printStream.printf(rowClosing());
    }

    @Override
    public void print() {
        printTableOpening();
        printTitle();
        printColumnHeaders();
        rows.forEach(this::printRow);
        printTableClosing();
        getPrintStream().println();
    }

    protected void printColumnHeaders(){
        printStream.printf(rowOpening());
        final String headerData = columns.stream()
                                         .map(TableColumn::generateTitleHeader)
                                         .collect(Collectors.joining(columnSeparator));
        printStream.print(headerData);
        printStream.printf(rowClosing());
        printSubtitleHeaders();
    }

    private void printSubtitleHeaders() {
        if(!isThereAnySubtitledColumn())
            return;

        printStream.printf(subtitleHeaderOpening());
        printStream.printf(rowOpening());
        final String subTitlesData = columns.stream()
                                            .map(TableColumn::generateSubtitleHeader)
                                            .collect(Collectors.joining(columnSeparator));
        printStream.print(subTitlesData);
        printStream.printf(rowClosing());
    }

    /**
     * Prints the string to open the table.
     */
    protected abstract void printTableOpening();

    /**
     * Prints the table title.
     */
    protected abstract void printTitle();

    /**
     * {@return the string that has to precede each printed row}
     */
    protected abstract String rowOpening();

    /**
     * {@return the string to close a row}
     */
    protected abstract String rowClosing();

    /**
     * {@return the string that has to precede the subtitle head}
     */
    protected abstract String subtitleHeaderOpening();

    /**
     * Prints the string to close the table.
     */
    protected abstract void printTableClosing();

    @Override
    public final Table addColumnList(final String... columnTitles) {
        for(final String column: columnTitles){
            newColumn(column);
        }
        return this;
    }

    @Override
    public final TableColumn newColumn(final String title, final String subTitle) {
        return newColumn(title).setSubTitle(subTitle);
    }

    @Override
    public final TableColumn newColumn(final String title) {
        return newColumn(title, "", "");
    }

    /**
     * Adds a column to the end of the table.
     * @param column the column to add
     * @return this table instance
     */
    protected final TableColumn addColumn(final TableColumn column) {
        return addColumn(column, columns.size());
    }

    /**
     * Adds a column at a given position of the table.
     *
     * @param column the column to add
     * @param index  the position in the table to add the column
     * @return this table instance
     */
    protected final TableColumn addColumn(final TableColumn column, final int index) {
        column.setTable(this);
        if(index > colCount())
            columns.add(column);
        else columns.add(index, column);
        return column;
    }
}
