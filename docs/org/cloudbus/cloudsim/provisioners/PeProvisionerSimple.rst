.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: org.cloudbus.cloudsim.vms Vm

PeProvisionerSimple
===================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public class PeProvisionerSimple extends PeProvisioner

   PeProvisionerSimple is an extension of \ :java:ref:`PeProvisioner`\  which uses a best-effort policy to allocate virtual PEs to VMs: if there is available MIPS on the physical PE, it allocates to a virtual PE; otherwise, it fails. Each host's PE has to have its own instance of a PeProvisioner.

   :author: Anton Beloglazov

Constructors
------------
PeProvisionerSimple
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PeProvisionerSimple(double availableMips)
   :outertype: PeProvisionerSimple

   Instantiates a new pe provisioner simple.

   :param availableMips: The total mips capacity of the PE that the provisioner can allocate to VMs.

Methods
-------
allocateMipsForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateMipsForVm(Vm vm, double mips)
   :outertype: PeProvisionerSimple

allocateMipsForVm
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateMipsForVm(Vm vm, List<Double> mipsShare)
   :outertype: PeProvisionerSimple

deallocateMipsForAllVms
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateMipsForAllVms()
   :outertype: PeProvisionerSimple

deallocateMipsForVm
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void deallocateMipsForVm(Vm vm)
   :outertype: PeProvisionerSimple

getAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Double> getAllocatedMipsForVm(Vm vm)
   :outertype: PeProvisionerSimple

getAllocatedMipsForVmByVirtualPeId
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAllocatedMipsForVmByVirtualPeId(Vm vm, int peId)
   :outertype: PeProvisionerSimple

getPeTable
^^^^^^^^^^

.. java:method:: protected Map<Vm, List<Double>> getPeTable()
   :outertype: PeProvisionerSimple

   Gets the pe map.

   :return: the pe map

getTotalAllocatedMipsForVm
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getTotalAllocatedMipsForVm(Vm vm)
   :outertype: PeProvisionerSimple

setPeTable
^^^^^^^^^^

.. java:method:: protected final void setPeTable(Map<Vm, List<Double>> peTable)
   :outertype: PeProvisionerSimple

   Sets the pe map.

   :param peTable: the peTable to set

