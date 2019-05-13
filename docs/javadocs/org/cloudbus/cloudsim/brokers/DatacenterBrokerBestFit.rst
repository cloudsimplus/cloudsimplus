.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.vms Vm

DatacenterBrokerBestFit
=======================

.. java:package:: org.cloudbus.cloudsim.brokers
   :noindex:

.. java:type:: public class DatacenterBrokerBestFit extends DatacenterBrokerSimple

   A simple implementation of \ :java:ref:`DatacenterBroker`\  that uses a best fit mapping among submitted cloudlets and Vm's. The Broker then places the submitted Vm's at the first Datacenter found. If there isn't capacity in that one, it will try the other ones.

   :author: Humaira Abdul Salam

Constructors
------------
DatacenterBrokerBestFit
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterBrokerBestFit(CloudSim simulation)
   :outertype: DatacenterBrokerBestFit

   Creates a new DatacenterBroker object.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to

Methods
-------
defaultVmMapper
^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm defaultVmMapper(Cloudlet cloudlet)
   :outertype: DatacenterBrokerBestFit

   Selects the VM with the lowest number of PEs that is able to run a given Cloudlet. In case the algorithm can't find such a VM, it uses the default DatacenterBroker VM mapper as a fallback.

   :param cloudlet: the Cloudlet to find a VM to run it
   :return: the VM selected for the Cloudlet or \ :java:ref:`Vm.NULL`\  if no suitable VM was found

