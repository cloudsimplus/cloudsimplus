.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: java.util.function Function

MachineEvent
============

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public final class MachineEvent extends MachineDataAbstract

   A data class to store the attributes to create a \ :java:ref:`Host`\ , according to the data read from a line inside a "machine events" trace file. Instance of this class are created by the \ :java:ref:`GoogleMachineEventsTraceReader`\  and provided to the user's simulation.

   In order to create such Hosts, the \ :java:ref:`GoogleMachineEventsTraceReader`\  requires the developer to provide a \ :java:ref:`Function`\  that creates Hosts according to the developer needs.

   The \ :java:ref:`GoogleMachineEventsTraceReader`\  cannot create the Hosts itself by hardcoding some simulation specific parameters such as the \ :java:ref:`VmScheduler`\  or \ :java:ref:`ResourceProvisioner`\ . This way, it request a \ :java:ref:`Function`\  implemented by the developer using the \ :java:ref:`GoogleMachineEventsTraceReader`\  class that has the custom logic to create Hosts. However, this developer's \ :java:ref:`Function`\  needs to receive the host parameters read from the trace file. To avoid passing so many parameters to the developer's Function, an instance of this class that wraps all these parameters is used instead.

   :author: Manoel Campos da Silva Filho

Methods
-------
getCpuCores
^^^^^^^^^^^

.. java:method:: public int getCpuCores()
   :outertype: MachineEvent

   Gets the actual number of \ :java:ref:`Pe`\ s (CPU cores) to be assigned to a Host, according the \ :java:ref:`GoogleMachineEventsTraceReader.getMaxCpuCores()`\ .

   **See also:** :java:ref:`GoogleMachineEventsTraceReader.FieldIndex.CPU_CAPACITY`

getRam
^^^^^^

.. java:method:: public long getRam()
   :outertype: MachineEvent

   Gets the actual RAM capacity to be assigned to a Host, according the \ :java:ref:`GoogleMachineEventsTraceReader.getMaxRamCapacity()`\ .

   **See also:** :java:ref:`GoogleMachineEventsTraceReader.FieldIndex.RAM_CAPACITY`

getTimestamp
^^^^^^^^^^^^

.. java:method:: public double getTimestamp()
   :outertype: MachineEvent

   Gets the time the event happened (converted to seconds).

   **See also:** :java:ref:`GoogleMachineEventsTraceReader.FieldIndex.TIMESTAMP`

setCpuCores
^^^^^^^^^^^

.. java:method::  MachineEvent setCpuCores(int cpuCores)
   :outertype: MachineEvent

setRam
^^^^^^

.. java:method:: protected MachineEvent setRam(long ram)
   :outertype: MachineEvent

setTimestamp
^^^^^^^^^^^^

.. java:method::  MachineEvent setTimestamp(double timestamp)
   :outertype: MachineEvent

