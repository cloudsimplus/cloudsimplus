.. java:import:: org.cloudbus.cloudsim.core CloudInformationService

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.io UncheckedIOException

.. java:import:: java.nio.file Files

.. java:import:: java.nio.file Paths

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Set

.. java:import:: java.util.function BiFunction

.. java:import:: java.util.function Function

GoogleMachineEventsTraceReader.FieldIndex
=========================================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public enum FieldIndex implements TraceField<GoogleMachineEventsTraceReader>
   :outertype: GoogleMachineEventsTraceReader

   The index of each field in the trace file.

Enum Constants
--------------
CPU_CAPACITY
^^^^^^^^^^^^

.. java:field:: public static final GoogleMachineEventsTraceReader.FieldIndex CPU_CAPACITY
   :outertype: GoogleMachineEventsTraceReader.FieldIndex

   4: The index of the CPU capacity field in the trace, that represents a percentage (between 0 and 1) of the \ :java:ref:`getMaxCpuCores()`\ .

EVENT_TYPE
^^^^^^^^^^

.. java:field:: public static final GoogleMachineEventsTraceReader.FieldIndex EVENT_TYPE
   :outertype: GoogleMachineEventsTraceReader.FieldIndex

   2: The index of the field containing the type of event. The possible values for this field are the ordinal values of the enum \ :java:ref:`MachineEventType`\ .

MACHINE_ID
^^^^^^^^^^

.. java:field:: public static final GoogleMachineEventsTraceReader.FieldIndex MACHINE_ID
   :outertype: GoogleMachineEventsTraceReader.FieldIndex

   1: The index of the field containing the machine ID.

PLATFORM_ID
^^^^^^^^^^^

.. java:field:: public static final GoogleMachineEventsTraceReader.FieldIndex PLATFORM_ID
   :outertype: GoogleMachineEventsTraceReader.FieldIndex

   3: The platform ID is an opaque string representing the microarchitecture and chipset version of the machine.

RAM_CAPACITY
^^^^^^^^^^^^

.. java:field:: public static final GoogleMachineEventsTraceReader.FieldIndex RAM_CAPACITY
   :outertype: GoogleMachineEventsTraceReader.FieldIndex

   5: The index of the RAM capacity field in the trace, that represents a percentage (between 0 and 1) of the \ :java:ref:`getMaxRamCapacity()`\  ()}.

TIMESTAMP
^^^^^^^^^

.. java:field:: public static final GoogleMachineEventsTraceReader.FieldIndex TIMESTAMP
   :outertype: GoogleMachineEventsTraceReader.FieldIndex

   0: The index of the field containing the time the event happened (in microsecond).

