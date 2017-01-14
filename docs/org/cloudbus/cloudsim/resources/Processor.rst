.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.stream Collectors

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletExecutionInfo

Processor
=========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class Processor implements ResourceCapacity

   A Central Unit Processing (CPU) that can have multiple cores (\ :java:ref:`Processing Elements <Pe>`\ ).

   :author: Manoel Campos da Silva Filho

Constructors
------------
Processor
^^^^^^^^^

.. java:constructor:: public Processor()
   :outertype: Processor

   Instantiates a Processor with zero capacity (zero PEs and MIPS).

Processor
^^^^^^^^^

.. java:constructor:: public Processor(long individualPeCapacity, int numberOfPes)
   :outertype: Processor

   Instantiates a Processor.

   :param individualPeCapacity: capacity of each \ :java:ref:`Processing Elements (cores) <Pe>`\
   :param numberOfPes: number of \ :java:ref:`Processing Elements (cores) <Pe>`\

Methods
-------
fromMipsList
^^^^^^^^^^^^

.. java:method:: public static Processor fromMipsList(List<Double> mipsList, List<CloudletExecutionInfo> cloudletExecList)
   :outertype: Processor

   Instantiates a new Processor from a given MIPS list, ignoring all elements having zero capacity.

   :param mipsList: a list of \ :java:ref:`Processing Elements (cores) <Pe>`\  capacity where all elements have the same capacity. This list represents the capacity of each processor core.
   :param cloudletExecList: list of cloudlets currently executing in this processor.
   :return: the new processor

fromMipsList
^^^^^^^^^^^^

.. java:method:: public static Processor fromMipsList(List<Double> mipsList)
   :outertype: Processor

   Instantiates a new Processor from a given MIPS list, ignoring all elements having zero capacity.

   :param mipsList: a list of \ :java:ref:`Processing Elements (cores) <Pe>`\  capacity where all elements have the same capacity. This list represents the capacity of each processor core.
   :return: the new processor

getAvailableMipsByPe
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getAvailableMipsByPe()
   :outertype: Processor

   Gets the amount of MIPS available (free) for each Processor PE, considering the currently executing cloudlets in this processor and the number of PEs these cloudlets require. This is the amount of MIPS that each Cloudlet is allowed to used, considering that the processor is shared among all executing cloudlets.

   In the case of space shared schedulers, there is no concurrency for PEs because some cloudlets may wait in a queue until there is available PEs to be used exclusively by them.

   :return: the amount of available MIPS for each Processor PE.

getCapacity
^^^^^^^^^^^

.. java:method:: @Override public long getCapacity()
   :outertype: Processor

   Gets the individual MIPS capacity of each \ :java:ref:`Processing Elements (cores) <Pe>`\ .

getCloudletExecList
^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<CloudletExecutionInfo> getCloudletExecList()
   :outertype: Processor

   Gets a read-only list of cloudlets currently executing in this processor.

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: public int getNumberOfPes()
   :outertype: Processor

   Gets the number of \ :java:ref:`Processing Elements (cores) <Pe>`\  of the Processor

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getTotalMipsCapacity()
   :outertype: Processor

   Gets the total MIPS capacity of the Processor, that is the sum of all its \ :java:ref:`Processing Elements (cores) <Pe>`\  capacity.

setCapacity
^^^^^^^^^^^

.. java:method:: public final void setCapacity(long newCapacity)
   :outertype: Processor

   Sets the individual MIPS capacity of each \ :java:ref:`Processing Elements (cores) <Pe>`\ .

   :param newCapacity: the new MIPS capacity of each PE

setNumberOfPes
^^^^^^^^^^^^^^

.. java:method:: public final void setNumberOfPes(int numberOfPes)
   :outertype: Processor

   Sets the number of \ :java:ref:`Processing Elements (cores) <Pe>`\  of the Processor

   :param numberOfPes: the number of PEs to set

