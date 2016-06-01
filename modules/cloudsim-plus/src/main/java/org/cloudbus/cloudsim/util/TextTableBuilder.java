package org.cloudbus.cloudsim.util;

import java.util.List;
import org.cloudbus.cloudsim.Log;

/**
 * Prints a table from a given data set, using a simple delimited text format.
 * @author Manoel Campos da Silva Filho
 */
public class TextTableBuilder extends CsvTableBuilder {
    public TextTableBuilder() {
        super();
    }

    /**
     * Creates an TableBuilder
     * @param title Title of the table
     */
    public TextTableBuilder(final String title) {
        super(title);
    }

    @Override
    public void printTitle() {        
        if(!getTitle().trim().isEmpty()){
            Log.print(getCentralizedString(getTitle()));
        }
    }

    @Override
    public void printTableOpenning() {
        Log.printLine();        
    }

    @Override
    protected void printColumnHeaders() {
        super.printColumnHeaders(); 
        Log.printFormatted("%s\n", createHorizontalLine()); 
    }
    
    @Override
    public void printTableClosing() {
        Log.printFormatted("%s\n", createHorizontalLine());
    }
    
    /**
     * Gets a given string and returns a formatted version of it
     * that is centralized in the table width.
     * @param str The string to be centralized
     * @return The centralized version of the string
     */
    private String getCentralizedString(final String str) {
        final int identationLength = (getLengthOfColumnHeadersRow() - str.length()) / 2;
        final String format = String.format("\n%%%ds\n", identationLength);
        return String.format(format, str);
    }    
    
    @Override
    protected String getFormatedColumnData(final List<Object> row, final int columnIndex){
        final String format = String.format("%%%ds", getColumnHeaders().get(columnIndex).length());
        return String.format(format, super.getFormatedColumnData(row, columnIndex));
    }    

    /**
     * Creates a horizontal line with the same width of the table.
     * @return The string containing the horizontal line
     */
    private String createHorizontalLine() {
        return stringRepeat(getLineSeparator(), getLengthOfColumnHeadersRow());
    }    

    /**
     * Creates a copy of the a string repeated a given number of times.
     * @param str The string to repeat
     * @param timesToRepeat The number of times to repeat the string
     * @return The string repeated the given number of times
     */
    private String stringRepeat(final String str, final int timesToRepeat) {
        return new String(new char[timesToRepeat]).replace("\0", str);
    }    

    /**
     * Gets the number of characters of the column headers row.
     * 
     * @return the number of characters of column headers row
     */
    private int getLengthOfColumnHeadersRow(){
        int length = 0;
        for(int i = 0; i < getColumnHeaders().size(); i++){
            length += generateColumn(getColumnHeaders().get(i)).length();
        }
        return length;
    }
    
    @Override
    public String getLineSeparator() { 
        return "-"; 
    }
    
    @Override
    public String getColumnSeparator(){ 
        return "|"; 
    }
}
