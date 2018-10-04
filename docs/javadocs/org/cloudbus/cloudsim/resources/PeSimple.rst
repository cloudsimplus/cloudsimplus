.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

.. java:import:: java.util Objects

PeSimple
========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class PeSimple extends ResourceManageableAbstract implements Pe

   Pe (Processing Element) class represents a CPU core of a physical machine (PM), defined in terms of Millions Instructions Per Second (MIPS) rating. Such a class allows managing the Pe capacity and allocation.

   \ **ASSUMPTION:**\  All PEs under the same Machine have the same MIPS rating.

   :author: Manzur Murshed, Rajkumar Buyya

Constructors
------------
PeSimple
^^^^^^^^

.. java:constructor:: public PeSimple(double mipsCapacity, PeProvisioner peProvisioner)
   :outertype: PeSimple

   Instantiates a new PE object. The id of the PE is just set when a List of PEs is assigned to a Host.

   :param mipsCapacity: the capacity of the PE in MIPS (Million Instructions per Second)
   :param peProvisioner: the provisioner that will manage the allocation of this physical Pe for VMs

PeSimple
^^^^^^^^

.. java:constructor:: public PeSimple(int id, double mipsCapacity, PeProvisioner peProvisioner)
   :outertype: PeSimple

   Instantiates a new PE object defining a given id. The id of the PE is just set when a List of PEs is assigned to a Host.

   :param id: the PE id
   :param mipsCapacity: the capacity of the PE in MIPS (Million Instructions per Second)
   :param peProvisioner: the provisioner that will manage the allocation of this physical Pe for VMs

Methods
-------
getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: PeSimple

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

isBuzy
^^^^^^

.. java:method:: @Override public boolean isBuzy()
   :outertype: PeSimple

isFailed
^^^^^^^^

.. java:method:: @Override public boolean isFailed()
   :outertype: PeSimple

isFree
^^^^^^

.. java:method:: @Override public boolean isFree()
   :outertype: PeSimple

isWorking
^^^^^^^^^

.. java:method:: @Override public boolean isWorking()
   :outertype: PeSimple

setCapacity
^^^^^^^^^^^

.. java:method:: @Override public boolean setCapacity(double mipsCapacity)
   :outertype: PeSimple

setId
^^^^^

.. java:method:: @Override public final void setId(long id)
   :outertype: PeSimple

setPeProvisioner
^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Pe setPeProvisioner(PeProvisioner peProvisioner)
   :outertype: PeSimple

setStatus
^^^^^^^^^

.. java:method:: @Override public final boolean setStatus(Status status)
   :outertype: PeSimple

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: PeSimple

