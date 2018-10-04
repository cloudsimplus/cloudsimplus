.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.network.topologies NetworkTopology

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Stream

CloudSim
========

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public class CloudSim implements Simulation

   The main class of the simulation API, that manages Cloud Computing simulations providing all methods to start, pause and stop them. It sends and processes all discrete events during the simulation time.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
VERSION
^^^^^^^

.. java:field:: public static final String VERSION
   :outertype: CloudSim

   CloudSim Plus current version.

Constructors
------------
CloudSim
^^^^^^^^

.. java:constructor:: public CloudSim()
   :outertype: CloudSim

   Creates a CloudSim simulation. Internally it creates a CloudInformationService.

   **See also:** :java:ref:`CloudInformationService`, :java:ref:`.CloudSim(double)`

CloudSim
^^^^^^^^

.. java:constructor:: public CloudSim(double minTimeBetweenEvents)
   :outertype: CloudSim

   Creates a CloudSim simulation that tracks events happening in a time interval as little as the minTimeBetweenEvents parameter. Internally it creates a \ :java:ref:`CloudInformationService`\ .

   :param minTimeBetweenEvents: the minimal period between events. Events within shorter periods after the last event are discarded.

   **See also:** :java:ref:`CloudInformationService`

Methods
-------
abort
^^^^^

.. java:method:: @Override public void abort()
   :outertype: CloudSim

addEntity
^^^^^^^^^

.. java:method:: @Override public void addEntity(CloudSimEntity entity)
   :outertype: CloudSim

addOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Simulation addOnClockTickListener(EventListener<EventInfo> listener)
   :outertype: CloudSim

addOnEventProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Simulation addOnEventProcessingListener(EventListener<SimEvent> listener)
   :outertype: CloudSim

addOnSimulationPauseListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Simulation addOnSimulationPauseListener(EventListener<EventInfo> listener)
   :outertype: CloudSim

addOnSimulationStartListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Simulation addOnSimulationStartListener(EventListener<EventInfo> listener)
   :outertype: CloudSim

cancel
^^^^^^

.. java:method:: @Override public SimEvent cancel(SimEntity src, Predicate<SimEvent> p)
   :outertype: CloudSim

cancelAll
^^^^^^^^^

.. java:method:: @Override public boolean cancelAll(SimEntity src, Predicate<SimEvent> p)
   :outertype: CloudSim

clock
^^^^^

.. java:method:: @Override public double clock()
   :outertype: CloudSim

clockInHours
^^^^^^^^^^^^

.. java:method:: @Override public double clockInHours()
   :outertype: CloudSim

clockInMinutes
^^^^^^^^^^^^^^

.. java:method:: @Override public double clockInMinutes()
   :outertype: CloudSim

findFirstDeferred
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public SimEvent findFirstDeferred(SimEntity dest, Predicate<SimEvent> p)
   :outertype: CloudSim

getCalendar
^^^^^^^^^^^

.. java:method:: @Override public Calendar getCalendar()
   :outertype: CloudSim

getCloudInfoService
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public CloudInformationService getCloudInfoService()
   :outertype: CloudSim

getDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Datacenter> getDatacenterList()
   :outertype: CloudSim

getEntityList
^^^^^^^^^^^^^

.. java:method:: @Override public List<SimEntity> getEntityList()
   :outertype: CloudSim

getMinTimeBetweenEvents
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMinTimeBetweenEvents()
   :outertype: CloudSim

getNetworkTopology
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public NetworkTopology getNetworkTopology()
   :outertype: CloudSim

getNumEntities
^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumEntities()
   :outertype: CloudSim

getNumberOfFutureEvents
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfFutureEvents(Predicate<SimEvent> predicate)
   :outertype: CloudSim

holdEntity
^^^^^^^^^^

.. java:method:: @Override public void holdEntity(SimEntity src, long delay)
   :outertype: CloudSim

isPaused
^^^^^^^^

.. java:method:: @Override public boolean isPaused()
   :outertype: CloudSim

isRunning
^^^^^^^^^

.. java:method:: @Override public boolean isRunning()
   :outertype: CloudSim

isTerminationTimeSet
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isTerminationTimeSet()
   :outertype: CloudSim

isTimeToTerminateSimulationUnderRequest
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isTimeToTerminateSimulationUnderRequest()
   :outertype: CloudSim

pause
^^^^^

.. java:method:: @Override public boolean pause()
   :outertype: CloudSim

pause
^^^^^

.. java:method:: @Override public boolean pause(double time)
   :outertype: CloudSim

pauseEntity
^^^^^^^^^^^

.. java:method:: @Override public void pauseEntity(SimEntity src, double delay)
   :outertype: CloudSim

removeOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnClockTickListener(EventListener<? extends EventInfo> listener)
   :outertype: CloudSim

removeOnEventProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnEventProcessingListener(EventListener<SimEvent> listener)
   :outertype: CloudSim

removeOnSimulationPauseListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnSimulationPauseListener(EventListener<EventInfo> listener)
   :outertype: CloudSim

resume
^^^^^^

.. java:method:: @Override public boolean resume()
   :outertype: CloudSim

select
^^^^^^

.. java:method:: @Override public SimEvent select(SimEntity dest, Predicate<SimEvent> p)
   :outertype: CloudSim

send
^^^^

.. java:method:: @Override public void send(SimEntity src, SimEntity dest, double delay, int tag, Object data)
   :outertype: CloudSim

send
^^^^

.. java:method:: @Override public void send(SimEvent evt)
   :outertype: CloudSim

sendFirst
^^^^^^^^^

.. java:method:: @Override public void sendFirst(SimEntity src, SimEntity dest, double delay, int tag, Object data)
   :outertype: CloudSim

sendFirst
^^^^^^^^^

.. java:method:: @Override public void sendFirst(SimEvent evt)
   :outertype: CloudSim

sendNow
^^^^^^^

.. java:method:: @Override public void sendNow(SimEntity src, SimEntity dest, int tag, Object data)
   :outertype: CloudSim

setNetworkTopology
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setNetworkTopology(NetworkTopology networkTopology)
   :outertype: CloudSim

start
^^^^^

.. java:method:: @Override public double start()
   :outertype: CloudSim

terminate
^^^^^^^^^

.. java:method:: @Override public boolean terminate()
   :outertype: CloudSim

terminateAt
^^^^^^^^^^^

.. java:method:: @Override public boolean terminateAt(double time)
   :outertype: CloudSim

wait
^^^^

.. java:method:: @Override public void wait(CloudSimEntity src, Predicate<SimEvent> p)
   :outertype: CloudSim

waiting
^^^^^^^

.. java:method:: @Override public long waiting(SimEntity dest, Predicate<SimEvent> p)
   :outertype: CloudSim

