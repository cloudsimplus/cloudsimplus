.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.util Log

AbstractTableBuilder
====================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public abstract class AbstractTableBuilder implements TableBuilder

   An abstract base class for implementing table builders.

   :author: Manoel Campos da Silva Filho

Constructors
------------
AbstractTableBuilder
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractTableBuilder()
   :outertype: AbstractTableBuilder

AbstractTableBuilder
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractTableBuilder(String title)
   :outertype: AbstractTableBuilder

   Creates an TableBuilder

   :param title: Title of the table

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: @Override public final TableColumn addColumn(String columnTitle)
   :outertype: AbstractTableBuilder

addColumn
^^^^^^^^^

.. java:method:: @Override public final TableColumn addColumn(String columnTitle, String columnSubTitle)
   :outertype: AbstractTableBuilder

addColumn
^^^^^^^^^

.. java:method:: @Override public final TableColumn addColumn(int index, TableColumn column)
   :outertype: AbstractTableBuilder

addColumn
^^^^^^^^^

.. java:method:: @Override public final TableColumn addColumn(TableColumn column)
   :outertype: AbstractTableBuilder

addColumnList
^^^^^^^^^^^^^

.. java:method:: @Override public final TableBuilder addColumnList(String... columnTitles)
   :outertype: AbstractTableBuilder

getColumnSeparator
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public String getColumnSeparator()
   :outertype: AbstractTableBuilder

getColumns
^^^^^^^^^^

.. java:method:: @Override public List<TableColumn> getColumns()
   :outertype: AbstractTableBuilder

   :return: the list of columns of the table

getRows
^^^^^^^

.. java:method:: protected List<List<Object>> getRows()
   :outertype: AbstractTableBuilder

   :return: The data to be printed, where each row contains a list of data columns.

getTitle
^^^^^^^^

.. java:method:: @Override public String getTitle()
   :outertype: AbstractTableBuilder

newRow
^^^^^^

.. java:method:: @Override public List<Object> newRow()
   :outertype: AbstractTableBuilder

print
^^^^^

.. java:method:: @Override public void print()
   :outertype: AbstractTableBuilder

printColumnHeaders
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void printColumnHeaders()
   :outertype: AbstractTableBuilder

printRowClosing
^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printRowClosing()
   :outertype: AbstractTableBuilder

   Prints the string to close a row.

printRowOpening
^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printRowOpening()
   :outertype: AbstractTableBuilder

   Prints the string that has to precede each printed row.

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printTableClosing()
   :outertype: AbstractTableBuilder

   Prints the string to close the table.

printTableOpening
^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printTableOpening()
   :outertype: AbstractTableBuilder

   Prints the string to open the table.

printTitle
^^^^^^^^^^

.. java:method:: protected abstract void printTitle()
   :outertype: AbstractTableBuilder

   Prints the table title.

setColumnSeparator
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final TableBuilder setColumnSeparator(String columnSeparator)
   :outertype: AbstractTableBuilder

setTitle
^^^^^^^^

.. java:method:: @Override public final TableBuilder setTitle(String title)
   :outertype: AbstractTableBuilder

