.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: java.util List

PriorityCloudletsTableBuilderHelper
===================================

.. java:package:: org.cloudsimplus.builders.tables
   :noindex:

.. java:type:: public class PriorityCloudletsTableBuilderHelper extends CloudletsTableBuilderHelper

   A helper class to print cloudlets results as a table, including the Cloudlet priority value.

   :author: Manoel Campos da Silva Filho

Constructors
------------
PriorityCloudletsTableBuilderHelper
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PriorityCloudletsTableBuilderHelper(List<? extends Cloudlet> list)
   :outertype: PriorityCloudletsTableBuilderHelper

Methods
-------
addDataToRow
^^^^^^^^^^^^

.. java:method:: @Override protected void addDataToRow(Cloudlet cloudlet, List<Object> row)
   :outertype: PriorityCloudletsTableBuilderHelper

createTableColumns
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void createTableColumns()
   :outertype: PriorityCloudletsTableBuilderHelper

