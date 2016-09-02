package org.cloudbus.cloudsim.util;

/**
 * An interface that represents a column of a table generated
 * using a {@link TableBuilder}.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface TableColumn {

    /**
     * Generates the string that represents the data of the column,
     * formatted according to the {@link #getFormat() format}.
     * @param data The data of the column to be formatted
     * @return a string containing the formatted column data
     */
    String generateData(final Object data);

    /**
     * Generates the string that represents the header of the column,
     * containing the column title.
     * @return the generated header string
     */
    String generateTitleHeader();

    /**
     * Generates the string that represents the sub-header of the column (if any),
     * containing the column subtitle.
     * @return the generated sub-header string
     */
    String generateSubtitleHeader();
    /**
     *
     * @return The format to be used to display the content of the column,
     * according to the {@link String#format(java.lang.String, java.lang.Object...)} (optional).
     */
    String getFormat();

    /**
     *
     * @return The subtitle to be displayed below the title of the column (optional).
     */
    String getSubTitle();

    /**
     *
     * @return The table that the column belongs to.
     */
    TableBuilder getTable();

    /**
     *
     * @return The title to be displayed at the top of the column.
     */
    String getTitle();

    TableColumn setFormat(String format);

    TableColumn setSubTitle(String subTitle);

    TableColumn setTable(TableBuilder table);

    TableColumn setTitle(String title);
    
    /**
     * @return The string used to separate one column from another (optional).
     */
    String getColumnSeparator();
    
    TableColumn setColumnSeparator(String columnSeparator);
    
}
