.. java:import:: org.cloudbus.cloudsim.vms Vm

Processor
=========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public final class Processor extends ResourceManageableAbstract

   A Central Unit Processing (CPU) attached to a \ :java:ref:`Vm`\  and which can have multiple cores (\ :java:ref:`Pe`\ s). It's a also called a Virtual CPU (vCPU).

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field:: public static final Processor NULL
   :outertype: Processor

Constructors
------------
Processor
^^^^^^^^^

.. java:constructor:: public Processor(Vm vm, double pesMips, long numberOfPes)
   :outertype: Processor

   Instantiates a Processor for a given VM.

   :param vm: the \ :java:ref:`Vm`\  the processor will belong to
   :param pesMips: MIPS of each \ :java:ref:`Pe`\
   :param numberOfPes: number of \ :java:ref:`Pe`\ s

Methods
-------
getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: Processor

   Gets the number of used PEs.

getAvailableResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAvailableResource()
   :outertype: Processor

   Gets the number of free PEs.

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: Processor

   Gets the number of \ :java:ref:`Pe`\ s of the Processor

getMips
^^^^^^^

.. java:method:: public double getMips()
   :outertype: Processor

   Gets the individual MIPS of each \ :java:ref:`Pe`\ .

getTotalMips
^^^^^^^^^^^^

.. java:method:: public double getTotalMips()
   :outertype: Processor

   Gets the sum of MIPS from all \ :java:ref:`Pe`\ s.

getVm
^^^^^

.. java:method:: public Vm getVm()
   :outertype: Processor

   Gets the \ :java:ref:`Vm`\  the processor belongs to.

setCapacity
^^^^^^^^^^^

.. java:method:: @Override public boolean setCapacity(long numberOfPes)
   :outertype: Processor

   Sets the number of \ :java:ref:`Pe`\ s of the Processor

   :param numberOfPes: the number of PEs to set

setMips
^^^^^^^

.. java:method:: public void setMips(double newMips)
   :outertype: Processor

   Sets the individual MIPS of each \ :java:ref:`Pe`\ .

   :param newMips: the new MIPS of each PE

