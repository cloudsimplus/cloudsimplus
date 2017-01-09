.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

.. java:import:: java.util Objects

PeSimple
========

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public class PeSimple implements Pe

   Pe (Processing Element) class represents a CPU core of a physical machine (PM), defined in terms of Millions Instructions Per Second (MIPS) rating.

   \ **ASSUMPTION:**\  All PEs under the same Machine have the same MIPS rating.

   :author: Manzur Murshed, Rajkumar Buyya

Constructors
------------
PeSimple
^^^^^^^^

.. java:constructor:: public PeSimple(int id, PeProvisioner peProvisioner)
   :outertype: PeSimple

   Instantiates a new PE object.

   :param id: the PE ID
   :param peProvisioner: the PE provisioner

Methods
-------
getId
^^^^^

.. java:method:: @Override public int getId()
   :outertype: PeSimple

   Gets the PE id.

   :return: the PE id

getMips
^^^^^^^

.. java:method:: @Override public int getMips()
   :outertype: PeSimple

   Gets the MIPS Rating of this Pe.

   :return: the MIPS Rating

getPeProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public PeProvisioner getPeProvisioner()
   :outertype: PeSimple

   Gets the PE provisioner that manages the allocation of this physical PE to virtual machines.

   :return: the PE provisioner

getStatus
^^^^^^^^^

.. java:method:: @Override public Status getStatus()
   :outertype: PeSimple

   Gets the status of the PE.

   :return: the PE status

setId
^^^^^

.. java:method:: protected final void setId(int id)
   :outertype: PeSimple

   Sets the \ :java:ref:`getId()`\ .

   :param id: the new PE id

setMips
^^^^^^^

.. java:method:: @Override public boolean setMips(double d)
   :outertype: PeSimple

   Sets the MIPS Rating of this PE.

   :param d: the mips
   :return: true if MIPS > 0, false otherwise

setPeProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: protected final void setPeProvisioner(PeProvisioner peProvisioner)
   :outertype: PeSimple

   Sets the \ :java:ref:`getPeProvisioner()`\  that manages the allocation of this physical PE to virtual machines.

   :param peProvisioner: the new PE provisioner

setStatus
^^^^^^^^^

.. java:method:: @Override public final boolean setStatus(Status status)
   :outertype: PeSimple

