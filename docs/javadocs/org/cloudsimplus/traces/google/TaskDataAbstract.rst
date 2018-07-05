TaskDataAbstract
================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public abstract class TaskDataAbstract extends MachineDataAbstract

   A base class that stores data to identify a task. It has to be extended by classes that read task's events from a trace file.

   :author: Manoel Campos da Silva Filho

Methods
-------
getJobId
^^^^^^^^

.. java:method:: public int getJobId()
   :outertype: TaskDataAbstract

   Gets the id of the job this task belongs to.

getTaskIndex
^^^^^^^^^^^^

.. java:method:: public int getTaskIndex()
   :outertype: TaskDataAbstract

   Gets the task index within the job.

getUniqueTaskId
^^^^^^^^^^^^^^^

.. java:method:: public int getUniqueTaskId()
   :outertype: TaskDataAbstract

   An unique ID to be used to identify Cloudlets. The ID is composed of the \ :java:ref:`Job ID <getJobId()>`\  concatenated with the \ :java:ref:`Task Index <getTaskIndex()>`\ .

setJobId
^^^^^^^^

.. java:method:: protected TaskDataAbstract setJobId(int jobId)
   :outertype: TaskDataAbstract

setTaskIndex
^^^^^^^^^^^^

.. java:method:: protected TaskDataAbstract setTaskIndex(int taskIndex)
   :outertype: TaskDataAbstract

