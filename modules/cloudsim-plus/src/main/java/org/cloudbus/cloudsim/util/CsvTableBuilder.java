package org.cloudbus.cloudsim.util;

import java.util.List;
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
    public void printColumn(final List<Object> row, final int columnIndex) {
        if(columnIndex < getColumnHeaders().size()-1)
            Log.print(generateColumn(getFormatedColumnData(row, columnIndex)));
        else Log.print(getFormatedColumnData(row, columnIndex));
    }
    
    @Override
    protected void printRowOpenning() {}

    @Override
    protected void printColumnHeader(final int columnIndex) {
        if(columnIndex < getColumnHeaders().size()-1)
            Log.print(generateColumn(getColumnHeaders().get(columnIndex)));
        else Log.print(getColumnHeaders().get(columnIndex));
    }

    @Override
    protected void printRowClosing() {
        Log.printLine();
    }

    protected String generateColumn(final String data) {
        return String.format("%s%s", data, getColumnSeparator());
    }
    
    public String getLineSeparator() { 
        return ""; 
    }
    
    public String getColumnSeparator(){ 
        return ";"; 
    }
}
