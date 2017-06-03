.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util List

.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Collectors

PredicateNotFrom
================

.. java:package:: org.cloudbus.cloudsim.core.predicates
   :noindex:

.. java:type:: public class PredicateNotFrom implements Predicate<SimEvent>

   A predicate which selects events that have not been sent by specific entities.

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`PredicateFrom`, :java:ref:`Predicate`

Constructors
------------
PredicateNotFrom
^^^^^^^^^^^^^^^^

.. java:constructor:: public PredicateNotFrom(int sourceId)
   :outertype: PredicateNotFrom

   Constructor used to select events that were not sent by a specific entity.

   :param sourceId: the id number of the source entity

PredicateNotFrom
^^^^^^^^^^^^^^^^

.. java:constructor:: public PredicateNotFrom(int[] sourceIds)
   :outertype: PredicateNotFrom

   Constructor used to select events that were not sent by any entity from a given set.

   :param sourceIds: the set of id numbers of the source entities

Methods
-------
test
^^^^

.. java:method:: @Override public boolean test(SimEvent ev)
   :outertype: PredicateNotFrom

   Matches any event \ **NOT**\  received from the registered sources.

   :param ev: {@inheritDoc}
   :return: {@inheritDoc}

   **See also:** :java:ref:`.ids`

