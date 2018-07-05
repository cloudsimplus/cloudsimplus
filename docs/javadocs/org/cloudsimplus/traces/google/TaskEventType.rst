.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: java.util Optional

TaskEventType
=============

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public enum TaskEventType

   Defines the type of an event (a line) in the trace file that represents the state of the job. Each enum instance is a possible value for the \ :java:ref:`FieldIndex.EVENT_TYPE`\  field.

   :author: Manoel Campos da Silva Filho

Enum Constants
--------------
EVICT
^^^^^

.. java:field:: public static final TaskEventType EVICT
   :outertype: TaskEventType

   2: A task or job was descheduled because of a higher priority task or job, because the scheduler overcommitted and the actual demand exceeded the machine capacity, because the machine on which it was running became unusable (e.g. taken offline for repairs), or because a disk holding the task’s data was lost.

FAIL
^^^^

.. java:field:: public static final TaskEventType FAIL
   :outertype: TaskEventType

   3: A task or job was descheduled (or, in rare cases, ceased to be eligible for scheduling while it was pending) due to a task failure.

FINISH
^^^^^^

.. java:field:: public static final TaskEventType FINISH
   :outertype: TaskEventType

   4: A task or job completed normally.

KILL
^^^^

.. java:field:: public static final TaskEventType KILL
   :outertype: TaskEventType

   5: A task or job was cancelled by the user or a driver program or because another job or task on which this job was dependent died.

LOST
^^^^

.. java:field:: public static final TaskEventType LOST
   :outertype: TaskEventType

   6: A task or job was presumably terminated, but a record indicating its termination was missing from our source data.

SCHEDULE
^^^^^^^^

.. java:field:: public static final TaskEventType SCHEDULE
   :outertype: TaskEventType

   1: A job or task was scheduled on a machine (it may not start running immediately due to code-shipping time, etc). For jobs, this occurs the first time any task of the job is scheduled on a machine.

SUBMIT
^^^^^^

.. java:field:: public static final TaskEventType SUBMIT
   :outertype: TaskEventType

   0: A task or job became eligible for scheduling.

UPDATE_PENDING
^^^^^^^^^^^^^^

.. java:field:: public static final TaskEventType UPDATE_PENDING
   :outertype: TaskEventType

   7: A task or job’s scheduling class, resource requirements, or constraints were updated while it was waiting to be scheduled.

UPDATE_RUNNING
^^^^^^^^^^^^^^

.. java:field:: public static final TaskEventType UPDATE_RUNNING
   :outertype: TaskEventType

   8: A task or job’s scheduling class, resource requirements, or constraints were updated while it was scheduled.

