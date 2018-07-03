.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts HostStateHistoryEntry

HostHistoryTableBuilder
=======================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class HostHistoryTableBuilder extends TableBuilderAbstract<HostStateHistoryEntry>

   Builds a table for printing \ :java:ref:`HostStateHistoryEntry`\  entries from the \ :java:ref:`Host.getStateHistory()`\ . It defines a set of default columns but new ones can be added dynamically using the \ ``addColumn()``\  methods.

   The basic usage of the class is by calling its constructor, giving a Host to print its history, and then calling the \ :java:ref:`build()`\  method.

   :author: Manoel Campos da Silva Filho

Constructors
------------
HostHistoryTableBuilder
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostHistoryTableBuilder(Host host)
   :outertype: HostHistoryTableBuilder

   Instantiates a builder to print the history of a Host using the a default \ :java:ref:`TextTable`\ . To use a different \ :java:ref:`Table`\ , check the alternative constructors.

   :param host: the Host to get the history to print

HostHistoryTableBuilder
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HostHistoryTableBuilder(Host host, Table table)
   :outertype: HostHistoryTableBuilder

   Instantiates a builder to print the history of a Host using the a given \ :java:ref:`Table`\ .

   :param host: the Host to get the history to print
   :param table: the \ :java:ref:`Table`\  used to build the table with the Cloudlets data

Methods
-------
createTableColumns
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void createTableColumns()
   :outertype: HostHistoryTableBuilder

