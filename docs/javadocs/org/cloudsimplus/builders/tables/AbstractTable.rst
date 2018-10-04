.. java:import:: org.apache.commons.lang3 StringUtils

.. java:import:: java.io PrintStream

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

AbstractTable
=============

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public abstract class AbstractTable implements Table

   An abstract base class for implementing data tables.

   :author: Manoel Campos da Silva Filho

Constructors
------------
AbstractTable
^^^^^^^^^^^^^

.. java:constructor:: public AbstractTable()
   :outertype: AbstractTable

AbstractTable
^^^^^^^^^^^^^

.. java:constructor:: public AbstractTable(String title)
   :outertype: AbstractTable

   Creates an Table

   :param title: Title of the table

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: @Override public final TableColumn addColumn(String columnTitle)
   :outertype: AbstractTable

addColumn
^^^^^^^^^

.. java:method:: @Override public final TableColumn addColumn(String columnTitle, String columnSubTitle)
   :outertype: AbstractTable

addColumn
^^^^^^^^^

.. java:method:: @Override public final TableColumn addColumn(int index, TableColumn column)
   :outertype: AbstractTable

addColumn
^^^^^^^^^

.. java:method:: @Override public final TableColumn addColumn(TableColumn column)
   :outertype: AbstractTable

addColumnList
^^^^^^^^^^^^^

.. java:method:: @Override public final Table addColumnList(String... columnTitles)
   :outertype: AbstractTable

getColumnSeparator
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public String getColumnSeparator()
   :outertype: AbstractTable

getColumns
^^^^^^^^^^

.. java:method:: @Override public List<TableColumn> getColumns()
   :outertype: AbstractTable

   :return: the list of columns of the table

getPrintStream
^^^^^^^^^^^^^^

.. java:method:: protected PrintStream getPrintStream()
   :outertype: AbstractTable

   Gets the \ :java:ref:`PrintStream`\  used to print the generated table.

   :return: the \ :java:ref:`PrintStream`\

getRows
^^^^^^^

.. java:method:: protected List<List<Object>> getRows()
   :outertype: AbstractTable

   :return: The data to be printed, where each row contains a list of data columns.

getTitle
^^^^^^^^

.. java:method:: @Override public String getTitle()
   :outertype: AbstractTable

newRow
^^^^^^

.. java:method:: @Override public List<Object> newRow()
   :outertype: AbstractTable

print
^^^^^

.. java:method:: @Override public void print()
   :outertype: AbstractTable

printColumnHeaders
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void printColumnHeaders()
   :outertype: AbstractTable

printRowClosing
^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printRowClosing()
   :outertype: AbstractTable

   Prints the string to close a row.

printRowOpening
^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printRowOpening()
   :outertype: AbstractTable

   Prints the string that has to precede each printed row.

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printTableClosing()
   :outertype: AbstractTable

   Prints the string to close the table.

printTableOpening
^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printTableOpening()
   :outertype: AbstractTable

   Prints the string to open the table.

printTitle
^^^^^^^^^^

.. java:method:: protected abstract void printTitle()
   :outertype: AbstractTable

   Prints the table title.

setColumnSeparator
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Table setColumnSeparator(String columnSeparator)
   :outertype: AbstractTable

setPrintStream
^^^^^^^^^^^^^^

.. java:method:: public void setPrintStream(PrintStream printStream)
   :outertype: AbstractTable

   Sets the \ :java:ref:`PrintStream`\  used to print the generated table.

   :param printStream: the \ :java:ref:`PrintStream`\  to set

setTitle
^^^^^^^^

.. java:method:: @Override public final Table setTitle(String title)
   :outertype: AbstractTable

