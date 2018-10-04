.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core.events CloudSimEvent

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelDynamic

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelFull

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.io UncheckedIOException

.. java:import:: java.nio.file Files

.. java:import:: java.nio.file Paths

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Set

GoogleTaskUsageTraceReader.FieldIndex
=====================================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public enum FieldIndex implements TraceField<GoogleTaskUsageTraceReader>
   :outertype: GoogleTaskUsageTraceReader

   The index of each field in the trace file.

Enum Constants
--------------
ASSIGNED_MEMORY_USAGE
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex ASSIGNED_MEMORY_USAGE
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   7: The index of the field containing the assigned memory usage, i.e., memory usage based on the memory actually assigned (but not necessarily used) to the container where the task was running inside the Google Cluster.

CANONICAL_MEMORY_USAGE
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex CANONICAL_MEMORY_USAGE
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   6: The index of the field containing the canonical memory usage, i.e., the number of user accessible pages, including page cache but excluding some pages marked as stale.

END_TIME
^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex END_TIME
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   1: The index of the field containing the end time​ of the measurement period (stored in microsecond but converted to seconds when read from the file).

JOB_ID
^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex JOB_ID
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   2: The index of the field containing the id of the job this task belongs to.

MACHINE_ID
^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex MACHINE_ID
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   4: The index of the field containing the machineID. If the field is present, indicates the machine onto which the task was scheduled, otherwise, the reader will return -1 as default value.

MAXIMUM_CPU_USAGE
^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex MAXIMUM_CPU_USAGE
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   13: The index of the field containing the maximum CPU usage observed over the measurement interval.

MAXIMUM_DISK_IO_TIME
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex MAXIMUM_DISK_IO_TIME
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   14: The index of the field containing the maximum disk IO time observed over the measurement interval.

MAXIMUM_MEMORY_USAGE
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex MAXIMUM_MEMORY_USAGE
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   10: The index of the field containing the maximum memory usage, i.e., the maximum value of the canonical memory usage measurement observed over the measurement interval. This value is not available for some tasks.

MEAN_CPU_USAGE_RATE
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex MEAN_CPU_USAGE_RATE
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   5: The index of the field containing the mean CPU usage rate (in percentage from 0 to 1).

MEAN_DISK_IO_TIME
^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex MEAN_DISK_IO_TIME
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   11: The index of the field containing the mean disk I/O time.

MEAN_LOCAL_DISK_SPACE_USED
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex MEAN_LOCAL_DISK_SPACE_USED
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   12: The index of the field containing the mean local disk space used. Represents runtime local disk capacity usage. Disk usage required for binaries and other read-only, pre-staged runtime files is ​not​included. Additionally, most disk space used by distributed, persistent storage (e.g. GFS, Colossus) is not accounted for in this trace.

START_TIME
^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex START_TIME
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   0: The index of the field containing the start time​ of the measurement period (stored in microsecond but converted to seconds when read from the file).

TASK_INDEX
^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex TASK_INDEX
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   3: The index of the field containing the task index within the job.

TOTAL_PAGE_CACHE_MEMORY_USAGE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex TOTAL_PAGE_CACHE_MEMORY_USAGE
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   9: The index of the field containing the total page cache memory usage, i.e., the total Linux page cache (file-backed memory).

UNMAPPED_PAGE_CACHE_MEMORY_USAGE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskUsageTraceReader.FieldIndex UNMAPPED_PAGE_CACHE_MEMORY_USAGE
   :outertype: GoogleTaskUsageTraceReader.FieldIndex

   8: The index of the field containing the unmapped page cache memory usage, i.e., Linux page cache (file-backed memory) not mapped into any userspace process.

