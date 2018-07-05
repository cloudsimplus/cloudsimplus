.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Stream

FutureQueue
===========

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: public class FutureQueue implements EventQueue

   This class implements the future event queue used by \ :java:ref:`CloudSim`\ . The event queue uses a \ :java:ref:`TreeSet`\  in order to store the events.

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`java.util.TreeSet`

Methods
-------
addEvent
^^^^^^^^

.. java:method:: @Override public void addEvent(SimEvent newEvent)
   :outertype: FutureQueue

addEventFirst
^^^^^^^^^^^^^

.. java:method:: public void addEventFirst(SimEvent newEvent)
   :outertype: FutureQueue

   Adds a new event to the head of the queue.

   :param newEvent: The event to be put in the queue.

clear
^^^^^

.. java:method:: public void clear()
   :outertype: FutureQueue

   Clears the queue.

first
^^^^^

.. java:method:: @Override public SimEvent first() throws NoSuchElementException
   :outertype: FutureQueue

isEmpty
^^^^^^^

.. java:method:: @Override public boolean isEmpty()
   :outertype: FutureQueue

iterator
^^^^^^^^

.. java:method:: @Override public Iterator<SimEvent> iterator()
   :outertype: FutureQueue

remove
^^^^^^

.. java:method:: public boolean remove(SimEvent event)
   :outertype: FutureQueue

   Removes the event from the queue.

   :param event: the event
   :return: true, if successful

removeAll
^^^^^^^^^

.. java:method:: public boolean removeAll(Collection<SimEvent> events)
   :outertype: FutureQueue

   Removes all the events from the queue.

   :param events: the events
   :return: true, if successful

removeIf
^^^^^^^^

.. java:method:: public boolean removeIf(Predicate predicate)
   :outertype: FutureQueue

size
^^^^

.. java:method:: @Override public int size()
   :outertype: FutureQueue

stream
^^^^^^

.. java:method:: @Override public Stream<SimEvent> stream()
   :outertype: FutureQueue

