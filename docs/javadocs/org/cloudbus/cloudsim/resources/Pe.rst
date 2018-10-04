.. java:import:: org.cloudbus.cloudsim.core ChangeableId

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

Pe
==

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface Pe extends ChangeableId, ResourceManageable

   A interface to be implemented by each class that provides the basic features of a virtual or physical Processing Element (PE) of a PM or VM. Each Pe represents a virtual or physical processor core.

   It also implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`Pe.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`Pe`\  variables.

   :author: Manzur Murshed, Rajkumar Buyya, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Pe NULL
   :outertype: Pe

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Pe`\  objects.

Methods
-------
getCapacity
^^^^^^^^^^^

.. java:method:: @Override  long getCapacity()
   :outertype: Pe

   Gets the capacity of this Pe in MIPS (Million Instructions Per Second).

   :return: the MIPS capacity

getPeProvisioner
^^^^^^^^^^^^^^^^

.. java:method::  PeProvisioner getPeProvisioner()
   :outertype: Pe

   Gets the PE provisioner that manages the allocation of this physical PE to virtual machines.

   :return: the PE provisioner

getStatus
^^^^^^^^^

.. java:method::  Status getStatus()
   :outertype: Pe

   Gets the status of the PE.

   :return: the PE status

isBuzy
^^^^^^

.. java:method::  boolean isBuzy()
   :outertype: Pe

   Checks if the PE is buzy to be used (it's being used).

isFailed
^^^^^^^^

.. java:method::  boolean isFailed()
   :outertype: Pe

   Checks if the PE is failed.

isFree
^^^^^^

.. java:method::  boolean isFree()
   :outertype: Pe

   Checks if the PE is free to be used (it's idle).

isWorking
^^^^^^^^^

.. java:method::  boolean isWorking()
   :outertype: Pe

   Checks if the PE is working (not failed).

setCapacity
^^^^^^^^^^^

.. java:method:: @Override  boolean setCapacity(long mipsCapacity)
   :outertype: Pe

   Sets the capacity of this Pe in MIPS (Million Instructions Per Second).

   :param mipsCapacity: the MIPS capacity to set
   :return: true if mipsCapacity > 0, false otherwise

setCapacity
^^^^^^^^^^^

.. java:method::  boolean setCapacity(double mipsCapacity)
   :outertype: Pe

   Sets the capacity of this Pe in MIPS (Million Instructions Per Second).

   It receives the amount of MIPS as a double value but converts it internally to a long. The method is just provided as a handy-way to define the PE capacity using a double value that usually is generated from some computations.

   :param mipsCapacity: the MIPS capacity to set
   :return: true if mipsCapacity > 0, false otherwise

setPeProvisioner
^^^^^^^^^^^^^^^^

.. java:method::  Pe setPeProvisioner(PeProvisioner peProvisioner)
   :outertype: Pe

   Sets the \ :java:ref:`getPeProvisioner()`\  that manages the allocation of this physical PE to virtual machines. This method is automatically called when a \ :java:ref:`PeProvisioner`\  is created passing a Pe instance. Thus, the PeProvisioner for a Pe doesn't have to be set manually.

   :param peProvisioner: the new PE provisioner

setStatus
^^^^^^^^^

.. java:method::  boolean setStatus(Status status)
   :outertype: Pe

   Sets the \ :java:ref:`status <getStatus()>`\  of the PE.

   :param status: the new PE status
   :return: true if the status was set, false otherwise

