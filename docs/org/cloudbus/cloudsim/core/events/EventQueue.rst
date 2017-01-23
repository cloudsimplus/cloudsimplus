.. java:import:: java.util Iterator

.. java:import:: java.util NoSuchElementException

.. java:import:: java.util.stream Stream

EventQueue
==========

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: public interface EventQueue

   An interface to be implemented by event queues.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Methods
-------
addEvent
^^^^^^^^

.. java:method::  void addEvent(SimEvent newEvent)
   :outertype: EventQueue

   Adds a new event to the queue. Adding a new event to the queue preserves the temporal order of the events in the queue.

   :param newEvent: The event to be put in the queue.

first
^^^^^

.. java:method::  SimEvent first() throws NoSuchElementException
   :outertype: EventQueue

   Gets the first element of the queue.

   :throws NoSuchElementException: when the queue is empty
   :return: the first element

isEmpty
^^^^^^^

.. java:method::  boolean isEmpty()
   :outertype: EventQueue

   Checks if the queue is empty.

   :return: true if the queue is empty, false otherwise

iterator
^^^^^^^^

.. java:method::  Iterator<SimEvent> iterator()
   :outertype: EventQueue

   Returns an iterator to the elements into the queue.

   :return: the iterator

size
^^^^

.. java:method::  int size()
   :outertype: EventQueue

   Returns the size of this event queue.

   :return: the size

stream
^^^^^^

.. java:method::  Stream<SimEvent> stream()
   :outertype: EventQueue

   Returns a stream to the elements into the queue.

   :return: the stream

