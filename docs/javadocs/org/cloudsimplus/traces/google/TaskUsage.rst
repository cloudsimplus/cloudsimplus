.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

TaskUsage
=========

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public final class TaskUsage extends TaskData

   A data class to store the attributes representing the resource usage of a \ :java:ref:`Cloudlet`\ , according to the data read from a line inside a "task usage" trace file. Instance of this class are created by the \ :java:ref:`GoogleTaskUsageTraceReader`\  and provided to the user's simulation.

   :author: Manoel Campos da Silva Filho

Methods
-------
getAssignedMemoryUsage
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getAssignedMemoryUsage()
   :outertype: TaskUsage

   Gets the assigned memory usage, i.e., memory usage based on the memory actually assigned (but not necessarily used) to the container where the task was running inside the Google Cluster.

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.ASSIGNED_MEMORY_USAGE`

getCanonicalMemoryUsage
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getCanonicalMemoryUsage()
   :outertype: TaskUsage

   Gets the canonical memory usage, i.e., the number of user accessible pages, including page cache but excluding some pages marked as stale.

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.CANONICAL_MEMORY_USAGE`

getEndTime
^^^^^^^^^^

.. java:method:: public double getEndTime()
   :outertype: TaskUsage

   Gets the end time​ of the measurement period (converted to seconds).

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.END_TIME`

getMaximumCpuUsage
^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaximumCpuUsage()
   :outertype: TaskUsage

   Gets the maximum CPU usage observed over the measurement interval.

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.MAXIMUM_CPU_USAGE`

getMaximumDiskIoTime
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaximumDiskIoTime()
   :outertype: TaskUsage

   Gets the maximum disk IO time observed over the measurement interval.

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.MAXIMUM_DISK_IO_TIME`

getMaximumMemoryUsage
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaximumMemoryUsage()
   :outertype: TaskUsage

   Gets the maximum memory usage, i.e., the maximum value of the canonical memory usage measurement observed over the measurement interval. This value is not available for some tasks.

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.MAXIMUM_MEMORY_USAGE`

getMeanCpuUsageRate
^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMeanCpuUsageRate()
   :outertype: TaskUsage

   Gets the mean CPU usage rate (in percentage from 0 to 1).

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.MEAN_CPU_USAGE_RATE`

getMeanDiskIoTime
^^^^^^^^^^^^^^^^^

.. java:method:: public double getMeanDiskIoTime()
   :outertype: TaskUsage

   Gets the mean disk I/O time.

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.MEAN_DISK_IO_TIME`

getMeanLocalDiskSpaceUsed
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMeanLocalDiskSpaceUsed()
   :outertype: TaskUsage

   Gets the mean local disk space used. Represents runtime local disk capacity usage. Disk usage required for binaries and other read-only, pre-staged runtime files is ​not​included. Additionally, most disk space used by distributed, persistent storage (e.g. GFS, Colossus) is not accounted for in this trace.

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.MEAN_LOCAL_DISK_SPACE_USED`

getStartTime
^^^^^^^^^^^^

.. java:method:: public double getStartTime()
   :outertype: TaskUsage

   Gets the start time​ of the measurement period (converted to seconds).

   **See also:** :java:ref:`GoogleTaskUsageTraceReader.FieldIndex.START_TIME`

setAssignedMemoryUsage
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskUsage setAssignedMemoryUsage(double assignedMemoryUsage)
   :outertype: TaskUsage

setCanonicalMemoryUsage
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskUsage setCanonicalMemoryUsage(double canonicalMemoryUsage)
   :outertype: TaskUsage

setEndTime
^^^^^^^^^^

.. java:method::  TaskUsage setEndTime(double endTime)
   :outertype: TaskUsage

setMaximumCpuUsage
^^^^^^^^^^^^^^^^^^

.. java:method::  TaskUsage setMaximumCpuUsage(double maximumCpuUsage)
   :outertype: TaskUsage

setMaximumDiskIoTime
^^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskUsage setMaximumDiskIoTime(double maximumDiskIoTime)
   :outertype: TaskUsage

setMaximumMemoryUsage
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskUsage setMaximumMemoryUsage(double maximumMemoryUsage)
   :outertype: TaskUsage

setMeanCpuUsageRate
^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskUsage setMeanCpuUsageRate(double meanCpuUsageRate)
   :outertype: TaskUsage

setMeanDiskIoTime
^^^^^^^^^^^^^^^^^

.. java:method::  TaskUsage setMeanDiskIoTime(double meanDiskIoTime)
   :outertype: TaskUsage

setMeanLocalDiskSpaceUsed
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskUsage setMeanLocalDiskSpaceUsed(double meanLocalDiskSpaceUsed)
   :outertype: TaskUsage

setStartTime
^^^^^^^^^^^^

.. java:method:: protected TaskUsage setStartTime(double startTime)
   :outertype: TaskUsage

