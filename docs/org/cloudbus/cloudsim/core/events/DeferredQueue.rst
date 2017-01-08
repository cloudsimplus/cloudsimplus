.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: java.util.stream Stream

DeferredQueue
=============

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: public class DeferredQueue implements EventQueue

   This class implements the deferred event queue used by \ :java:ref:`CloudSim`\ . The event queue uses a linked list to store the events.

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`CloudSim`, :java:ref:`SimEvent`

Methods
-------
addEvent
^^^^^^^^

.. java:method:: public void addEvent(SimEvent newEvent)
   :outertype: DeferredQueue

   Adds a new event to the queue. Adding a new event to the queue preserves the temporal order of the events.

   :param newEvent: The event to be added to the queue.

clear
^^^^^

.. java:method:: public void clear()
   :outertype: DeferredQueue

   Clears the queue.

first
^^^^^

.. java:method:: @Override public SimEvent first() throws NoSuchElementException
   :outertype: DeferredQueue

isEmpty
^^^^^^^

.. java:method:: @Override public boolean isEmpty()
   :outertype: DeferredQueue

iterator
^^^^^^^^

.. java:method:: public Iterator<SimEvent> iterator()
   :outertype: DeferredQueue

   Returns an iterator to the events in the queue.

   :return: the iterator

remove
^^^^^^

.. java:method:: public boolean remove(SimEvent event)
   :outertype: DeferredQueue

   Removes the event from the queue.

   :param event: the event
   :return: true, if successful

size
^^^^

.. java:method:: public int size()
   :outertype: DeferredQueue

   Returns the size of this event queue.

   :return: the number of events in the queue.

stream
^^^^^^

.. java:method:: public Stream<SimEvent> stream()
   :outertype: DeferredQueue

   Returns a stream to the elements into the queue.

   :return: the stream

