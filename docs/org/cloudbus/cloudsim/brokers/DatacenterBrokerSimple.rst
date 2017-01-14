.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.core CloudSim

DatacenterBrokerSimple
======================

.. java:package:: org.cloudbus.cloudsim.brokers
   :noindex:

.. java:type:: public class DatacenterBrokerSimple extends DatacenterBrokerAbstract

   A simple implementation of \ :java:ref:`DatacenterBroker`\  that try to host customer's VMs at the first Datacenter found. If there isn't capacity in that one, it will try the other ones.

   The selection of VMs for each cloudlet is based on a Round-Robin policy, cyclically selecting the next VM from the broker VM list for each requesting cloudlet.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
DatacenterBrokerSimple
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DatacenterBrokerSimple(CloudSim simulation)
   :outertype: DatacenterBrokerSimple

   Creates a new DatacenterBroker object.

   :param simulation: name to be associated with this entity

Methods
-------
getNextVmIndex
^^^^^^^^^^^^^^

.. java:method:: protected int getNextVmIndex()
   :outertype: DatacenterBrokerSimple

   Gets the index of next VM in the broker's created VM list. If not VM was selected yet, selects the first one, otherwise, cyclically selects the next VM.

   :return: the index of the next VM to bind a cloudlet to

selectDatacenterForWaitingVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter selectDatacenterForWaitingVms()
   :outertype: DatacenterBrokerSimple

   {@inheritDoc} It always selects the first Datacenter from the Datacenter list.

   :return: {@inheritDoc}

selectFallbackDatacenterForWaitingVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Datacenter selectFallbackDatacenterForWaitingVms()
   :outertype: DatacenterBrokerSimple

   {@inheritDoc}

   It gets the first Datacenter that has not been tried yet.

   :return: {@inheritDoc}

selectVmForWaitingCloudlet
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Vm selectVmForWaitingCloudlet(Cloudlet cloudlet)
   :outertype: DatacenterBrokerSimple

   {@inheritDoc} It applies a Round-Robin policy to cyclically select the next Vm from the list of waiting VMs.

   :param cloudlet: {@inheritDoc}
   :return: {@inheritDoc}

