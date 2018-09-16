.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets.network NetworkCloudlet

.. java:import:: org.cloudbus.cloudsim.network VmPacket

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.vms VmSimple

.. java:import:: java.util ArrayList

.. java:import:: java.util List

NetworkVm
=========

.. java:package:: org.cloudbus.cloudsim.vms.network
   :noindex:

.. java:type:: public class NetworkVm extends VmSimple

   NetworkVm class extends \ :java:ref:`VmSimple`\  to support simulation of networked datacenters. It executes actions related to management of packets (sent and received).

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg

Constructors
------------
NetworkVm
^^^^^^^^^

.. java:constructor:: public NetworkVm(int id, long mipsCapacity, int numberOfPes)
   :outertype: NetworkVm

   Creates a NetworkVm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size. To change these values, use the respective setters. While the Vm \ :java:ref:`is not created inside a Host <isCreated()>`\ , such values can be changed freely.

   :param id: unique ID of the VM
   :param mipsCapacity: the mips capacity of each Vm \ :java:ref:`Pe`\
   :param numberOfPes: amount of \ :java:ref:`Pe`\  (CPU cores)

NetworkVm
^^^^^^^^^

.. java:constructor:: public NetworkVm(long mipsCapacity, int numberOfPes)
   :outertype: NetworkVm

   Creates a NetworkVm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size. To change these values, use the respective setters. While the Vm \ :java:ref:`is not created inside a Host <isCreated()>`\ , such values can be changed freely.

   It is not defined an id for the Vm. The id is defined when the Vm is submitted to a \ :java:ref:`DatacenterBroker`\ .

   :param mipsCapacity: the mips capacity of each Vm \ :java:ref:`Pe`\
   :param numberOfPes: amount of \ :java:ref:`Pe`\  (CPU cores)

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Vm o)
   :outertype: NetworkVm

getCloudletList
^^^^^^^^^^^^^^^

.. java:method:: public List<NetworkCloudlet> getCloudletList()
   :outertype: NetworkVm

   List of \ :java:ref:`NetworkCloudlet`\  of the VM.

getFinishTime
^^^^^^^^^^^^^

.. java:method:: public double getFinishTime()
   :outertype: NetworkVm

   The time when the VM finished to process its cloudlets.

getReceivedPacketList
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<VmPacket> getReceivedPacketList()
   :outertype: NetworkVm

   List of packets received by the VM.

isFree
^^^^^^

.. java:method:: public boolean isFree()
   :outertype: NetworkVm

   Indicates if the VM is free or not.

setCloudletList
^^^^^^^^^^^^^^^

.. java:method:: public void setCloudletList(List<NetworkCloudlet> cloudletList)
   :outertype: NetworkVm

setFinishTime
^^^^^^^^^^^^^

.. java:method:: public void setFinishTime(double finishTime)
   :outertype: NetworkVm

setFree
^^^^^^^

.. java:method:: public void setFree(boolean free)
   :outertype: NetworkVm

setReceivedPacketList
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setReceivedPacketList(List<VmPacket> receivedPacketList)
   :outertype: NetworkVm

