.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModelDynamic

.. java:import:: java.util.function Function

TaskEvent
=========

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public final class TaskEvent extends TaskData

   A data class to store the attributes to create a \ :java:ref:`Cloudlet`\ , according to the data read from a line inside a "task events" trace file. Instance of this class are created by the \ :java:ref:`GoogleTaskEventsTraceReader`\  and provided to the user's simulation.

   In order to create such Cloudlets, the \ :java:ref:`GoogleTaskEventsTraceReader`\  requires the developer to provide a \ :java:ref:`Function`\  that creates Cloudlets according to the developer needs.

   The \ :java:ref:`GoogleTaskEventsTraceReader`\  cannot create the Cloudlets itself by hardcoding some simulation specific parameters such as the \ :java:ref:`UtilizationModel`\  or cloudlet length. This way, it request a \ :java:ref:`Function`\  implemented by the developer using the \ :java:ref:`GoogleTaskEventsTraceReader`\  class that has the custom logic to create Cloudlets. However, this developer's \ :java:ref:`Function`\  needs to receive the task parameters read from the trace file such as CPU, RAM and disk requirements and priority. To avoid passing so many parameters to the developer's Function, an instance of this class that wraps all these parameters is used instead.

   :author: Manoel Campos da Silva Filho

Methods
-------
actualCpuCores
^^^^^^^^^^^^^^

.. java:method:: public long actualCpuCores(long maxCpuCores)
   :outertype: TaskEvent

   Computes the actual number of CPU cores (PEs) to be assigned to a Cloudlet, according to the \ :java:ref:`percentage of CPUs to be used <getResourceRequestForCpuCores()>`\  and a given maximum number of existing CPUs.

   :param maxCpuCores: the maximum number of existing CPUs the Cloudlet can use (that can be defined as the number of VM's CPUs)
   :return: the actual number of CPU cores the Cloudlet will require

getPriority
^^^^^^^^^^^

.. java:method:: public int getPriority()
   :outertype: TaskEvent

getResourceRequestForCpuCores
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getResourceRequestForCpuCores()
   :outertype: TaskEvent

   Gets the maximum number of CPU cores the task is permitted to use (in percentage from 0 to 1). This percentage value can be used to compute the number of \ :java:ref:`Pe`\ s the Cloudlet will require, based on the number of PEs of the Vm where the Cloudlet will be executed.

   The actual value to be assigned to a Cloudlet created from this trace field must be defined by the researcher, inside the \ :java:ref:`cloudlet creation function <GoogleTaskEventsTraceReader.getCloudletCreationFunction()>`\  given to the trace reader.

   Since there are "task usage" trace files, they can used used to define the CPU utilization along the time. The value of this attribute is not the same as the max resource usage of the CPU \ :java:ref:`UtilizationModel`\ . It just represents the maximum number of CPUs the Cloudet will use. The percentage that such CPUs will be used for a given time is defined by the CPU \ :java:ref:`UtilizationModel.getUtilization()`\ . Such a value is defined by a "task usage" trace.

   **See also:** :java:ref:`GoogleTaskEventsTraceReader.FieldIndex.RESOURCE_REQUEST_FOR_CPU_CORES`, :java:ref:`GoogleTaskUsageTraceReader`

getResourceRequestForLocalDiskSpace
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getResourceRequestForLocalDiskSpace()
   :outertype: TaskEvent

   Gets the maximum amount of local disk space the task is permitted to use (in percentage from 0 to 1).

   The actual value to be assigned to a Cloudlet created from this trace field must be defined by the researcher, inside the \ :java:ref:`cloudlet creation function <GoogleTaskEventsTraceReader.getCloudletCreationFunction()>`\  given to the trace reader.

   This field can be used to define the initial Cloudlet file size and/or output size when creating the Cloudlet, according to the researcher needs.

   **See also:** :java:ref:`GoogleTaskEventsTraceReader.FieldIndex.RESOURCE_REQUEST_FOR_LOCAL_DISK_SPACE`

getResourceRequestForRam
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getResourceRequestForRam()
   :outertype: TaskEvent

   Gets the maximum amount of RAM the task is permitted to use (in percentage from 0 to 1).

   The actual value to be assigned to a Cloudlet created from this trace field must be defined by the researcher, inside the \ :java:ref:`cloudlet creation function <GoogleTaskEventsTraceReader.getCloudletCreationFunction()>`\  given to the trace reader.

   This field can be used to define the max resource utilization percentage for a UtilizationModel when creating the Cloudlet. Since there are "task usage" trace files, they can used used to define the RAM utilization along the time. In this case, a \ :java:ref:`UtilizationModelDynamic`\  is required for the Cloudlet's RAM UtilizationModel. Using a different class will raise an runtime exception when trying to create the Cloudlets.

   **See also:** :java:ref:`GoogleTaskEventsTraceReader.FieldIndex.RESOURCE_REQUEST_FOR_RAM`, :java:ref:`GoogleTaskUsageTraceReader`

getSchedulingClass
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getSchedulingClass()
   :outertype: TaskEvent

   Gets the s​cheduling class ​that roughly represents how latency-sensitive the task is. The scheduling class is represented by a single number, with 3 representing a more latency-sensitive task (e.g., serving revenue-generating user requests) and 0 representing a non-production task (e.g., development, non-business-critical analyses, etc.).

   **See also:** :java:ref:`GoogleTaskEventsTraceReader.FieldIndex.SCHEDULING_CLASS`

getTimestamp
^^^^^^^^^^^^

.. java:method:: public double getTimestamp()
   :outertype: TaskEvent

   Gets the time the event happened (converted to seconds).

   **See also:** :java:ref:`GoogleTaskEventsTraceReader.FieldIndex.TIMESTAMP`

getUserName
^^^^^^^^^^^

.. java:method:: public String getUserName()
   :outertype: TaskEvent

   Gets the hashed username provided as an opaque base64-encoded string that can be tested for equality.

   **See also:** :java:ref:`GoogleTaskEventsTraceReader.FieldIndex.USERNAME`

setPriority
^^^^^^^^^^^

.. java:method:: protected TaskEvent setPriority(int priority)
   :outertype: TaskEvent

setResourceRequestForCpuCores
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskEvent setResourceRequestForCpuCores(double resourceRequestForCpuCores)
   :outertype: TaskEvent

setResourceRequestForLocalDiskSpace
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskEvent setResourceRequestForLocalDiskSpace(double resourceRequestForLocalDiskSpace)
   :outertype: TaskEvent

setResourceRequestForRam
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  TaskEvent setResourceRequestForRam(double resourceRequestForRam)
   :outertype: TaskEvent

setSchedulingClass
^^^^^^^^^^^^^^^^^^

.. java:method::  TaskEvent setSchedulingClass(int schedulingClass)
   :outertype: TaskEvent

setTimestamp
^^^^^^^^^^^^

.. java:method:: protected TaskEvent setTimestamp(double timestamp)
   :outertype: TaskEvent

setUserName
^^^^^^^^^^^

.. java:method::  TaskEvent setUserName(String userName)
   :outertype: TaskEvent

