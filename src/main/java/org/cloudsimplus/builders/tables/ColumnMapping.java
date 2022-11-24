package org.cloudsimplus.builders.tables;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A record that creates a mapping for adding a column into a table latter on.
 * That is used by {@link TableBuilderAbstract} objects.
 *
 * @param <T>          the type of objects printed into the table
 * @param col          the column to add
 * @param dataFunction a function that receives an object T and returns the data to be printed from that object
 * @param index        the index of the column in the table
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.3.1
 */
record ColumnMapping<T>(TableColumn col, Function<T, Object> dataFunction, int index) {
    ColumnMapping(TableColumn col, Function<T, Object> dataFunction) {
        this(col, dataFunction, Integer.MAX_VALUE);
    }

    ColumnMapping {
        requireNonNull(col);
        requireNonNull(dataFunction);
    }

    public Object getColData(T object){
        return dataFunction.apply(object);
    }
}
