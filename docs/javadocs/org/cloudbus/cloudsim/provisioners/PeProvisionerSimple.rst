.. java:import:: org.cloudbus.cloudsim.resources Pe

PeProvisionerSimple
===================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: public class PeProvisionerSimple extends ResourceProvisionerSimple implements PeProvisioner

   A \ :java:ref:`PeProvisioner`\  that uses a best-effort policy to allocate virtual PEs to VMs from a physical PE: if there is available MIPS on the physical PE, it allocates to a virtual PE; otherwise, it fails. Each host's PE has to have its own instance of a PeProvisioner.

   Each host's PE must have its own instance of a PeProvisioner. When extending this class, care must be taken to guarantee that the field availableMips will always contain the amount of free MIPS available for future allocations.

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
PeProvisionerSimple
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PeProvisionerSimple()
   :outertype: PeProvisionerSimple

   Instantiates a new PeProvisionerSimple that the \ :java:ref:`Pe`\  it will manage will be set just at Pe instantiation.

PeProvisionerSimple
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PeProvisionerSimple(Pe pe)
   :outertype: PeProvisionerSimple

   Instantiates a new PeProvisionerSimple for a given \ :java:ref:`Pe`\ .

   :param pe:

Methods
-------
getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization()
   :outertype: PeProvisionerSimple

setPe
^^^^^

.. java:method:: @Override public void setPe(Pe pe)
   :outertype: PeProvisionerSimple

