.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

CloudletsTableBuilder
=====================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class CloudletsTableBuilder

   A class to build a table for printing simulation results from a list of cloudlets.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CloudletsTableBuilder
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletsTableBuilder(List<? extends Cloudlet> list)
   :outertype: CloudletsTableBuilder

   Creates new helper object to print the list of cloudlets using the a default \ :java:ref:`TextTableBuilder`\ . To use a different \ :java:ref:`TableBuilder`\ , use the \ :java:ref:`setPrinter(TableBuilder)`\  method.

   :param list: the list of Cloudlets that the data will be included into the table to be printed

Methods
-------
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

createTableColumns
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void createTableColumns()
   :outertype: CloudletsTableBuilder

getPrinter
^^^^^^^^^^

.. java:method:: protected TableBuilder getPrinter()
   :outertype: CloudletsTableBuilder

setCloudletList
^^^^^^^^^^^^^^^

.. java:method:: protected CloudletsTableBuilder setCloudletList(List<? extends Cloudlet> cloudletList)
   :outertype: CloudletsTableBuilder

setPrinter
^^^^^^^^^^

.. java:method:: public final CloudletsTableBuilder setPrinter(TableBuilder printer)
   :outertype: CloudletsTableBuilder

setTitle
^^^^^^^^

.. java:method:: public CloudletsTableBuilder setTitle(String title)
   :outertype: CloudletsTableBuilder

