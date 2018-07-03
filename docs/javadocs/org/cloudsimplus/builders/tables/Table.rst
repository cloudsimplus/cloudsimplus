.. java:import:: java.util List

Table
=====

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public interface Table

   An interface for classes that generate tables from a given data set, following the Builder Design Pattern.

   :author: Manoel Campos da Silva Filho

Methods
-------
addColumn
^^^^^^^^^

.. java:method::  TableColumn addColumn(String columnTitle)
   :outertype: Table

   Adds a column with a given to the end of the table's columns to be printed.

   :param columnTitle: The title of the column to be added.
   :return: The created column.

addColumn
^^^^^^^^^

.. java:method::  TableColumn addColumn(int index, String columnTitle)
   :outertype: Table

   Adds a column with a given title to the end of the table's columns to be printed.

   :param index: the position to insert the column into the column's list
   :param columnTitle: The title of the column to be added.
   :return: the created column

addColumn
^^^^^^^^^

.. java:method::  TableColumn addColumn(String columnTitle, String columnSubTitle)
   :outertype: Table

   Adds a column with a given title and sub-title to the end of the table's columns to be printed.

   :param columnTitle: The title of the column to be added.
   :param columnSubTitle: The sub-title of the column to be added.
   :return: the created column

addColumn
^^^^^^^^^

.. java:method::  TableColumn addColumn(int index, TableColumn column)
   :outertype: Table

   Adds a column object to a specific position of the table's columns to be printed.

   :param index: the position to insert the column into the column's list
   :param column: The column to be added.
   :return: the created column

addColumn
^^^^^^^^^

.. java:method::  TableColumn addColumn(TableColumn column)
   :outertype: Table

   Adds a column object to the end of the table's columns to be printed.

   :param column: The column to be added.
   :return: the created column

addColumnList
^^^^^^^^^^^^^

.. java:method::  Table addColumnList(String... columnTitles)
   :outertype: Table

   Adds a list of columns (with given titles) to the end of the table's columns to be printed, where the column data will be printed without a specific format.

   :param columnTitles: The titles of the columns
   :return: the \ :java:ref:`Table`\  instance.

   **See also:** :java:ref:`.addColumn(String)`

getColumnSeparator
^^^^^^^^^^^^^^^^^^

.. java:method::  String getColumnSeparator()
   :outertype: Table

   Gets the string used to separate one column from another (optional).

getColumns
^^^^^^^^^^

.. java:method::  List<TableColumn> getColumns()
   :outertype: Table

   :return: the list of columns of the table

getTitle
^^^^^^^^

.. java:method::  String getTitle()
   :outertype: Table

   :return: the table title

newRow
^^^^^^

.. java:method::  List<Object> newRow()
   :outertype: Table

   Adds a new row to the list of rows containing the data to be printed.

print
^^^^^

.. java:method::  void print()
   :outertype: Table

   Prints the table.

setColumnSeparator
^^^^^^^^^^^^^^^^^^

.. java:method::  Table setColumnSeparator(String columnSeparator)
   :outertype: Table

   Sets the string used to separate one column from another (optional).

   :param columnSeparator: the separator to set

setTitle
^^^^^^^^

.. java:method::  Table setTitle(String title)
   :outertype: Table

   :param title: the table title to set
   :return: The Table instance

