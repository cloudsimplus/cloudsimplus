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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * An abstract class to build tables to print
 * data from a list of objects containing simulation results.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 2.3.2
 */
public abstract class TableBuilderAbstract<T> {
    private List<? extends T> list;

    /**
     * A Map containing a function that receives an object T and returns
     * the data to be printed from that object.
     * That data is the value for the associated column
     * of the table being generated.
     */
    private final Map<TableColumn, Function<T, Object>> colsDataFunctions;

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
        colsDataFunctions = new HashMap<>();
        createTableColumns();
    }

    /**
     * Sets a List of objects T to be printed.
     * @param list List of objects T to set
     * @return
     */
    protected final TableBuilderAbstract<T> setObjectList(final List<? extends T> list) {
        this.list = requireNonNull(list);
        return this;
    }

    public TableBuilderAbstract<T> setTitle(final String title){
        table.setTitle(title);
        return this;
    }

    protected Table getTable() {
        return table;
    }

    /**
     * Access a column at a given position in order to perform some configuration on it.
     * @param index index of the column to access
     * @param consumer a {@link Consumer} that will be called to use the column accessed at the requested position.
     *                 The consumer should provide the code you want to be performed over that column.
     * @return this TableBuilder object
     */
    public final TableBuilderAbstract<T> column(final int index, final Consumer<TableColumn> consumer){
        requireNonNull(consumer);
        consumer.accept(table.getColumns().get(index));
        return this;
    }

    /**
     * Sets the {@link Table} used to build the table with Cloudlet Data.
     * The default table builder is {@link TextTable}.
     * @param table the  {@link Table} to set
     * @return
     */
    protected final TableBuilderAbstract<T> setTable(final Table table) {
        this.table = requireNonNull(table);
        return this;
    }

    /**
     * Dynamically adds a column to the end of the table to be built.
     * @param col the column to add
     * @param dataFunction a function that receives a Cloudlet and returns the data to be printed for the added column
     * @return
     */
    public TableBuilderAbstract<T> addColumn(final TableColumn col, final Function<T, Object> dataFunction){
        return addColumn(getTable().colCount(), col, dataFunction);
    }

    /**
     * Creates a column at the end of the table to be built.
     * @param title The title of the column to be added.
     * @param subtitle The subtitle of the column to be added.
     * @return the created column
     */
    protected TableColumn newColumn(final String title, final String subtitle) {
        return newColumn(title, subtitle, "");
    }

    /**
     * Creates a column at the end of the table to be built.
     * @param title The title of the column to be added.
     * @param subtitle The subtitle of the column to be added.
     * @param format format to print the column data
     * @return the created column
     */
    protected TableColumn newColumn(final String title, final String subtitle, final String format) {
        return getTable().addColumn(title, subtitle, format);
    }

    /**
     * Adds a column to a specific position into the table to be built.
     * @param index the position to insert the column.
     * @param col the column to add
     * @param dataFunction a function that receives a Cloudlet and returns the data to be printed for the added column
     * @return
     */
    public TableBuilderAbstract<T> addColumn(final int index, final TableColumn col, final Function<T, Object> dataFunction){
        requireNonNull(col);
        requireNonNull(dataFunction);

        getTable().addColumn(index, col);
        colsDataFunctions.put(col, dataFunction);
        return this;
    }

    private TableColumn getColumn(final int index) {
        return getTable().getColumns().get(index);
    }

    /**
     * Sets the formatting for one column.
     *
     * @param format the new formatting string.
     * @param index  the index of the target column.
     * @return
     */
    public TableBuilderAbstract<T> setFormatByIndex(final String format, final int index) {
    	getColumn(index).setFormat(format);
    	return this;
    }

    /**
     * Sets the formatting for multiple columns.
     *
     * @param format  the new formatting string.
     * @param indices indices for all target columns.
     * @return
     */
    public TableBuilderAbstract<T> setFormatByIndex(final String format, final int ...indices) {
    	for(final int index : indices) {
    		getColumn(index).setFormat(format);
    	}
    	return this;
    }

    /**
     * Sets the formatting for the column with a specific title.
     * If multiple columns have the same title then all columns will be changed.
     * @param title the title of the target column(s).
     * @param format the new formatting string.
     * @return
     */
    public TableBuilderAbstract<T> setFormatByTitle(final String title, final String format){
        setColumnsFormat(col -> col.matchTitle(title), format);
    	return this;
    }

    /**
     * Sets the formatting for all columns with a specific subtitle.
     * @param subTitle the subtitle of the target column(s).
     * @param format the new formatting string.
     * @return
     */
    public TableBuilderAbstract<T> setFormatBySubTitle(final String subTitle, final String format){
        setColumnsFormat(col -> col.matchSubTitle(subTitle), format);
    	return this;
    }

    /**
     * Iterate over the columns of the table that match
     * a given {@link Predicate} and sets their format to a given value.
     * @param predicate the {@link Predicate} to filter columns to apply format
     * @param format the format to set for matching columns
     */
    protected void setColumnsFormat(final Predicate<TableColumn> predicate, final String format){
        table.getColumns()
             .stream()
             .filter(predicate)
             .forEach(col -> col.setFormat(format));
    }

    /**
     * Removes columns from given positions.
     * @param indexes the indexes of the columns to remove.
     * @return
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
     * @return
     * @see #removeColumn(int...)
     */
    public final TableBuilderAbstract<T> removeColumn(final int index){
        getTable().getColumns().remove(index);
        return this;
    }

    /**
     * Creates the columns of the table and define how the data for those columns
     * will be got from an object inside the {@link #list} of objects to be printed.
     */
    protected abstract void createTableColumns();

    /**
     * Builds the table with the data from the list of objects and shows the results.
     */
    public void build(){
        if(getTable().getTitle().isEmpty()){
            getTable().setTitle("SIMULATION RESULTS");
        }

        list.forEach(cloudlet -> addDataToRow(cloudlet, getTable().newRow()));
        getTable().print();
    }

    /**
     * Add data to a row of the table being generated.
     * @param object The object T to get to data to show in the row of the table
     * @param row The row that the data from the object T will be added to
     */
    protected void addDataToRow(final T object, final List<Object> row) {
        getTable()
            .getColumns()
            .forEach(col -> row.add(colsDataFunctions.get(col).apply(object)));
    }

    /**
     * Adds a data function for a given column.
     * @param col column to add a data function
     * @param dataFunction a function that receives an object T and returns the data to be printed from that object.
     * @return
     * @see #colsDataFunctions
     */
    protected TableBuilderAbstract<T> addColDataFunction(final TableColumn col, final Function<T, Object> dataFunction){
        colsDataFunctions.put(requireNonNull(col), requireNonNull(dataFunction));
        return this;
    }
}
