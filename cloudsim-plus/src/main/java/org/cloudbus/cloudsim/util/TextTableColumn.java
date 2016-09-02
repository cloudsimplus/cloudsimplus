package org.cloudbus.cloudsim.util;

/**
 * A column of an text (ASCII) table. The class generates the string
 * that represents a column in a text table.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class TextTableColumn extends CsvTableColumn {

    public TextTableColumn(TableBuilder table, String title) {
        super(table, title);
        setColumnSeparator("|");
    }

    @Override
    public String generateData(Object data) {
        return alignStringRight(super.generateData(data));
    }    

    /**
     * Align a string to the right side, based on the length of the title
     * header of the column.
     * @param str the string to be aligned
     * @return the aligned string
     */
    private String alignStringRight(String str) {
        final String fmt = String.format("%%%ds", generateTitleHeader().length());
        return String.format(fmt, str);
    }    

    @Override
    public String generateSubtitleHeader() {
        return alignStringRight(super.generateSubtitleHeader()); 
    }

  
}
