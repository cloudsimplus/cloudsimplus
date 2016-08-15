package org.cloudbus.cloudsim.util;

/**
 * A column of an CSV table. The class generates the CSV code
 * that represents a column in a CSV table.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class CsvTableColumn extends AbstractTableColumn {

    public CsvTableColumn(TableBuilder table, String title) {
        super(table, title);
        this.setColumnSeparator(";");
    }

    @Override
    protected String generateHeader(String title) {
        if(isLastColumn())
            return title;
        return String.format("%s%s", title, getColumnSeparator());
    }

    @Override
    public String generateData(Object data) {
        if(isLastColumn())
            return super.generateData(data);
        return String.format("%s%s", super.generateData(data), getColumnSeparator());
    }

    
}
