package org.cloudsimplus.builders.tables;

/**
 * Generates tabular data in LaTeX format.
 * @author marwenbenhajhassine
 */
public class LatexTable extends AbstractTable {
    public LatexTable() {
        super();
        setColumnSeparator(" & ");
    }

    public LatexTable(final String title){
        super(title);
    }

    @Override
    protected void printTableOpening(){
        getPrintStream().println("\\begin{table}[h]");
        getPrintStream().println("\\centering");
    }

    @Override
    protected void printTitle() {
        getPrintStream().printf("  \\caption{%s}%n", getTitle());
        printTabularStart();
    }

    protected void printTabularStart(){
        getPrintStream().print("  \\begin{tabular}{ ");
        colNumberSpecifier(); // number of cols must be specified before building the table
        getPrintStream().println(" }");
    }

    /**
     * Adds column specifiers which creates columsn and define how they are aligned in the LaTex Table.
    */
    public void colNumberSpecifier() {
        final String columnSpecs = "|c".repeat(this.colCount());
        getPrintStream().printf("%s|", columnSpecs);
    }

    @Override
    protected String rowClosing(){
        return " \\\\%n";
    }

    @Override
    protected void printTableClosing(){
        getPrintStream().println("  \\hline");
        getPrintStream().println("  \\end{tabular}");
        getPrintStream().println("\\end{table} ");
    }

    @Override
    public TableColumn newColumn(final String title, final String subtitle, final String format) {
        return new LatexTableColumn(title, subtitle, format);
    }

    @Override
    protected String rowOpening() {
        return "    \\hline ";
    }

    @Override
    protected String subtitleHeaderOpening() {
        return "";
    }
}
