.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core Identifiable

.. java:import:: java.util List

CloudletsTableBuilder
=====================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class CloudletsTableBuilder extends TableBuilderAbstract<Cloudlet>

   Builds a table for printing simulation results from a list of Cloudlets. It defines a set of default columns but new ones can be added dynamically using the \ ``addColumn()``\  methods.

   The basic usage of the class is by calling its constructor, giving a list of Cloudlets to be printed, and then calling the \ :java:ref:`build()`\  method.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CloudletsTableBuilder
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletsTableBuilder(List<? extends Cloudlet> list)
   :outertype: CloudletsTableBuilder

   Instantiates a builder to print the list of Cloudlets using the a default \ :java:ref:`TextTable`\ . To use a different \ :java:ref:`Table`\ , check the alternative constructors.

   :param list: the list of Cloudlets to print

CloudletsTableBuilder
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletsTableBuilder(List<? extends Cloudlet> list, Table table)
   :outertype: CloudletsTableBuilder

   Instantiates a builder to print the list of Cloudlets using the a given \ :java:ref:`Table`\ .

   :param list: the list of Cloudlets to print
   :param table: the \ :java:ref:`Table`\  used to build the table with the Cloudlets data

Methods
-------
createTableColumns
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void createTableColumns()
   :outertype: CloudletsTableBuilder

