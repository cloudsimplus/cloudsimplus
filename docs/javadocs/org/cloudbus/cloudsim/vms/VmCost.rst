.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterCharacteristics

.. java:import:: org.cloudbus.cloudsim.resources Pe

VmCost
======

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: public class VmCost

   Computes the monetary cost to run a given VM, including the \ :java:ref:`total cost <getTotalCost()>`\  and individual resource cost, namely: the processing power, bandwidth, memory and storage cost.

   :author: raysaoliveira

Constructors
------------
VmCost
^^^^^^

.. java:constructor:: public VmCost(Vm vm)
   :outertype: VmCost

   Creates a VmCost object to compute the monetary cost to run a given VM.

   :param vm: the VM to compute its monetary cost

Methods
-------
getBwCost
^^^^^^^^^

.. java:method:: public double getBwCost()
   :outertype: VmCost

   Gets the total monetary cost of the VM's allocated BW.

getMemoryCost
^^^^^^^^^^^^^

.. java:method:: public double getMemoryCost()
   :outertype: VmCost

   Gets the total monetary cost of the VM's allocated memory.

getProcessingCost
^^^^^^^^^^^^^^^^^

.. java:method:: public double getProcessingCost()
   :outertype: VmCost

   Gets the total monetary cost of processing power allocated from the PM hosting the VM.

getStorageCost
^^^^^^^^^^^^^^

.. java:method:: public double getStorageCost()
   :outertype: VmCost

   Gets the total monetary cost of the VM's allocated storage.

   :return: getStorageCost

getTotalCost
^^^^^^^^^^^^

.. java:method:: public double getTotalCost()
   :outertype: VmCost

   Gets the total monetary cost of all resources allocated to the VM, namely the processing power, bandwidth, memory and storage.

getVm
^^^^^

.. java:method:: public Vm getVm()
   :outertype: VmCost

   Gets the VM for which the total monetary cost will be computed.

