package org.cloudsimplus.builders.tables;

import java.util.List;

public class LatexTable extends AbstractTable {
   //private final List<List<Object>> rows;
    //private final List<LatexTableColumn> columns;

    public LatexTable() {
        super();
    }
    public LatexTable(final String title){
        super(title);
    }
    @Override
    protected void printTableOpening(){
        getPrintStream().printf("\\begin{table}[H]");
        getPrintStream().printf("\\centering");
        //getPrintStream().printf("\\begin{tabular}{");

    }

    @Override
    protected void printTitle() {
        
        getPrintStream().printf(" \\caption{%s}",getTitle());
        printTabularStart();
    }
    protected void printTabularStart(){
        getPrintStream().printf(" \\begin{tabular}{ ");
        colNbSpecifier();//nb of cols must be specified before building the table
        getPrintStream().printf("}");

    }

    public void colNbSpecifier() {
        // Adding column specifiers
        int x = this.colCount();
        String columnSpecs = "|c".repeat(x);
        getPrintStream().printf("%s", columnSpecs);

        for (TableColumn column : getColumns()) {
            getPrintStream().printf("|c");
            
        }
    }
    
    @Override
    protected String rowClosing(){
        return " \\\\ ";
    }
    protected void printTabularEnd(){
        getPrintStream().printf(" \\hline \\end{tabular} ");
    }
    @Override
    protected void printTableClosing(){
        printTabularEnd();
        getPrintStream().printf(" \\end{table} ");

    };
    @Override
    protected List<List<Object>> getRows() {
        //Auto-generated method stub
        return super.getRows();
    }

    @Override
    public TableColumn newColumn(final String title, final String subtitle, final String format) {
        return new LatexTableColumn(title, subtitle, format);
    }
    @Override
    protected String rowOpening() {
        return "\\hline ";
        
    }
    @Override
    protected String subtitleHeaderOpening() {
        return "";}
    
}
