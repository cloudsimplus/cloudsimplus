.. java:import:: org.apache.commons.lang3 StringUtils

TextTable
=========

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class TextTable extends CsvTable

   Prints a table from a given data set, using a simple delimited text format.

   :author: Manoel Campos da Silva Filho

Constructors
------------
TextTable
^^^^^^^^^

.. java:constructor:: public TextTable()
   :outertype: TextTable

TextTable
^^^^^^^^^

.. java:constructor:: public TextTable(String title)
   :outertype: TextTable

   Creates an Table

   :param title: Title of the table

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: @Override public TableColumn addColumn(int index, String columnTitle)
   :outertype: TextTable

getLineSeparator
^^^^^^^^^^^^^^^^

.. java:method:: @Override public String getLineSeparator()
   :outertype: TextTable

printColumnHeaders
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printColumnHeaders()
   :outertype: TextTable

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableClosing()
   :outertype: TextTable

printTableOpening
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableOpening()
   :outertype: TextTable

printTitle
^^^^^^^^^^

.. java:method:: @Override public void printTitle()
   :outertype: TextTable

