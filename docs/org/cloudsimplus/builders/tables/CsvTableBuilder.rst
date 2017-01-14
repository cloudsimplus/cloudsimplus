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

.. java:method:: @Override public TableColumn addColumn(String columnTitle)
   :outertype: CsvTableBuilder

getLineSeparator
^^^^^^^^^^^^^^^^

.. java:method:: public String getLineSeparator()
   :outertype: CsvTableBuilder

printRowClosing
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowClosing()
   :outertype: CsvTableBuilder

printRowOpenning
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowOpenning()
   :outertype: CsvTableBuilder

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableClosing()
   :outertype: CsvTableBuilder

printTableOpenning
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableOpenning()
   :outertype: CsvTableBuilder

printTitle
^^^^^^^^^^

.. java:method:: @Override public void printTitle()
   :outertype: CsvTableBuilder

