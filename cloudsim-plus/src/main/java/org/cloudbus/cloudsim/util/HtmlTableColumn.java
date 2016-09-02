package org.cloudbus.cloudsim.util;

/**
 * A column of an HTML table. The class generates the HTML code
 * that represents a column in a HTML table.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class HtmlTableColumn extends AbstractTableColumn {

    public HtmlTableColumn(TableBuilder table, String title) {
        super(table, title);
    }
    
    private String identLine(int columnIndex) {
        return columnIndex == 0 ? "    " : "";
    }

    @Override
    protected String generateHeader(String title) {
        final int index = getTable().getColumns().indexOf(this);
        return String.format("%s<th>%s</th>", identLine(index), title);
    }

    @Override
    public String generateData(Object data) {
        final int index = getTable().getColumns().indexOf(this);
        return String.format("%s<td>%s</td>", identLine(index), super.generateData(data));
    }
    
}
