package org.cloudsimplus.builders.tables;

import lombok.Getter;

/**
 * A column for a {@link LatexTable}
 * @author marwenbenhajhassine
 */
@Getter
public class LatexTableColumn extends AbstractTableColumn {
    public LatexTableColumn(final String title, final String subtitle) {
        this(title, subtitle, "");
    }

    public LatexTableColumn(final String title) {
        this(title, "", "");
    }

    public LatexTableColumn(final Table table, final String title) {
        super(table, title);
    }

    public LatexTableColumn(final Table table, final String title, final String subTitle) {
        super(table, title, subTitle);
    }

    public LatexTableColumn(final String title, final String subTitle, final String format) {
        super(title, subTitle, format);
    }

    @Override
    protected String generateHeader(final String str) {
        return "\\textbf{%s}".formatted(str);
    }
}
