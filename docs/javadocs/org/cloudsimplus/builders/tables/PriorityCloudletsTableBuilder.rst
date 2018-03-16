.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: java.util List

PriorityCloudletsTableBuilder
=============================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class PriorityCloudletsTableBuilder extends CloudletsTableBuilder

   A helper class to print cloudlets results as a table, including the Cloudlet priority value.

   :author: Manoel Campos da Silva Filho

Constructors
------------
PriorityCloudletsTableBuilder
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PriorityCloudletsTableBuilder(List<? extends Cloudlet> list)
   :outertype: PriorityCloudletsTableBuilder

Methods
-------
addDataToRow
^^^^^^^^^^^^

.. java:method:: @Override protected void addDataToRow(Cloudlet cloudlet, List<Object> row)
   :outertype: PriorityCloudletsTableBuilder

createTableColumns
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void createTableColumns()
   :outertype: PriorityCloudletsTableBuilder

