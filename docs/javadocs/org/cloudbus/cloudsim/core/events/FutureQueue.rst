.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Stream

FutureQueue
===========

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: public class FutureQueue implements EventQueue

   An \ :java:ref:`EventQueue`\  that stores future simulation events. It uses a \ :java:ref:`TreeSet`\  in order ensure the events are stored ordered. Using a \ :java:ref:`java.util.LinkedList`\  as defined by \ :java:ref:`DeferredQueue`\  to improve performance doesn't work for this queue.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

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

getMaxEventsNumber
^^^^^^^^^^^^^^^^^^

.. java:method:: public long getMaxEventsNumber()
   :outertype: FutureQueue

   Maximum number of events that have ever existed at the same time inside the queue.

getSerial
^^^^^^^^^

.. java:method:: public long getSerial()
   :outertype: FutureQueue

   Gets an incremental number used for \ :java:ref:`SimEvent.getSerial()`\  event attribute.

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

.. java:method:: public boolean removeIf(Predicate<SimEvent> predicate)
   :outertype: FutureQueue

size
^^^^

.. java:method:: @Override public int size()
   :outertype: FutureQueue

stream
^^^^^^

.. java:method:: @Override public Stream<SimEvent> stream()
   :outertype: FutureQueue

