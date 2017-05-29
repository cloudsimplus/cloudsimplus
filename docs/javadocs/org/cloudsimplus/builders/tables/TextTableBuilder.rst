.. java:import:: org.apache.commons.lang3 StringUtils

.. java:import:: org.cloudbus.cloudsim.util Log

TextTableBuilder
================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class TextTableBuilder extends CsvTableBuilder

   Prints a table from a given data set, using a simple delimited text format.

   :author: Manoel Campos da Silva Filho

Constructors
------------
TextTableBuilder
^^^^^^^^^^^^^^^^

.. java:constructor:: public TextTableBuilder()
   :outertype: TextTableBuilder

TextTableBuilder
^^^^^^^^^^^^^^^^

.. java:constructor:: public TextTableBuilder(String title)
   :outertype: TextTableBuilder

   Creates an TableBuilder

   :param title: Title of the table

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: @Override public TableColumn addColumn(int index, String columnTitle)
   :outertype: TextTableBuilder

getLineSeparator
^^^^^^^^^^^^^^^^

.. java:method:: @Override public String getLineSeparator()
   :outertype: TextTableBuilder

printColumnHeaders
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printColumnHeaders()
   :outertype: TextTableBuilder

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableClosing()
   :outertype: TextTableBuilder

printTableOpening
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void printTableOpening()
   :outertype: TextTableBuilder

printTitle
^^^^^^^^^^

.. java:method:: @Override public void printTitle()
   :outertype: TextTableBuilder

