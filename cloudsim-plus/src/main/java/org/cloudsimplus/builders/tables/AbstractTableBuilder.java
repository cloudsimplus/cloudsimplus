/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.util.Log;

import static java.util.stream.Collectors.toList;

/**
 * An abstract base class for implementing table builders.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class AbstractTableBuilder implements TableBuilder {
    /** @see #getColumns() */
    private final List<TableColumn> columns;

    /** @see #getTitle() */
    private String title;

    /** @see #getRows() */
    private final List<List<Object>> rows;

    /**
     * @see #getColumnSeparator()
     */
    private String columnSeparator;

    public AbstractTableBuilder(){
        this("");
    }

    /**
     * Creates an TableBuilder
     * @param title Title of the table
     */
    public AbstractTableBuilder(final String title){
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        setTitle(title);
    }

    /**
     * @return the list of columns of the table
     */
    @Override
    public List<TableColumn> getColumns() {
        return columns;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public final TableBuilder setTitle(final String title) {
        this.title = title;
        return this;
    }

    @Override
    public String getColumnSeparator(){
        return columnSeparator;
    }

    @Override
    public final TableBuilder setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
        return this;
    }

    /**
     * @return The data to be printed, where each row contains
     * a list of data columns.
     */
    protected List<List<Object>> getRows() {
        return rows;
    }

    @Override
    public List<Object> newRow(){
        List<Object> row = new ArrayList<>();
        rows.add(row);
        return row;
    }

    /**
     *
     * @return true if there is at least a column with a subtitle, false if no column
     * has a subtitle.
     */
    private boolean isThereAnySubtitledColumn(){
        return columns.stream().anyMatch(col -> !col.getSubTitle().trim().isEmpty());
    }

    private void printRow(final List<Object> row) {
        printRowOpening();
        final List<TableColumn> cols =
            columns.stream()
                .limit( Math.min(columns.size(), row.size()))
                .collect(toList());

        int i = 0;
        for(TableColumn col: cols){
            Log.print(col.generateData(row.get(i++)));
        }
        printRowClosing();
    }

    @Override
    public void print() {
        printTableOpening();
        printTitle();
        printColumnHeaders();
        rows.forEach(this::printRow);
        printTableClosing();
    }

    protected void printColumnHeaders(){
        printRowOpening();
        columns.forEach(col -> Log.print(col.generateTitleHeader()));
        printRowClosing();
        if(isThereAnySubtitledColumn()){
            printRowOpening();
            columns.forEach(col -> Log.print(col.generateSubtitleHeader()));
            printRowClosing();
        }
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
     * Prints the string that has to precede each printed row.
     */
    protected abstract void printRowOpening();

    /**
     * Prints the string to close a row.
     */
    protected abstract void printRowClosing();

    /**
     * Prints the string to close the table.
     */
    protected abstract void printTableClosing();

    @Override
    public final TableBuilder addColumnList(final String... columnTitles) {
        for(String column: columnTitles){
            addColumn(column);
        }
        return this;
    }

    @Override
    public final TableColumn addColumn(final String columnTitle) {
        return addColumn(getColumns().size(), columnTitle);
    }

    @Override
    public final TableColumn addColumn(String columnTitle, String columnSubTitle) {
        return addColumn(columnTitle).setSubTitle(columnSubTitle);
    }

    @Override
    public final TableColumn addColumn(int index, TableColumn column) {
        columns.add(index, column);
        return column;
    }

    @Override
    public final TableColumn addColumn(TableColumn column) {
        return addColumn(columns.size(), column);
    }
}
