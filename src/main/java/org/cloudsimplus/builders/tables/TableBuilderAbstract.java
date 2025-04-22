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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * An abstract class to build tables to print
 * data from a list of objects containing simulation results.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 2.3.2
 * @param <T> the type of objects printed into the table
 */
public abstract class TableBuilderAbstract<T> {
    private List<? extends T> list;

    /**
     * A list containing information about columns to be added to a table later on.
     */
    private List<ColumnMapping<T>> colsMappings;

    @Getter
    private Table table;

    /**
     * Instantiates a builder to print the list of objects T using the
     * default {@link MarkdownTable}.
     * To use a different {@link Table}, check the alternative constructors.
     *
     * @param list the list of objects T to print
     */
    public TableBuilderAbstract(final List<? extends T> list){
        this(list, new MarkdownTable());
    }

    /**
     * Instantiates a builder to print the list of objects T using a
     * given {@link Table}.
     *
     * @param list the list of objects T to print
     * @param table the {@link Table} used to build the table with the object data
     */
    public TableBuilderAbstract(final List<? extends T> list, final Table table){
        setTable(table);
        setObjectList(list);
        colsMappings = new ArrayList<>();
    }

    /**
     * Sets a List of objects T to be printed.
     * @param list List of objects T to set
     * @return this table builder
     */
    protected final TableBuilderAbstract<T> setObjectList(final List<? extends T> list) {
        this.list = requireNonNull(list);
        return this;
    }

    public TableBuilderAbstract<T> setTitle(final String title){
        table.setTitle(title);
        return this;
    }

    /**
     * Access a column at a given position in order to perform some configuration on it.
     * @param index index of the column to access
     * @param consumer a {@link Consumer} that will be called to use the column accessed at the requested position.
     *                 The consumer should provide the code you want to be performed over that column.
     * @return this TableBuilder object
     */
    public final TableBuilderAbstract<T> column(final int index, @NonNull final Consumer<TableColumn> consumer){
        consumer.accept(table.getColumns().get(index));
        return this;
    }

    /**
     * Sets the {@link Table} used to build the table with data.
     * The default table builder is {@link TextTable}.
     * @param table the {@link Table} to set
     * @return this TableBuilder object
     */
    protected final TableBuilderAbstract<T> setTable(@NonNull final Table table) {
        this.table = table;
        return this;
    }

    /**
     * Adds a column to the end of the table to be built.
     * @param col the column to add
     * @param dataFunction a function that receives a T object and returns some data from it to be printed for the added column
     * @return this TableBuilder object
     */
    public TableBuilderAbstract<T> addColumn(final TableColumn col, final Function<T, Object> dataFunction){
        return addColumn(col, dataFunction, Integer.MAX_VALUE);
    }

    /**
     * Adds a column to a specific position into the table to be built.
     *
     * @param col          the column to add
     * @param dataFunction a function that receives a T object and returns some data from it to be printed for the added column
     * @param index        the position to insert the column.
     * @return this TableBuilder object
     */
    public TableBuilderAbstract<T> addColumn(@NonNull final TableColumn col, @NonNull final Function<T, Object> dataFunction, final int index){
        colsMappings.add(new ColumnMapping<>(col, dataFunction, index));
        return this;
    }

    /**
     * Removes columns from given positions.
     * @param indexes the indexes of the columns to remove.
     * @return this TableBuilder object
     * @see #removeColumn(int)
     */
    public final TableBuilderAbstract<T> removeColumn(final int ...indexes){
        for (final int i : indexes) {
            removeColumn(i);
        }
        return this;
    }

    /**
     * Removes a column from a given position.
     * @param index the index of the column to remove.
     * @return this TableBuilder object
     * @see #removeColumn(int...)
     */
    public final TableBuilderAbstract<T> removeColumn(final int index){
        table.getColumns().remove(index);
        return this;
    }

    /**
     * Builds the table with the data from the list of objects and shows the results.
     */
    public void build(){
        createAndAddTableColumns();

        if(table.getTitle().isEmpty()){
            table.setTitle("SIMULATION RESULTS");
        }

        list.forEach(cloudlet -> addDataToRow(cloudlet, table.newRow()));
        table.print();
    }

    private void createAndAddTableColumns() {
        createTableColumns();

        final var tb = (AbstractTable)table;
        colsMappings.forEach(mapping -> tb.addColumn(mapping.col(), mapping.index()));
    }

    /**
     * Creates the columns of the table and define how the data for those columns
     * will be got from an object inside the {@link #list} of objects to be printed.
     * It doesn't add such columns into a table yet.
     * @see #createAndAddTableColumns()
     */
    protected abstract void createTableColumns();

    /**
     * Add data to a row of the table being generated.
     * @param object The object T to get data to show in the row of the table
     * @param row the row that the data from the object T will be added to
     */
    protected void addDataToRow(final T object, final List<Object> row) {
        colsMappings.forEach(mapping ->  row.add(mapping.getColData(object)));
    }
}
