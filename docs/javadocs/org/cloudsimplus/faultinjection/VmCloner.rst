.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util Collections

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util.function Function

.. java:import:: java.util.function UnaryOperator

VmCloner
========

.. java:package:: org.cloudsimplus.faultinjection
   :noindex:

.. java:type:: public interface VmCloner

   Enables cloning a \ :java:ref:`Vm`\  which was destroyed due to a \ :java:ref:`Host Failure <HostFaultInjection>`\ . It provides all the features to clone a Vm, simulating the creating of another Vm from an snapshot of the failed one. It also enables re-creating Cloudlets which were running inside the failed VM.

   :author: raysaoliveira

Fields
------
NULL
^^^^

.. java:field::  VmCloner NULL
   :outertype: VmCloner

Methods
-------
clone
^^^^^

.. java:method::  Map.Entry<Vm, List<Cloudlet>> clone(Vm sourceVm)
   :outertype: VmCloner

   Clones a given \ :java:ref:`Vm`\  using the Vm Cloner Function and their Cloudlets using the Clodlets Cloner Function, binding the cloned Cloudlets to the cloned Vm.

   :param sourceVm: the Vm to be cloned
   :return: a \ :java:ref:`Map.Entry`\  where the key is the cloned Vm and the value is the List of cloned Cloudltes.

   **See also:** :java:ref:`.setVmClonerFunction(UnaryOperator)`, :java:ref:`.setCloudletsClonerFunction(Function)`

getClonedVmsNumber
^^^^^^^^^^^^^^^^^^

.. java:method::  int getClonedVmsNumber()
   :outertype: VmCloner

   Gets the number of VMs cloned so far.

getMaxClonesNumber
^^^^^^^^^^^^^^^^^^

.. java:method::  int getMaxClonesNumber()
   :outertype: VmCloner

   Gets the maximum number of Vm clones to create. For instance, if this value is equal to 2, it means if all VMs from a given broker are destroyed multiple times, a clone will be created only 2 times. If all VMs are destroyed again for the 3rd time, no clone will be created. The default value is 1.

isMaxClonesNumberReached
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isMaxClonesNumberReached()
   :outertype: VmCloner

   Checks if the maximum number of Vm clones to be created was reached.

   :return: true if the maximum number of clones was reached, false otherwise

setCloudletsClonerFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  VmCloner setCloudletsClonerFunction(Function<Vm, List<Cloudlet>> cloudletsClonerFunction)
   :outertype: VmCloner

   Gets the \ :java:ref:`Function`\  to be used to clone Vm's \ :java:ref:`Cloudlet`\ s. When the given Function is called, creates a clone of cloudlets which were running inside a specific Vm.

   Such a Function is used to recreate those Cloudlets inside a clone of the failed VM. In this case, all the Cloudlets are recreated from scratch into the cloned VM. This way, when they are submitted to a broker, they re-start execution from the beginning.

   :param cloudletsClonerFunction: the \ :java:ref:`Cloudlet`\ s cloner Function to set

setMaxClonesNumber
^^^^^^^^^^^^^^^^^^

.. java:method::  VmCloner setMaxClonesNumber(int maxClonesNumber)
   :outertype: VmCloner

setVmClonerFunction
^^^^^^^^^^^^^^^^^^^

.. java:method::  VmCloner setVmClonerFunction(UnaryOperator<Vm> vmClonerFunction)
   :outertype: VmCloner

   Sets the \ :java:ref:`UnaryOperator`\  to be used to clone \ :java:ref:`Vm`\ s. It is a Function which, when called, creates a clone of a specific Vm.

   :param vmClonerFunction: the \ :java:ref:`Vm`\  cloner Function to set

