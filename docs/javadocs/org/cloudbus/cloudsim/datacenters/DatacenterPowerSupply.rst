.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.power.models PowerAware

DatacenterPowerSupply
=====================

.. java:package:: org.cloudbus.cloudsim.datacenters
   :noindex:

.. java:type:: public class DatacenterPowerSupply implements PowerAware

   Computes current amount of power being consumed by the \ :java:ref:`Host`\ s of a \ :java:ref:`Datacenter`\ .

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field:: public static final DatacenterPowerSupply NULL
   :outertype: DatacenterPowerSupply

Constructors
------------
DatacenterPowerSupply
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterPowerSupply()
   :outertype: DatacenterPowerSupply

DatacenterPowerSupply
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected DatacenterPowerSupply(Datacenter datacenter)
   :outertype: DatacenterPowerSupply

Methods
-------
computePowerUtilizationForTimeSpan
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double computePowerUtilizationForTimeSpan(double lastDatacenterProcessTime)
   :outertype: DatacenterPowerSupply

   Computes an \ **estimation**\  of total power consumed (in Watts-sec) by all Hosts of the Datacenter since the last time the processing of Cloudlets in this Host was updated. It also updates the \ :java:ref:`Datacenter's total consumed power up to now <getPower()>`\ .

   :return: the \ **estimated**\  total power consumed (in Watts-sec) by all Hosts in the elapsed time span

getPower
^^^^^^^^

.. java:method:: @Override public double getPower()
   :outertype: DatacenterPowerSupply

   Gets the total power consumed by the Datacenter up to now in Watt-Second (Ws).

   :return: the total power consumption in Watt-Second (Ws)

   **See also:** :java:ref:`.getPowerInKWatts()`

setDatacenter
^^^^^^^^^^^^^

.. java:method:: protected DatacenterPowerSupply setDatacenter(Datacenter datacenter)
   :outertype: DatacenterPowerSupply

