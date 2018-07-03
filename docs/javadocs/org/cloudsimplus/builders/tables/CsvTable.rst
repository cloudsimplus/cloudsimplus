CsvTable
========

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class CsvTable extends AbstractTable

   Prints a table from a given data set, using a Comma Separated Text (CSV) format.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CsvTable
^^^^^^^^

.. java:constructor:: public CsvTable()
   :outertype: CsvTable

CsvTable
^^^^^^^^

.. java:constructor:: public CsvTable(String title)
   :outertype: CsvTable

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: @Override public TableColumn addColumn(int index, String columnTitle)
   :outertype: CsvTable

getLineSeparator
^^^^^^^^^^^^^^^^

.. java:method:: public String getLineSeparator()
   :outertype: CsvTable

printRowClosing
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowClosing()
   :outertype: CsvTable

printRowOpening
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowOpening()
   :outertype: CsvTable

   CSV files doesn't have a row opening line.

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableClosing()
   :outertype: CsvTable

   CSV files doesn't have a table closing line.

printTableOpening
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableOpening()
   :outertype: CsvTable

   CSV files doesn't have a table opening line.

printTitle
^^^^^^^^^^

.. java:method:: @Override public void printTitle()
   :outertype: CsvTable

   CSV files doesn't have a title.

