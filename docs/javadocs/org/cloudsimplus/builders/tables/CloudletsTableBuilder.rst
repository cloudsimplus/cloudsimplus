.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Objects

.. java:import:: java.util.function Function

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core Identificable

CloudletsTableBuilder
=====================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class CloudletsTableBuilder

   Builds a table for printing simulation results from a list of Cloudlets. It defines a set of default columns but new ones can be added dynamically using the \ ``addColumn()``\  methods.

   The basic usage of the class is by calling its constructor, giving a list of Cloudlets to be printed, and then calling the \ :java:ref:`build()`\  method.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CloudletsTableBuilder
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletsTableBuilder(List<? extends Cloudlet> list)
   :outertype: CloudletsTableBuilder

   Creates new helper object to print the list of cloudlets using the a default \ :java:ref:`TextTableBuilder`\ . To use a different \ :java:ref:`TableBuilder`\ , use the alternative constructors.

   :param list: the list of Cloudlets that the data will be included into the table to be printed

CloudletsTableBuilder
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletsTableBuilder(List<? extends Cloudlet> list, TableBuilder table)
   :outertype: CloudletsTableBuilder

   Creates new helper object to print the list of cloudlets using the a given \ :java:ref:`TableBuilder`\ .

   :param list: the list of Cloudlets that the data will be included into the table to be printed
   :param table: the \ :java:ref:`TableBuilder`\  used to build the table with Cloudlet Data

Methods
-------
addColumn
^^^^^^^^^

.. java:method:: public CloudletsTableBuilder addColumn(TableColumn col, Function<Cloudlet, Object> dataFunction)
   :outertype: CloudletsTableBuilder

   Dynamically adds a column to the end of the table to be built.

   :param col: the column to add
   :param dataFunction: a function that receives a Cloudlet and returns the data to be printed for the added column

addColumn
^^^^^^^^^

.. java:method:: public CloudletsTableBuilder addColumn(int index, TableColumn col, Function<Cloudlet, Object> dataFunction)
   :outertype: CloudletsTableBuilder

   Dynamically adds a column to a specific position into the table to be built.

   :param index: the position to insert the column.
   :param col: the column to add
   :param dataFunction: a function that receives a Cloudlet and returns the data to be printed for the added column

addDataToRow
^^^^^^^^^^^^

.. java:method:: protected void addDataToRow(Cloudlet cloudlet, List<Object> row)
   :outertype: CloudletsTableBuilder

   Add data to a row of the table being generated.

   :param cloudlet: The cloudlet to get to data to show in the row of the table
   :param row: The row to be added the data to

build
^^^^^

.. java:method:: public void build()
   :outertype: CloudletsTableBuilder

   Builds the table with the data of the Cloudlet list and shows the results.

getTable
^^^^^^^^

.. java:method:: protected TableBuilder getTable()
   :outertype: CloudletsTableBuilder

setCloudletList
^^^^^^^^^^^^^^^

.. java:method:: protected final CloudletsTableBuilder setCloudletList(List<? extends Cloudlet> cloudletList)
   :outertype: CloudletsTableBuilder

setTitle
^^^^^^^^

.. java:method:: public CloudletsTableBuilder setTitle(String title)
   :outertype: CloudletsTableBuilder

