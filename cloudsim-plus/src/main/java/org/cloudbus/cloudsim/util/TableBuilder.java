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
     * Adds a column to the table to be printed.
     * 
     * @param columnTitle The title of the column to be added.
     * @return The created column.
     */
    TableColumn addColumn(final String columnTitle);
    
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
     * @return the list of columns of the table
     */
    List<TableColumn> getColumns();    
       
    /**
     * Builds and prints the table.
     */
    void print();
        
  
}
