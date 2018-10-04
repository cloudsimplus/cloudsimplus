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

GoogleTaskEventsTraceReader.MissingInfo
=======================================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public enum MissingInfo
   :outertype: GoogleTaskEventsTraceReader

   Defines the type of information missing in the trace file. It represents the possible values for the MISSING_INFO field.

Enum Constants
--------------
EXISTS_BUT_NO_CREATION
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.MissingInfo EXISTS_BUT_NO_CREATION
   :outertype: GoogleTaskEventsTraceReader.MissingInfo

   2: Means Google Clusters did not find a record representing the creation of the given task or job. In this case, we may be missing metadata (job name, resource requests, etc.) about the job or task and we may have placed SCHEDULE or SUBMIT events latter than they actually are.

NO_SNAPSHOT_OR_TRANSITION
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.MissingInfo NO_SNAPSHOT_OR_TRANSITION
   :outertype: GoogleTaskEventsTraceReader.MissingInfo

   1: Means Google Clusters did not find a record representing the given termination event, but the job or task disappeared from later snapshots of cluster states, so it must have been terminated. The timestamp of the synthesized event is a pessimistic upper bound on its actual termination time assuming it could have legitimately been missing from one snapshot.

SNAPSHOT_BUT_NO_TRANSITION
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final GoogleTaskEventsTraceReader.MissingInfo SNAPSHOT_BUT_NO_TRANSITION
   :outertype: GoogleTaskEventsTraceReader.MissingInfo

   0: Means Google Clusters did not find a record representing the given event, but a later snapshot of the job or task state indicated that the transition must have occurred. The timestamp of the synthesized event is the timestamp of the snapshot.

