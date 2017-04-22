.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.stream Collectors

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecutionInfo

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

.. java:constructor:: public Processor()
   :outertype: Processor

   Instantiates a Processor with zero capacity (zero PEs).

Processor
^^^^^^^^^

.. java:constructor:: public Processor(Vm vm, double pesMips, long numberOfPes)
   :outertype: Processor

   Instantiates a Processor.

   :param vm: the \ :java:ref:`Vm`\  the processor will belong to
   :param pesMips: MIPS of each \ :java:ref:`Pe`\
   :param numberOfPes: number of \ :java:ref:`Pe`\ s

Methods
-------
allocateResource
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResource(long amountToAllocate)
   :outertype: Processor

deallocateAllResources
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long deallocateAllResources()
   :outertype: Processor

deallocateResource
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean deallocateResource(long amountToDeallocate)
   :outertype: Processor

fromMipsList
^^^^^^^^^^^^

.. java:method:: public static Processor fromMipsList(Vm vm, List<Double> mipsList, List<CloudletExecutionInfo> cloudletExecList)
   :outertype: Processor

   Instantiates a new Processor from a given MIPS list, ignoring all elements having zero capacity.

   :param vm: the \ :java:ref:`Vm`\  the processor will belong to
   :param mipsList: a list of \ :java:ref:`Processing Elements (cores) <Pe>`\  capacity where all elements have the same capacity. This list represents the capacity of each processor core.
   :param cloudletExecList: list of cloudlets currently executing in this processor.
   :return: the new Processor

fromMipsList
^^^^^^^^^^^^

.. java:method:: public static Processor fromMipsList(Vm vm, List<Double> mipsList)
   :outertype: Processor

   Instantiates a new Processor from a given MIPS list, ignoring all elements having zero capacity.

   :param vm: the \ :java:ref:`Vm`\  the processor will belong to
   :param mipsList: a list of \ :java:ref:`Processing Elements (cores) <Pe>`\  capacity where all elements have the same capacity. This list represents the capacity of each processor core.
   :return: the new Processor

getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getAllocatedResource()
   :outertype: Processor

   Gets the number of used PEs.

getAvailableMipsByPe
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getAvailableMipsByPe()
   :outertype: Processor

   Gets the amount of MIPS available (free) for each Processor PE, considering the currently executing cloudlets in this processor and the number of PEs these cloudlets require. This is the amount of MIPS that each Cloudlet is allowed to used, considering that the processor is shared among all executing cloudlets.

   In the case of space shared schedulers, there is no concurrency for PEs because some cloudlets may wait in a queue until there is available PEs to be used exclusively by them.

   :return: the amount of available MIPS for each Processor PE.

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

getCloudletExecList
^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<CloudletExecutionInfo> getCloudletExecList()
   :outertype: Processor

   Gets a read-only list of cloudlets currently executing in this processor.

getMips
^^^^^^^

.. java:method:: public double getMips()
   :outertype: Processor

   Gets the individual MIPS of each \ :java:ref:`Pe`\ .

getPercentUtilization
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getPercentUtilization()
   :outertype: Processor

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

.. java:method:: @Override public final boolean setCapacity(long numberOfPes)
   :outertype: Processor

   Sets the number of \ :java:ref:`Pe`\ s of the Processor

   :param numberOfPes: the number of PEs to set

setMips
^^^^^^^

.. java:method:: public final void setMips(double newMips)
   :outertype: Processor

   Sets the individual MIPS of each \ :java:ref:`Pe`\ .

   :param newMips: the new MIPS of each PE

