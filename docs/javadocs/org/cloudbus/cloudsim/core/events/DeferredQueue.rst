.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Stream

DeferredQueue
=============

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: public class DeferredQueue implements EventQueue

   An \ :java:ref:`EventQueue`\  that orders \ :java:ref:`SimEvent`\ s based on their time attribute. Since a new event's time is usually equal or higher than the previous event in regular simulations, this classes uses a \ :java:ref:`LinkedList`\  instead of a \ :java:ref:`java.util.SortedSet`\  such as \ :java:ref:`java.util.TreeSet`\  because the \ :java:ref:`LinkedList`\  provides constant O(1) complexity to add elements to the end.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Methods
-------
addEvent
^^^^^^^^

.. java:method:: public void addEvent(SimEvent newEvent)
   :outertype: DeferredQueue

   Adds a new event to the queue, preserving the temporal order of the events.

   :param newEvent: the event to be added to the queue.

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

removeAll
^^^^^^^^^

.. java:method:: public boolean removeAll(Collection<SimEvent> events)
   :outertype: DeferredQueue

   Removes all the events from the queue.

   :param events: the events
   :return: true, if successful

removeIf
^^^^^^^^

.. java:method:: public boolean removeIf(Predicate<SimEvent> predicate)
   :outertype: DeferredQueue

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

