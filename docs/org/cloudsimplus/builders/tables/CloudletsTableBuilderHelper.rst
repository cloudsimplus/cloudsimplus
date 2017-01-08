.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

CloudletsTableBuilderHelper
===========================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public class CloudletsTableBuilderHelper

   A class to help printing simulation results for a list of cloudlets.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CloudletsTableBuilderHelper
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletsTableBuilderHelper(List<? extends Cloudlet> list)
   :outertype: CloudletsTableBuilderHelper

   Creates new helper object to print the list of cloudlets using the a default \ :java:ref:`TextTableBuilder`\ . To use a different \ :java:ref:`TableBuilder`\ , use the \ :java:ref:`setPrinter(TableBuilder)`\  method.

   :param list: the list of Cloudlets that the data will be included into the table to be printed

Methods
-------
addDataToRow
^^^^^^^^^^^^

.. java:method:: protected void addDataToRow(Cloudlet cloudlet, List<Object> row)
   :outertype: CloudletsTableBuilderHelper

   Add data to a row of the table being generated.

   :param cloudlet: The cloudlet to get to data to show in the row of the table
   :param row: The row to be added the data to

build
^^^^^

.. java:method:: public void build()
   :outertype: CloudletsTableBuilderHelper

   Builds the table with the data of the Cloudlet list and shows the results.

createTableColumns
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void createTableColumns()
   :outertype: CloudletsTableBuilderHelper

getPrinter
^^^^^^^^^^

.. java:method:: protected TableBuilder getPrinter()
   :outertype: CloudletsTableBuilderHelper

setCloudletList
^^^^^^^^^^^^^^^

.. java:method:: protected CloudletsTableBuilderHelper setCloudletList(List<? extends Cloudlet> cloudletList)
   :outertype: CloudletsTableBuilderHelper

setPrinter
^^^^^^^^^^

.. java:method:: public final CloudletsTableBuilderHelper setPrinter(TableBuilder printer)
   :outertype: CloudletsTableBuilderHelper

setTitle
^^^^^^^^

.. java:method:: public CloudletsTableBuilderHelper setTitle(String title)
   :outertype: CloudletsTableBuilderHelper

