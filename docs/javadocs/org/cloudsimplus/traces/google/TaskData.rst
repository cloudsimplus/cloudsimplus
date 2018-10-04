TaskData
========

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type::  class TaskData extends MachineDataAbstract

   A base class that stores data to identify a task. It has to be extended by classes that read task's events from a trace file.

   :author: Manoel Campos da Silva Filho

Constructors
------------
TaskData
^^^^^^^^

.. java:constructor::  TaskData()
   :outertype: TaskData

   A protected constructor to avoid class instantiation, since only subclasses of this class must be used.

Methods
-------
getJobId
^^^^^^^^

.. java:method::  long getJobId()
   :outertype: TaskData

   Gets the id of the job this task belongs to.

getTaskIndex
^^^^^^^^^^^^

.. java:method::  long getTaskIndex()
   :outertype: TaskData

   Gets the task index within the job.

getUniqueTaskId
^^^^^^^^^^^^^^^

.. java:method::  long getUniqueTaskId()
   :outertype: TaskData

   An unique ID to be used to identify Cloudlets. The ID is composed of the \ :java:ref:`Job ID <getJobId()>`\  plus the \ :java:ref:`Task Index <getTaskIndex()>`\ .

setJobId
^^^^^^^^

.. java:method::  TaskData setJobId(long jobId)
   :outertype: TaskData

setTaskIndex
^^^^^^^^^^^^

.. java:method::  TaskData setTaskIndex(long taskIndex)
   :outertype: TaskData

