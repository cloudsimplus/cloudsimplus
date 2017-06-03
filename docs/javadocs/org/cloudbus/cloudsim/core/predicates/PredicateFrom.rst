.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util List

.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Collectors

PredicateFrom
=============

.. java:package:: org.cloudbus.cloudsim.core.predicates
   :noindex:

.. java:type:: public class PredicateFrom implements Predicate<SimEvent>

   A predicate which selects events coming from specific registered entities.

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`PredicateNotFrom`, :java:ref:`Predicate`

Constructors
------------
PredicateFrom
^^^^^^^^^^^^^

.. java:constructor:: public PredicateFrom(int sourceId)
   :outertype: PredicateFrom

   Constructor used to select events that were sent by a specific entity.

   :param sourceId: the id number of the source entity

PredicateFrom
^^^^^^^^^^^^^

.. java:constructor:: public PredicateFrom(int[] sourceIds)
   :outertype: PredicateFrom

   Constructor used to select events that were sent by any entity from a given set.

   :param sourceIds: the set of id numbers of the source entities

Methods
-------
test
^^^^

.. java:method:: @Override public boolean test(SimEvent ev)
   :outertype: PredicateFrom

   Matches any event received from the registered sources.

   :param ev: {@inheritDoc}
   :return: {@inheritDoc}

   **See also:** :java:ref:`.ids`

