package org.cloudbus.cloudsim.util;

import java.util.List;
import org.cloudbus.cloudsim.Log;

/**
 * A generator of HTML tables.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class HtmlTableBuilder extends AbstractTableBuilder {
    public HtmlTableBuilder() {
        super();
    }

    /**
     * Creates an TableBuilder
     * @param title Title of the table
     */
    public HtmlTableBuilder(final String title) {
        super(title);
    }
    
    @Override
    protected void printTableOpenning() {
        Log.printLine("\n<table>");
    }

    @Override
    protected void printTitle() {
        Log.printFormatted("  <caption>%s</caption>\n", getTitle());
    }

    @Override
    protected void printRowOpenning() {
        Log.printLine("  <tr>");
    }

    @Override
    protected void printColumn(List<Object> row, int columnIndex) {
        Log.printFormatted("%s<td>%s</td>", identLine(columnIndex), getFormatedColumnData(row, columnIndex));
    }

    public String identLine(int columnIndex) {
        return columnIndex == 0 ? "    " : "";
    }

    @Override
    protected void printRowClosing() {
        Log.printLine("\n  </tr>");
    }

    @Override
    protected void printTableClosing() {
        Log.printLine("</table>\n");
    }

    @Override
    protected void printColumnHeader(int columnIndex) {
        Log.printFormatted("%s<th>%s</th>", identLine(columnIndex), getColumnHeaders().get(columnIndex));
    }
}
