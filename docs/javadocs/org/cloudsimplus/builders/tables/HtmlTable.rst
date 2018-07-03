HtmlTable
=========

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class HtmlTable extends AbstractTable

   A generator of HTML tables.

   :author: Manoel Campos da Silva Filho

Constructors
------------
HtmlTable
^^^^^^^^^

.. java:constructor:: public HtmlTable()
   :outertype: HtmlTable

HtmlTable
^^^^^^^^^

.. java:constructor:: public HtmlTable(String title)
   :outertype: HtmlTable

   Creates an Table

   :param title: Title of the table

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: @Override public TableColumn addColumn(int index, String columnTitle)
   :outertype: HtmlTable

printRowClosing
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowClosing()
   :outertype: HtmlTable

printRowOpening
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowOpening()
   :outertype: HtmlTable

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printTableClosing()
   :outertype: HtmlTable

printTableOpening
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printTableOpening()
   :outertype: HtmlTable

printTitle
^^^^^^^^^^

.. java:method:: @Override protected void printTitle()
   :outertype: HtmlTable

