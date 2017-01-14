CsvTableColumn
==============

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class CsvTableColumn extends AbstractTableColumn

   A column of an CSV table. The class generates the CSV code that represents a column in a CSV table.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CsvTableColumn
^^^^^^^^^^^^^^

.. java:constructor:: public CsvTableColumn(TableBuilder table, String title)
   :outertype: CsvTableColumn

Methods
-------
generateData
^^^^^^^^^^^^

.. java:method:: @Override public String generateData(Object data)
   :outertype: CsvTableColumn

generateHeader
^^^^^^^^^^^^^^

.. java:method:: @Override protected String generateHeader(String title)
   :outertype: CsvTableColumn

