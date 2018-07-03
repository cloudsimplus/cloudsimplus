TextTableColumn
===============

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class TextTableColumn extends CsvTableColumn

   A column of an text (ASCII) table. The class generates the string that represents a column in a text table.

   :author: Manoel Campos da Silva Filho

Constructors
------------
TextTableColumn
^^^^^^^^^^^^^^^

.. java:constructor:: public TextTableColumn(String title, String subTitle)
   :outertype: TextTableColumn

TextTableColumn
^^^^^^^^^^^^^^^

.. java:constructor:: public TextTableColumn(String title)
   :outertype: TextTableColumn

TextTableColumn
^^^^^^^^^^^^^^^

.. java:constructor:: public TextTableColumn(Table table, String title, String subTitle)
   :outertype: TextTableColumn

TextTableColumn
^^^^^^^^^^^^^^^

.. java:constructor:: public TextTableColumn(Table table, String title)
   :outertype: TextTableColumn

Methods
-------
generateData
^^^^^^^^^^^^

.. java:method:: @Override public String generateData(Object data)
   :outertype: TextTableColumn

generateSubtitleHeader
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public String generateSubtitleHeader()
   :outertype: TextTableColumn

