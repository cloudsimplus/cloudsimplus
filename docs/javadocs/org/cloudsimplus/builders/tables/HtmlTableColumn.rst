HtmlTableColumn
===============

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class HtmlTableColumn extends AbstractTableColumn

   A column of an HTML table. The class generates the HTML code that represents a column in a HTML table.

   :author: Manoel Campos da Silva Filho

Constructors
------------
HtmlTableColumn
^^^^^^^^^^^^^^^

.. java:constructor:: public HtmlTableColumn(String title, String subTitle)
   :outertype: HtmlTableColumn

HtmlTableColumn
^^^^^^^^^^^^^^^

.. java:constructor:: public HtmlTableColumn(String title)
   :outertype: HtmlTableColumn

HtmlTableColumn
^^^^^^^^^^^^^^^

.. java:constructor:: public HtmlTableColumn(Table table, String title)
   :outertype: HtmlTableColumn

HtmlTableColumn
^^^^^^^^^^^^^^^

.. java:constructor:: public HtmlTableColumn(Table table, String title, String subTitle)
   :outertype: HtmlTableColumn

Methods
-------
generateData
^^^^^^^^^^^^

.. java:method:: @Override public String generateData(Object data)
   :outertype: HtmlTableColumn

generateHeader
^^^^^^^^^^^^^^

.. java:method:: @Override protected String generateHeader(String str)
   :outertype: HtmlTableColumn

