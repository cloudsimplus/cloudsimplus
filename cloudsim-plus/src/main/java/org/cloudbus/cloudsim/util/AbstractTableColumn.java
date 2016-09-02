package org.cloudbus.cloudsim.util;

/**
 * A column of a table to be generated using a {@link TableBuilder} class.
 * @author Manoel Campos da Silva Filho
 */
public abstract class AbstractTableColumn implements TableColumn {
    /**
     * @see #getTitle() 
     */
    private String title;
    
    /**
     * @see #getSubTitle() 
     */
    private String subTitle;
    
    /**
     * @see #getFormat() 
     */
    private String format;
    
    /**
     * @see #getTable() 
     */
    private TableBuilder table;
    
    /**
     * @see #getColumnSeparator() 
     */
    private String columnSeparator;    

    /**
     * Creates a column with a specific title.
     * @param table The table that the column belongs to.
     * @param title The column title.
     */
    public AbstractTableColumn(TableBuilder table, final String title) {
        this.table = table;
        this.title = title;
        this.setFormat("");
        this.subTitle = "";
        this.columnSeparator = "";
    }

    /**
     * 
     * @return The title to be displayed at the top of the column.
     */
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public AbstractTableColumn setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 
     * @return The subtitle to be displayed below the title of the column (optional).
     */
    @Override
    public String getSubTitle() {
        return subTitle;
    }

    @Override
    public AbstractTableColumn setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    /**
     * 
     * @return The format to be used to display the content of the column,
     * according to the {@link String#format(java.lang.String, java.lang.Object...)} (optional).
     */
    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public final AbstractTableColumn setFormat(String format) {
        this.format = format;
        return this;
    }
    
    @Override
    public String toString() {
        return getTitle();
    }    

    /**
     * 
     * @return The table that the column belongs to.
     */
    @Override
    public TableBuilder getTable() {
        return table;
    }
    
    
    @Override
    public AbstractTableColumn setTable(TableBuilder table) {
        this.table = table;
        return this;
    }
    
    /**
     * Generates the string that represents the data of the column,
     * formatted according to the {@link #getFormat() format}. 
     * @param data The data of the column to be formatted
     * @return a string containing the formatted column data
     */
    @Override
    public String generateData(final Object data){
        if(format.trim().isEmpty())
            return String.valueOf(data);
        
        return String.format(format, data);        
    }
    
    /**
     * Generates a header for the column, either for the title or subtitle header.
     * 
     * @param title header title or subtitle
     * @return the generated header string
     */
    protected abstract String generateHeader(String title);

    @Override
    public String generateTitleHeader() {
        return generateHeader(title);
    }

    @Override
    public String generateSubtitleHeader() {
        return generateHeader(subTitle);
    }
    
    @Override
    public String getColumnSeparator(){
        return columnSeparator;
    }

    @Override
    public final TableColumn setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
        return this;
    }
    
    /**
     * 
     * @return The index of the current column into the
     * column list of the {@link #getTable() TableBuilder}.
     */
    protected int getIndex() {
        return getTable().getColumns().indexOf(this);
    }
    
    /**
     * Indicates if the current column is the last one
     * in the column list of the {@link #getTable() TableBuilder}.
     * @return true if it is the last column, false otherwise.
     */
    protected boolean isLastColumn() {
        return getIndex() == getTable().getColumns().size()-1;
    }
}
