package org.cloudsimplus.builders.tables;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableBuilderAbstractTest {
    private static final List<Integer> DATA = List.of(1, 2);

    /// A builder with columns A, B and C, whose data is the number times 1, 10 and 100.
    private static final class NumbersTableBuilder extends TableBuilderAbstract<Integer> {
        private String format = "";

        NumbersTableBuilder() {
            super(DATA, new CsvTable());
        }

        NumbersTableBuilder setFormat(final String format) {
            this.format = format;
            return this;
        }

        @Override
        protected void createTableColumns() {
            addColumn(getTable().newColumn("A", "", format), n -> n);
            addColumn(getTable().newColumn("B", "", format), n -> n * 10);
            addColumn(getTable().newColumn("C", "", format), n -> n * 100);
        }

        AbstractTable table() {
            return (AbstractTable) getTable();
        }

        List<String> titles() {
            return getTable().getColumns().stream().map(TableColumn::getTitle).toList();
        }
    }

    @Test
    void columnCalledBeforeBuildChangesTheRequestedColumn() {
        final var builder = new NumbersTableBuilder();
        builder.column(1, col -> col.setFormat("%d!")).build();

        final var cols = builder.getTable().getColumns();
        assertEquals("", cols.get(0).getFormat());
        assertEquals("%d!", cols.get(1).getFormat());
        assertEquals("", cols.get(2).getFormat());
    }

    @Test
    void removeColumnRemovesHeaderAndDataOfThatColumnOnly() {
        final var builder = new NumbersTableBuilder();
        builder.removeColumn(1).build();

        assertEquals(List.of("A", "C"), builder.titles());
        assertEquals(List.of(List.of(1, 100), List.of(2, 200)), builder.table().getRows());
    }

    @Test
    void removeColumnsUsesIndexesBeforeAnyRemoval() {
        final var builder = new NumbersTableBuilder();
        builder.removeColumn(0, 1).build();

        assertEquals(List.of("C"), builder.titles());
        assertEquals(List.of(List.of(100), List.of(200)), builder.table().getRows());
    }

    @Test
    void formatSetAfterColumnActionsIsStillApplied() {
        final var builder = new NumbersTableBuilder();
        builder.column(0, col -> col.setFormat("%d!")).removeColumn(2);
        builder.setFormat("%d#").build();

        final var cols = builder.getTable().getColumns();
        assertEquals("%d!", cols.get(0).getFormat());
        assertEquals("%d#", cols.get(1).getFormat());
        assertEquals(List.of("A", "B"), builder.titles());
    }
}
