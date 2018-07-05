.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudsimplus.traces.google GoogleMachineEventsTraceReader.FieldIndex

MachineEventType
================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public enum MachineEventType

   Defines the type of an event (a line) in the trace file that represents the operation to be performed with the \ :java:ref:`Host`\ . Each enum instance is a possible value for the \ :java:ref:`FieldIndex.EVENT_TYPE`\  field.

   This enum defines a some methods to move the processing logic of each event type to the enum value associated to it. Since the enum includes the \ :java:ref:`process(GoogleMachineEventsTraceReader)`\  abstract method, if a new enum value is added, we just need to implement the method for that value. Using such approach we avoid spreading if chains to check which event type a trace line is to call the corresponding process method.

   :author: Manoel Campos da Silva Filho

Enum Constants
--------------
ADD
^^^

.. java:field:: public static final MachineEventType ADD
   :outertype: MachineEventType

   0: A \ :java:ref:`Host`\  became available to the cluster - all machines in the trace will have an ADD event.

REMOVE
^^^^^^

.. java:field:: public static final MachineEventType REMOVE
   :outertype: MachineEventType

   1: A \ :java:ref:`Host`\  was removed from the cluster. Removals can occur due to failures or maintenance.

UPDATE
^^^^^^

.. java:field:: public static final MachineEventType UPDATE
   :outertype: MachineEventType

   2: A \ :java:ref:`Host`\  available to the cluster had its available resources changed.

