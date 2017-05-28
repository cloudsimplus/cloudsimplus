.. java:import:: org.cloudbus.cloudsim.util Log

CsvTableBuilder
===============

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class CsvTableBuilder extends AbstractTableBuilder

   Prints a table from a given data set, using a Comma Separated Text (CSV) format.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CsvTableBuilder
^^^^^^^^^^^^^^^

.. java:constructor:: public CsvTableBuilder()
   :outertype: CsvTableBuilder

CsvTableBuilder
^^^^^^^^^^^^^^^

.. java:constructor:: public CsvTableBuilder(String title)
   :outertype: CsvTableBuilder

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: @Override public TableColumn addColumn(int index, String columnTitle)
   :outertype: CsvTableBuilder

getLineSeparator
^^^^^^^^^^^^^^^^

.. java:method:: public String getLineSeparator()
   :outertype: CsvTableBuilder

printRowClosing
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowClosing()
   :outertype: CsvTableBuilder

printRowOpening
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowOpening()
   :outertype: CsvTableBuilder

   CSV files doesn't have a row opening line.

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableClosing()
   :outertype: CsvTableBuilder

   CSV files doesn't have a table closing line.

printTableOpening
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableOpening()
   :outertype: CsvTableBuilder

   CSV files doesn't have a table opening line.

printTitle
^^^^^^^^^^

.. java:method:: @Override public void printTitle()
   :outertype: CsvTableBuilder

   CSV files doesn't have a title.

