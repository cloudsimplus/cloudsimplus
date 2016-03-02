package org.cloudbus.cloudsim.util;

import java.util.List;

/**
 * An interface for classes that generate tables from a given data set, 
 * following the Builder Design Pattern.
 * @author Manoel Campos da Silva Filho
 */
public interface TableBuilder {
    /**
     * Adds a new row to the list of rows containing the data to be printed.
     * @return 
     */
    List<Object> newRow();

    /**
     * Adds a column to the table to be printed, where the column data
     * will be printed according to a given format.
     * 
     * @param columnTitle The title of the column
     * @param format The format of the column data, following
     * the patterns defined by {@link String#format(java.lang.String, java.lang.Object...)}
     * @return The {@link TableBuilder} instance.
     */
    TableBuilder addColumn(final String columnTitle, String format);
    
    /**
     * Adds a column to the table to be printed, where the column data
     * will be printed without a specific format.
     * 
     * @param columnTitle The title of the column
     * @return The {@link TableBuilder} instance.
     * @see #addColumn(java.lang.String, java.lang.String) 
     */
    TableBuilder addColumn(final String columnTitle);
    
    /**
     * Adds a list of columns to the table to be printed, where the column data
     * will be printed without a specific format.
     * 
     * @param columnTitles The titles of the columns
     * @return The {@link TableBuilder} instance.
     * @see #addColumn(java.lang.String, java.lang.String) 
     */
    TableBuilder addColumnList(final String columnTitles[]);

    /**
     * 
     * @return the table title
     */
    String getTitle();
    
    /**
     * 
     * @param title the table title to set
     * @return The TableBuilder instance
     */
    TableBuilder setTitle(final String title);   
       
    /**
     * Builds and prints the table.
     */
    void print();
}
