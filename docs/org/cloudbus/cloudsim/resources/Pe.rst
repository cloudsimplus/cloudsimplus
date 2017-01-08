.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisionerSimple

Pe
==

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface Pe

   A interface to be implemented by each class that provides the basic features of a virtual or physical Processing Element (PE) of a PM or VM. Each Pe represents a virtual or physical processor core.

   It also implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`Pe.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`Pe`\  variables.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Pe NULL
   :outertype: Pe

   A property that implements the Null Object Design Pattern for \ :java:ref:`Pe`\  objects.

Methods
-------
getId
^^^^^

.. java:method::  int getId()
   :outertype: Pe

   Gets the PE id.

   :return: the PE id

getMips
^^^^^^^

.. java:method::  int getMips()
   :outertype: Pe

   Gets the MIPS Rating of this Pe.

   :return: the MIPS Rating

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

setMips
^^^^^^^

.. java:method::  boolean setMips(double d)
   :outertype: Pe

   Sets the MIPS Rating of this PE.

   :param d: the mips
   :return: true if MIPS > 0, false otherwise

setStatus
^^^^^^^^^

.. java:method::  boolean setStatus(Status status)
   :outertype: Pe

   Sets the \ :java:ref:`status <getStatus()>`\  of the PE.

   :param status: the new PE status
   :return: true if the status was set, false otherwise

