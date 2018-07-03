org.cloudsimplus.builders.tables
================================

Provides \ :java:ref:`org.cloudsimplus.builders.tables.Table`\  classes that are used to format simulation results in different and structured ways such as ASCII, CSV or HTML tables. Such tables can even be used by external softwares to process simulation results.

All the examples use some \ :java:ref:`org.cloudsimplus.builders.tables.Table`\  implementation to print simulation results.

The classes and interfaces provided allow creating custom TableBuilders to add, change or remove columns from the results, to sort rows, to filter, and so on.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudsimplus.builders.tables

.. toctree::
   :maxdepth: 1

   AbstractTable
   AbstractTableColumn
   CloudletsTableBuilder
   CsvTable
   CsvTableColumn
   HostHistoryTableBuilder
   HtmlTable
   HtmlTableColumn
   Table
   TableBuilderAbstract
   TableColumn
   TextTable
   TextTableColumn

