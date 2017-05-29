.. java:import:: java.util.stream Stream

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.network.topologies NetworkTopology

.. java:import:: org.cloudbus.cloudsim.util Log

.. java:import:: java.util.function Predicate

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

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

   Creates a CloudSim simulation using a default calendar. Internally it creates a CloudInformationService.

   **See also:** :java:ref:`CloudInformationService`

CloudSim
^^^^^^^^

.. java:constructor:: public CloudSim(Calendar cal)
   :outertype: CloudSim

   Creates a CloudSim simulation with the given parameters. Internally it creates a \ :java:ref:`CloudInformationService`\ .

   :param cal: starting time for this simulation. If it is \ ``null``\ , then the time will be taken from \ ``Calendar.getInstance()``\
   :throws RuntimeException:

   **See also:** :java:ref:`CloudInformationService`

CloudSim
^^^^^^^^

.. java:constructor:: @Deprecated public CloudSim(int numUser, Calendar cal, boolean traceFlag, double periodBetweenEvents)
   :outertype: CloudSim

   Creates a CloudSim simulation with the given parameters. Internally it creates a \ :java:ref:`CloudInformationService`\ .

   :param numUser: this parameter is not being used anymore
   :param cal: starting time for this simulation. If it is \ ``null``\ , then the time will be taken from \ ``Calendar.getInstance()``\
   :param traceFlag: this parameter is not being used anymore
   :param periodBetweenEvents: the minimal period between events. Events within shorter periods after the last event are discarded.

   **See also:** :java:ref:`CloudInformationService`

Methods
-------
abort
^^^^^

.. java:method:: @Override public void abort()
   :outertype: CloudSim

addEntity
^^^^^^^^^

.. java:method:: @Override public void addEntity(CloudSimEntity e)
   :outertype: CloudSim

addEntityDynamically
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void addEntityDynamically(SimEntity e)
   :outertype: CloudSim

   Internal method used to add a new entity to the simulation when the simulation is running. \ **It should not be called from user simulations.**\

   :param e: The new entity

addOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Simulation addOnClockTickListener(EventListener<EventInfo> listener)
   :outertype: CloudSim

addOnEventProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Simulation addOnEventProcessingListener(EventListener<SimEvent> listener)
   :outertype: CloudSim

addOnSimulationPausedListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public final Simulation addOnSimulationPausedListener(EventListener<EventInfo> listener)
   :outertype: CloudSim

cancel
^^^^^^

.. java:method:: @Override public SimEvent cancel(int src, Predicate<SimEvent> p)
   :outertype: CloudSim

cancelAll
^^^^^^^^^

.. java:method:: @Override public boolean cancelAll(int src, Predicate<SimEvent> p)
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

doPause
^^^^^^^

.. java:method:: public boolean doPause()
   :outertype: CloudSim

   Effectively pauses the simulation after an pause request.

   :return: true if the simulation was paused (the simulation is running and was not paused yet), false otherwise

   **See also:** :java:ref:`.pause()`, :java:ref:`.pause(double)`

findFirstDeferred
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public SimEvent findFirstDeferred(int dest, Predicate<SimEvent> p)
   :outertype: CloudSim

getCalendar
^^^^^^^^^^^

.. java:method:: @Override public Calendar getCalendar()
   :outertype: CloudSim

getCloudInfoServiceEntityId
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getCloudInfoServiceEntityId()
   :outertype: CloudSim

getDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Datacenter> getDatacenterList()
   :outertype: CloudSim

getEntitiesByName
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Map<String, SimEntity> getEntitiesByName()
   :outertype: CloudSim

getEntity
^^^^^^^^^

.. java:method:: @Override public SimEntity getEntity(int id)
   :outertype: CloudSim

getEntity
^^^^^^^^^

.. java:method:: @Override public SimEntity getEntity(String name)
   :outertype: CloudSim

getEntityId
^^^^^^^^^^^

.. java:method:: @Override public int getEntityId(String name)
   :outertype: CloudSim

getEntityList
^^^^^^^^^^^^^

.. java:method:: @Override public List<SimEntity> getEntityList()
   :outertype: CloudSim

getEntityName
^^^^^^^^^^^^^

.. java:method:: @Override public String getEntityName(int entityId)
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

.. java:method:: @Override public void holdEntity(int src, long delay)
   :outertype: CloudSim

isPaused
^^^^^^^^

.. java:method:: @Override public boolean isPaused()
   :outertype: CloudSim

isRunning
^^^^^^^^^

.. java:method:: @Override public boolean isRunning()
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

.. java:method:: @Override public void pauseEntity(int src, double delay)
   :outertype: CloudSim

removeOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnClockTickListener(EventListener<EventInfo> listener)
   :outertype: CloudSim

removeOnEventProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnEventProcessingListener(EventListener<SimEvent> listener)
   :outertype: CloudSim

removeOnSimulationPausedListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnSimulationPausedListener(EventListener<EventInfo> listener)
   :outertype: CloudSim

resume
^^^^^^

.. java:method:: @Override public boolean resume()
   :outertype: CloudSim

select
^^^^^^

.. java:method:: @Override public SimEvent select(int dest, Predicate<SimEvent> p)
   :outertype: CloudSim

send
^^^^

.. java:method:: @Override public void send(int src, int dest, double delay, int tag, Object data)
   :outertype: CloudSim

sendFirst
^^^^^^^^^

.. java:method:: @Override public void sendFirst(int src, int dest, double delay, int tag, Object data)
   :outertype: CloudSim

sendNow
^^^^^^^

.. java:method:: @Override public void sendNow(int src, int dest, int tag, Object data)
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

updateEntityName
^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean updateEntityName(String oldName)
   :outertype: CloudSim

wait
^^^^

.. java:method:: @Override public void wait(CloudSimEntity src, Predicate<SimEvent> p)
   :outertype: CloudSim

waiting
^^^^^^^

.. java:method:: @Override public long waiting(int dest, Predicate<SimEvent> p)
   :outertype: CloudSim

