.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.network.topologies NetworkTopology

.. java:import:: org.cloudsimplus.listeners EventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: java.util Calendar

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Set

.. java:import:: java.util.function Predicate

SimulationNull
==============

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: final class SimulationNull implements Simulation

   A class that implements the Null Object Design Pattern for \ :java:ref:`Simulation`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Simulation.NULL`

Methods
-------
abort
^^^^^

.. java:method:: @Override public void abort()
   :outertype: SimulationNull

addEntity
^^^^^^^^^

.. java:method:: @Override public void addEntity(CloudSimEntity entity)
   :outertype: SimulationNull

addOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Simulation addOnClockTickListener(EventListener<EventInfo> listener)
   :outertype: SimulationNull

addOnEventProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Simulation addOnEventProcessingListener(EventListener<SimEvent> listener)
   :outertype: SimulationNull

addOnSimulationPauseListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Simulation addOnSimulationPauseListener(EventListener<EventInfo> listener)
   :outertype: SimulationNull

addOnSimulationStartListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Simulation addOnSimulationStartListener(EventListener<EventInfo> listener)
   :outertype: SimulationNull

cancel
^^^^^^

.. java:method:: @Override public SimEvent cancel(SimEntity src, Predicate<SimEvent> predicate)
   :outertype: SimulationNull

cancelAll
^^^^^^^^^

.. java:method:: @Override public boolean cancelAll(SimEntity src, Predicate<SimEvent> predicate)
   :outertype: SimulationNull

clock
^^^^^

.. java:method:: @Override public double clock()
   :outertype: SimulationNull

clockInHours
^^^^^^^^^^^^

.. java:method:: @Override public double clockInHours()
   :outertype: SimulationNull

clockInMinutes
^^^^^^^^^^^^^^

.. java:method:: @Override public double clockInMinutes()
   :outertype: SimulationNull

findFirstDeferred
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public SimEvent findFirstDeferred(SimEntity dest, Predicate<SimEvent> predicate)
   :outertype: SimulationNull

getCalendar
^^^^^^^^^^^

.. java:method:: @Override public Calendar getCalendar()
   :outertype: SimulationNull

getCloudInfoService
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public CloudInformationService getCloudInfoService()
   :outertype: SimulationNull

getDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Set<Datacenter> getDatacenterList()
   :outertype: SimulationNull

getEntityList
^^^^^^^^^^^^^

.. java:method:: @Override public List<SimEntity> getEntityList()
   :outertype: SimulationNull

getMinTimeBetweenEvents
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getMinTimeBetweenEvents()
   :outertype: SimulationNull

getNetworkTopology
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public NetworkTopology getNetworkTopology()
   :outertype: SimulationNull

getNumEntities
^^^^^^^^^^^^^^

.. java:method:: @Override public int getNumEntities()
   :outertype: SimulationNull

getNumberOfFutureEvents
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public long getNumberOfFutureEvents(Predicate<SimEvent> predicate)
   :outertype: SimulationNull

holdEntity
^^^^^^^^^^

.. java:method:: @Override public void holdEntity(SimEntity src, long delay)
   :outertype: SimulationNull

isPaused
^^^^^^^^

.. java:method:: @Override public boolean isPaused()
   :outertype: SimulationNull

isRunning
^^^^^^^^^

.. java:method:: @Override public boolean isRunning()
   :outertype: SimulationNull

isTerminationTimeSet
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isTerminationTimeSet()
   :outertype: SimulationNull

isTimeToTerminateSimulationUnderRequest
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isTimeToTerminateSimulationUnderRequest()
   :outertype: SimulationNull

pause
^^^^^

.. java:method:: @Override public boolean pause()
   :outertype: SimulationNull

pause
^^^^^

.. java:method:: @Override public boolean pause(double time)
   :outertype: SimulationNull

pauseEntity
^^^^^^^^^^^

.. java:method:: @Override public void pauseEntity(SimEntity src, double delay)
   :outertype: SimulationNull

removeOnClockTickListener
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnClockTickListener(EventListener<? extends EventInfo> listener)
   :outertype: SimulationNull

removeOnEventProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnEventProcessingListener(EventListener<SimEvent> listener)
   :outertype: SimulationNull

removeOnSimulationPauseListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean removeOnSimulationPauseListener(EventListener<EventInfo> listener)
   :outertype: SimulationNull

resume
^^^^^^

.. java:method:: @Override public boolean resume()
   :outertype: SimulationNull

select
^^^^^^

.. java:method:: @Override public SimEvent select(SimEntity dest, Predicate<SimEvent> predicate)
   :outertype: SimulationNull

send
^^^^

.. java:method:: @Override public void send(SimEvent evt)
   :outertype: SimulationNull

send
^^^^

.. java:method:: @Override public void send(SimEntity src, SimEntity dest, double delay, int tag, Object data)
   :outertype: SimulationNull

sendFirst
^^^^^^^^^

.. java:method:: @Override public void sendFirst(SimEvent evt)
   :outertype: SimulationNull

sendFirst
^^^^^^^^^

.. java:method:: @Override public void sendFirst(SimEntity src, SimEntity dest, double delay, int tag, Object data)
   :outertype: SimulationNull

sendNow
^^^^^^^

.. java:method:: @Override public void sendNow(SimEntity src, SimEntity dest, int tag, Object data)
   :outertype: SimulationNull

setNetworkTopology
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setNetworkTopology(NetworkTopology networkTopology)
   :outertype: SimulationNull

start
^^^^^

.. java:method:: @Override public double start() throws RuntimeException
   :outertype: SimulationNull

terminate
^^^^^^^^^

.. java:method:: @Override public boolean terminate()
   :outertype: SimulationNull

terminateAt
^^^^^^^^^^^

.. java:method:: @Override public boolean terminateAt(double time)
   :outertype: SimulationNull

wait
^^^^

.. java:method:: @Override public void wait(CloudSimEntity src, Predicate<SimEvent> predicate)
   :outertype: SimulationNull

waiting
^^^^^^^

.. java:method:: @Override public long waiting(SimEntity dest, Predicate<SimEvent> predicate)
   :outertype: SimulationNull

