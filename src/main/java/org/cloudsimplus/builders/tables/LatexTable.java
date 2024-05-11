package org.cloudsimplus.builders.tables;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class LatexTable {
    private final List<List<Object>> rows;
    private final List<LatexTableColumn> columns;

    public LatexTable() {
        this.rows = new ArrayList<>();
        this.columns = new ArrayList<>();
    }

    public LatexTable addColumn(LatexTableColumn column) {
        columns.add(column);
        return this;
    }

    public LatexTable addRow(List<Object> rowData) {
        rows.add(rowData);
        return this;
    }

    public void print(String title) {
        // Table opening
        System.out.println("\\begin{table}[H]");
        System.out.println("\\centering");
        System.out.print("\\begin{tabular}{");

        // Adding column specifiers
        for (LatexTableColumn column : columns) {
            System.out.print("|" + column.getSpecifier());
        }
        System.out.println("|}");
        System.out.println("\\hline");

        // Adding column headers
        for (LatexTableColumn column : columns) {
            System.out.print(column.getName() + " & ");
        }
        System.out.println("\\\\");
        System.out.println("\\hline");

        // Adding rows
        for (List<Object> row : rows) {
            for (Object data : row) {
                System.out.print(data + " & ");
            }
            // Removing the last "&" and adding a line break
            System.out.println("\\\\");
        }

        // Table closing
        System.out.println("\\hline");
        System.out.println("\\end{tabular}");
        System.out.println("\\caption{"+title+"}");
        System.out.println("\\end{table}");
    }
}
