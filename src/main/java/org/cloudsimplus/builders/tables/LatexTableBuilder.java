package org.cloudsimplus.builders.tables;

import java.util.ArrayList;
import java.util.List;

public class LatexTableBuilder {
    private final LatexTable table;

    public LatexTableBuilder() {
        this.table = new LatexTable();
    }

    public LatexTableBuilder addColumn(LatexTableColumn column) {
        table.addColumn(column);
        return this;
    }

    public LatexTableBuilder addRow(List<Object> rowData) {
        table.addRow(rowData);
        return this;
    }

    public void build(String title) {
        table.print(title);
    }
}
