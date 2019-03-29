TaskData
========

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type::  class TaskData extends MachineDataAbstract

   A base class that stores data to identify a task. It has to be extended by classes that read task's events from a trace file.

   :author: Manoel Campos da Silva Filho

Methods
-------
getJobId
^^^^^^^^

.. java:method:: public long getJobId()
   :outertype: TaskData

   Gets the id of the job this task belongs to.

getTaskIndex
^^^^^^^^^^^^

.. java:method:: public long getTaskIndex()
   :outertype: TaskData

   Gets the task index within the job.

getUniqueTaskId
^^^^^^^^^^^^^^^

.. java:method:: public long getUniqueTaskId()
   :outertype: TaskData

   An unique ID to be used to identify Cloudlets. The ID is composed of the \ :java:ref:`Job ID <getJobId()>`\ , concatenated with the \ :java:ref:`Task Index <getTaskIndex()>`\ .

setJobId
^^^^^^^^

.. java:method::  TaskData setJobId(long jobId)
   :outertype: TaskData

setTaskIndex
^^^^^^^^^^^^

.. java:method::  TaskData setTaskIndex(long taskIndex)
   :outertype: TaskData

