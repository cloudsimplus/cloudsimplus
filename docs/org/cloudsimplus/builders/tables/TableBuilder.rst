.. java:import:: java.util List

TableBuilder
============

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public interface TableBuilder

   An interface for classes that generate tables from a given data set, following the Builder Design Pattern.

   :author: Manoel Campos da Silva Filho

Methods
-------
addColumn
^^^^^^^^^

.. java:method::  TableColumn addColumn(String columnTitle)
   :outertype: TableBuilder

   Adds a column to the table to be printed.

   :param columnTitle: The title of the column to be added.
   :return: The created column.

addColumnList
^^^^^^^^^^^^^

.. java:method::  TableBuilder addColumnList(String... columnTitles)
   :outertype: TableBuilder

   Adds a list of columns to the table to be printed, where the column data will be printed without a specific format.

   :param columnTitles: The titles of the columns
   :return: The \ :java:ref:`TableBuilder`\  instance.

   **See also:** :java:ref:`.addColumn(String)`

getColumns
^^^^^^^^^^

.. java:method::  List<TableColumn> getColumns()
   :outertype: TableBuilder

   :return: the list of columns of the table

getTitle
^^^^^^^^

.. java:method::  String getTitle()
   :outertype: TableBuilder

   :return: the table title

newRow
^^^^^^

.. java:method::  List<Object> newRow()
   :outertype: TableBuilder

   Adds a new row to the list of rows containing the data to be printed.

print
^^^^^

.. java:method::  void print()
   :outertype: TableBuilder

   Builds and prints the table.

setTitle
^^^^^^^^

.. java:method::  TableBuilder setTitle(String title)
   :outertype: TableBuilder

   :param title: the table title to set
   :return: The TableBuilder instance

