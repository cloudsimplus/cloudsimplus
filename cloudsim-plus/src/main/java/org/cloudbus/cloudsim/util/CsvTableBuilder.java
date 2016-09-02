package org.cloudbus.cloudsim.util;

import org.cloudbus.cloudsim.Log;

/**
 * Prints a table from a given data set, using a Comma Separated Text (CSV) format.
 * @author Manoel Campos da Silva Filho
 */
public class CsvTableBuilder extends AbstractTableBuilder {
    public CsvTableBuilder() {
        super();
    }

    public CsvTableBuilder(final String title) {
        super(title);
    }

    @Override
    public void printTitle() {}

    @Override
    public void printTableOpenning(){}

    @Override
    public void printTableClosing(){}

    @Override
    protected void printRowOpenning() {}
    
    @Override
    protected void printRowClosing() {
        Log.printLine();
    }

    public String getLineSeparator() { 
        return ""; 
    }
    
    @Override
    public TableColumn addColumn(String columnTitle) {
        TableColumn col = new CsvTableColumn(this, columnTitle);
        getColumns().add(col);
        return col;
    }
}
