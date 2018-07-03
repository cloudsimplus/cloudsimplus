CsvTableColumn
==============

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class CsvTableColumn extends AbstractTableColumn

   A column of an CSV table. The class generates the CSV code that represents a column in a CSV table.

   :author: Manoel Campos da Silva Filho

Fields
------
DATA_COL_SEPARATOR_FORMAT
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String DATA_COL_SEPARATOR_FORMAT
   :outertype: CsvTableColumn

   A format used to print data followed by the column separator.

Constructors
------------
CsvTableColumn
^^^^^^^^^^^^^^

.. java:constructor:: public CsvTableColumn(String title, String subTitle)
   :outertype: CsvTableColumn

CsvTableColumn
^^^^^^^^^^^^^^

.. java:constructor:: public CsvTableColumn(String title)
   :outertype: CsvTableColumn

CsvTableColumn
^^^^^^^^^^^^^^

.. java:constructor:: public CsvTableColumn(Table table, String title, String subTitle)
   :outertype: CsvTableColumn

CsvTableColumn
^^^^^^^^^^^^^^

.. java:constructor:: public CsvTableColumn(Table table, String title)
   :outertype: CsvTableColumn

Methods
-------
generateData
^^^^^^^^^^^^

.. java:method:: @Override public String generateData(Object data)
   :outertype: CsvTableColumn

generateHeader
^^^^^^^^^^^^^^

.. java:method:: @Override protected String generateHeader(String str)
   :outertype: CsvTableColumn

