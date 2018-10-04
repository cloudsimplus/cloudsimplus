.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBrokerSimple

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core.events CloudSimEvent

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelDynamic

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.io UncheckedIOException

.. java:import:: java.nio.file Files

.. java:import:: java.nio.file Paths

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

GoogleTaskEventsTraceReader.FieldIndex
======================================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public enum FieldIndex implements TraceField<GoogleTaskEventsTraceReader>
   :outertype: GoogleTaskEventsTraceReader

   The index of each field in the trace file.

Enum Constants
--------------
DIFFERENT_MACHINE_CONSTRAINT
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex DIFFERENT_MACHINE_CONSTRAINT
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   12: If the different-machine constraint​ field is present, and true (1), it indicates that a task must be scheduled to execute on a different machine than any other currently running task in the job. It is a special type of constraint.

   When there is no value for the field, -1 is returned instead.

EVENT_TYPE
^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex EVENT_TYPE
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   5: The index of the field containing the type of event. The possible values for this field are the ordinal values of the enum \ :java:ref:`TaskEventType`\ .

JOB_ID
^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex JOB_ID
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   2: The index of the field containing the id of the job this task belongs to.

MACHINE_ID
^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex MACHINE_ID
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   4: The index of the field containing the machineID. If the field is present, indicates the machine onto which the task was scheduled, otherwise, the reader will return -1 as default value.

MISSING_INFO
^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex MISSING_INFO
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   1: When it seems Google Cluster is missing an event record, it's synthesized a replacement. Similarly, we look for a record of every job or task that is active at the end of the trace time window, and synthesize a missing record if we don't find one. Synthesized records have a number (called the "missing info" field) to represent why they were added to the trace, according to \ :java:ref:`MissingInfo`\  values.

   When there is no info missing, the field is empty in the trace. In this case, -1 is returned instead.

PRIORITY
^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex PRIORITY
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   8: Each task has a p​riority, a​ small integer that is mapped here into a sorted set of values, with 0 as the lowest priority (least important). Tasks with larger priority numbers generally get preference for resources over tasks with smaller priority numbers.

   There are some special priority ranges:

   ..

   * \ **"free" priorities**\ : these are the lowest priorities. Resources requested at these priorities incur little internal charging.
   * \ **"production" priorities**\ : these are the highest priorities. The cluster scheduler attempts to prevent latency-sensitive tasks at these priorities from being evicted due to over-allocation of machine resources.
   * \ **"monitoring" priorities**\ : these priorities are intended for jobs which monitor the health of other, lower-priority jobs

RESOURCE_REQUEST_FOR_CPU_CORES
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex RESOURCE_REQUEST_FOR_CPU_CORES
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   9: The index of the field containing the maximum number of CPU cores the task is permitted to use (in percentage from 0 to 1).

   When there is no value for the field, 0 is returned instead.

RESOURCE_REQUEST_FOR_LOCAL_DISK_SPACE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex RESOURCE_REQUEST_FOR_LOCAL_DISK_SPACE
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   11: The index of the field containing the maximum amount of local disk space the task is permitted to use (in percentage from 0 to 1).

   When there is no value for the field, 0 is returned instead.

RESOURCE_REQUEST_FOR_RAM
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex RESOURCE_REQUEST_FOR_RAM
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   10: The index of the field containing the maximum amount of RAM the task is permitted to use (in percentage from 0 to 1).

   When there is no value for the field, 0 is returned instead.

SCHEDULING_CLASS
^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex SCHEDULING_CLASS
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   7: All jobs and tasks have a s​cheduling class ​that roughly represents how latency-sensitive it is. The scheduling class is represented by a single number, with 3 representing a more latency-sensitive task (e.g., serving revenue-generating user requests) and 0 representing a non-production task (e.g., development, non-business-critical analyses, etc.). Note that scheduling class is n​ot a priority, although more latency-sensitive tasks tend to have higher task priorities. Scheduling class affects machine-local policy for resource access. Priority determines whether a task is scheduled on a machine.

   \ **WARNING**\ : Currently, this field is totally ignored by CloudSim Plus.

TASK_INDEX
^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex TASK_INDEX
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   3: The index of the field containing the task index within the job.

TIMESTAMP
^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex TIMESTAMP
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   0: The index of the field containing the time the event happened (stored in microsecond but converted to seconds when read from the file).

USERNAME
^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.FieldIndex USERNAME
   :outertype: GoogleTaskEventsTraceReader.FieldIndex

   6: The index of the field containing the hashed username provided as an opaque base64-encoded string that can be tested for equality. For each distinct username, a corresponding \ :java:ref:`DatacenterBroker`\  is created.

