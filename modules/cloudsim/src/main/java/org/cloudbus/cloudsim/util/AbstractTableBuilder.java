package org.cloudbus.cloudsim.util;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base class for implementing table builders.
 * 
 * @author Manoel Campos da Silva Filho
 */
public abstract class AbstractTableBuilder implements TableBuilder {
    /** @see #getColumnHeaders() */
    private final List<String> columnHeaders;
    /** @see #getColumnFormats() */
    private final List<String> columnFormats;
    /** @see #getTitle() */
    private String title;
    
    /** @see #getRows() */
    private final List<List<Object>> rows;
    
    public AbstractTableBuilder(){
        this("");
    }    
    
    /**
     * Creates an TableBuilder
     * @param title Title of the table
     */
    public AbstractTableBuilder(final String title){
        this.columnHeaders = new ArrayList<>();
        this.columnFormats = new ArrayList<>();
        this.rows = new ArrayList<>();
        setTitle(title);
    }

    /**
     * @return the headers of the table columns
     */
    protected List<String> getColumnHeaders() {
        return columnHeaders;
    }

    /**
     * @return the format to print each column data, following
     * the formats defined in {@link String#format(java.lang.String, java.lang.Object...)}
     */
    protected List<String> getColumnFormats() {
        return columnFormats;
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
    
    @Override
    public TableBuilder addColumn(final String columnTitle, String format){
        if(format.trim().isEmpty()){
            format = "%s";
        }
        getColumnHeaders().add(columnTitle);
        getColumnFormats().add(format);
        return this;
    }
    
    @Override
    public TableBuilder addColumn(final String columnTitle){
        return addColumn(columnTitle, "%s");
    }

    @Override
    public TableBuilder addColumnList(final String columnTitles[]){
        for(String columnTitle: columnTitles){
            addColumn(columnTitle, "%s");
        }
        return this;
    }    
    
    private void printRow(final List<Object> row) {
        printRowOpenning();
        for(int i = 0; i < Math.min(getColumnHeaders().size(), row.size()); i++){
            printColumn(row, i);
        }
        printRowClosing();
    }
    
    protected int getColumnLength(final List<Object> row, final int columnIndex) {
        return row.get(columnIndex).toString().length();
    }    
    
    @Override
    public void print() {
        printTableOpenning();
        printTitle();
        printColumnHeaders();
        for(List<Object> row: getRows()){
            printRow(row);
        }
        printTableClosing();
    }     
    
    protected void printColumnHeaders(){
        printRowOpenning();
        for(int i = 0; i < getColumnHeaders().size(); i++){
            printColumnHeader(i);
        }
        printRowClosing();
    }
    
    /**
     * Gets the data of a given column, formatted according to the
     * {@link #getColumnFormats() format} associated to that column. 
     * @param row the row to get data of a specific column
     * @param columnIndex the index of the column
     * @return a string containing the formatted column
     * @see #getColumnFormats() 
     */
    protected String getFormatedColumnData(final List<Object> row, final int columnIndex){
        return String.format(getColumnFormats().get(columnIndex), row.get(columnIndex));
    }        
    
    /**
     * Prints the string to open the table.
     */
    protected abstract void printTableOpenning();
    
    /**
     * Prints the table title.
     */
    protected abstract void printTitle();
    
    /**
     * Prints the string that has to precede each printed row.
     */
    protected abstract void printRowOpenning();
    
    /**
     * Prints a given column header, opening and closing the column.
     * @param columnIndex the index of the column to print the header
     */
    protected abstract void printColumnHeader(final int columnIndex);
    
    /**
     * Prints a given column data for a specific row, opening and closing the column.
     * @param row the row to print a given column data
     * @param columnIndex the index of the column
     */
    protected abstract void printColumn(final List<Object> row, final int columnIndex);
    
    /**
     * Prints the string to close a row.
     */
    protected abstract void printRowClosing();
    
    /**
     * Prints the string to close the table.
     */
    protected abstract void printTableClosing();
}
