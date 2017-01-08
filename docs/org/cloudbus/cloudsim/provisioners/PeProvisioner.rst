.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterCharacteristics

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

PeProvisioner
=============

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public abstract class PeProvisioner

   /** PeProvisioner is an abstract class that represents the provisioning policy used by a host to allocate its PEs to virtual machines inside it. It gets a physical PE and manage it in order to provide this PE as virtual PEs for VMs. In that way, a given PE might be shared among different VMs. Each host's PE has to have its own instance of a PeProvisioner. When extending this class, care must be taken to guarantee that the field availableMips will always contain the amount of free mipsCapacity available for future allocations.

   :author: Anton Beloglazov

Constructors
------------
PeProvisioner
^^^^^^^^^^^^^

.. java:constructor:: public PeProvisioner(double mipsCapacity)
   :outertype: PeProvisioner

   Creates a new PeProvisioner.

   :param mipsCapacity: The total mipsCapacity capacity of the PE that the provisioner can allocate to VMs

Methods
-------
allocateMipsForVm
^^^^^^^^^^^^^^^^^

.. java:method:: public abstract boolean allocateMipsForVm(Vm vm, double mips)
   :outertype: PeProvisioner

   Allocates a new virtual PE with a specific capacity for a given VM. The virtual PE to be added will use the total or partial mipsCapacity capacity of the physical PE.

   :param vm: the virtual machine for which the new virtual PE is being allocated
   :param mips: the mipsCapacity to be allocated to the virtual PE of the given VM
   :return: $true if the virtual PE could be allocated; $false otherwise

allocateMipsForVm
^^^^^^^^^^^^^^^^^

.. java:method:: public abstract boolean allocateMipsForVm(Vm vm, List<Double> mips)
   :outertype: PeProvisioner

   Allocates a new set of virtual PEs with a specific capacity for a given VM. The virtual PE to be added will use the total or partial mipsCapacity capacity of the physical PE.

   :param vm: the virtual machine for which the new virtual PE is being allocated
   :param mips: the list of mipsCapacity capacity of each virtual PE to be allocated to the VM
   :return: $true if the set of virtual PEs could be allocated; $false otherwise

deallocateMipsForAllVms
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void deallocateMipsForAllVms()
   :outertype: PeProvisioner

   Releases all virtual PEs allocated to all VMs.

deallocateMipsForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract void deallocateMipsForVm(Vm vm)
   :outertype: PeProvisioner

   Releases all virtual PEs allocated to a given VM.

   :param vm: the vm

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: PeProvisioner

   Gets the list of allocated virtual PEs' MIPS for a given VM.

   :param vm: the virtual machine the get the list of allocated virtual PEs' MIPS
   :return: list of allocated virtual PEs' MIPS

getAllocatedMipsForVmByVirtualPeId
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract double getAllocatedMipsForVmByVirtualPeId(Vm vm, int peId)
   :outertype: PeProvisioner

   Gets the MIPS capacity of a virtual Pe allocated to a given VM.

   :param vm: virtual machine to get a given virtual PE capacity
   :param peId: the virtual pe id
   :return: allocated MIPS for the virtual PE

getAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: public double getAvailableMips()
   :outertype: PeProvisioner

   Gets the available MIPS in the PE.

   :return: available MIPS

getMipsCapacity
^^^^^^^^^^^^^^^

.. java:method:: public double getMipsCapacity()
   :outertype: PeProvisioner

   Gets the total MIPS capacity of the PE that the provisioner can allocate to VMs.

getTotalAllocatedMips
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getTotalAllocatedMips()
   :outertype: PeProvisioner

   Gets the total allocated MIPS.

   :return: the total allocated MIPS

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: PeProvisioner

   Gets total allocated MIPS for a given VM for all PEs.

   :param vm: the virtual machine the get the total allocated MIPS capacity
   :return: total allocated MIPS

getUtilization
^^^^^^^^^^^^^^

.. java:method:: public double getUtilization()
   :outertype: PeProvisioner

   Gets the utilization of the Pe in percents.

   :return: the utilization

setAvailableMips
^^^^^^^^^^^^^^^^

.. java:method:: protected final boolean setAvailableMips(double availableMips)
   :outertype: PeProvisioner

   Sets the available MIPS in the PE.

   :param availableMips: the availableMips to set
   :return: true if availableMips >= 0, false otherwise

setMipsCapacity
^^^^^^^^^^^^^^^

.. java:method:: public final boolean setMipsCapacity(double mipsCapacity)
   :outertype: PeProvisioner

   Sets the total MIPS capacity of the PE that the provisioner can allocate to VMs.

   :param mipsCapacity: the MIPS capacity to set
   :return: true if mipsCapacity > 0, false otherwise

