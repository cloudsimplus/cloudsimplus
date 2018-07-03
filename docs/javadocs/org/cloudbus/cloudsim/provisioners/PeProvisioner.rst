.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

PeProvisioner
=============

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public interface PeProvisioner extends ResourceProvisioner

   An interface that represents the provisioning policy used by a host to provide virtual PEs to its virtual machines. It gets a physical PE and manage it in order to provide this PE as virtual PEs for VMs. In that way, a given PE might be shared among different VMs.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  PeProvisioner NULL
   :outertype: PeProvisioner

   An attribute that implements the Null Object Design Pattern for PeProvisioner objects.

Methods
-------
allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override  boolean allocateResourceForVm(Vm vm, long mipsCapacity)
   :outertype: PeProvisioner

   Allocates an amount of MIPS from the physical Pe to a new virtual PE for a given VM. The virtual PE to be added will use the total or partial MIPS capacity of the physical PE.

   :param vm: the virtual machine for which the new virtual PE is being allocated
   :param mipsCapacity: the MIPS to be allocated to the virtual PE of the given VM
   :return: $true if the virtual PE could be allocated; $false otherwise

deallocateResourceForAllVms
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override  void deallocateResourceForAllVms()
   :outertype: PeProvisioner

   Releases all virtual PEs allocated to all VMs.

deallocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override  boolean deallocateResourceForVm(Vm vm)
   :outertype: PeProvisioner

   Releases the virtual Pe allocated to a given VM.

   :param vm: the vm to release the virtual Pe

getAllocatedResourceForVm
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override  long getAllocatedResourceForVm(Vm vm)
   :outertype: PeProvisioner

   Gets the amount of allocated MIPS from the physical Pe to a virtual PE of a VM.

   :param vm: the virtual machine to get the allocated virtual Pe MIPS
   :return: the allocated virtual Pe MIPS

getTotalAllocatedResource
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override  long getTotalAllocatedResource()
   :outertype: PeProvisioner

   Gets the total allocated MIPS from the physical Pe.

   :return: the total allocated MIPS

getUtilization
^^^^^^^^^^^^^^

.. java:method::  double getUtilization()
   :outertype: PeProvisioner

   Gets the utilization percentage of the Pe in scale from 0 to 1.

   :return: the utilization percentage from 0 to 1

setPe
^^^^^

.. java:method::  void setPe(Pe pe)
   :outertype: PeProvisioner

   Sets the \ :java:ref:`Pe`\  that this provisioner will manage.

   :param pe: the Pe to set

