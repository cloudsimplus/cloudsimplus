.. java:import:: org.cloudbus.cloudsim.util Log

HtmlTableBuilder
================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class HtmlTableBuilder extends AbstractTableBuilder

   A generator of HTML tables.

   :author: Manoel Campos da Silva Filho

Constructors
------------
HtmlTableBuilder
^^^^^^^^^^^^^^^^

.. java:constructor:: public HtmlTableBuilder()
   :outertype: HtmlTableBuilder

HtmlTableBuilder
^^^^^^^^^^^^^^^^

.. java:constructor:: public HtmlTableBuilder(String title)
   :outertype: HtmlTableBuilder

   Creates an TableBuilder

   :param title: Title of the table

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: @Override public TableColumn addColumn(int index, String columnTitle)
   :outertype: HtmlTableBuilder

printRowClosing
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowClosing()
   :outertype: HtmlTableBuilder

printRowOpening
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printRowOpening()
   :outertype: HtmlTableBuilder

printTableClosing
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printTableClosing()
   :outertype: HtmlTableBuilder

printTableOpening
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void printTableOpening()
   :outertype: HtmlTableBuilder

printTitle
^^^^^^^^^^

.. java:method:: @Override protected void printTitle()
   :outertype: HtmlTableBuilder

