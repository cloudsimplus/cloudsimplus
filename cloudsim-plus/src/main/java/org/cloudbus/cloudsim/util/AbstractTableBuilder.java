package org.cloudbus.cloudsim.util;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Log;

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
        return getColumns().stream().anyMatch(col -> !col.getSubTitle().trim().isEmpty());
    }
    
    private void printRow(final List<Object> row) {
        printRowOpenning();
        for(int i = 0; i < Math.min(getColumns().size(), row.size()); i++){
            Log.print(getColumns().get(i).generateData(row.get(i)));
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
        getRows().stream().forEach(row -> printRow(row));
        printTableClosing();
    }     
    
    protected void printColumnHeaders(){
        printRowOpenning();
        getColumns().forEach(col -> Log.print(col.generateTitleHeader()));
        printRowClosing();
        if(isThereAnySubtitledColumn()){
            printRowOpenning();
            getColumns().forEach(col -> Log.print(col.generateSubtitleHeader()));
            printRowClosing();
        }
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
     * Prints the string to close a row.
     */
    protected abstract void printRowClosing();
    
    /**
     * Prints the string to close the table.
     */
    protected abstract void printTableClosing();
    
    @Override
    public TableBuilder addColumnList(String[] columnTitles) {
        for(String column: columnTitles){
            addColumn(column);
        }
        return this;
    }
    
}
