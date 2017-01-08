.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util List

.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Collectors

PredicateType
=============

.. java:package:: org.cloudbus.cloudsim.core.predicates
   :noindex:

.. java:type:: public class PredicateType implements Predicate<SimEvent>

   A predicate to select events with specific \ :java:ref:`tags <SimEvent.getTag()>`\ .

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`PredicateNotType`, :java:ref:`Predicate`

Constructors
------------
PredicateType
^^^^^^^^^^^^^

.. java:constructor:: public PredicateType(int tag)
   :outertype: PredicateType

   Constructor used to select events with the given tag value.

   :param tag: an event \ :java:ref:`tag <SimEvent.getTag()>`\  value

PredicateType
^^^^^^^^^^^^^

.. java:constructor:: public PredicateType(int[] tags)
   :outertype: PredicateType

   Constructor used to select events with a tag value equal to any of the specified tags.

   :param tags: the list of \ :java:ref:`tags <SimEvent.getTag()>`\

Methods
-------
test
^^^^

.. java:method:: @Override public boolean test(SimEvent ev)
   :outertype: PredicateType

   Matches any event that has one of the specified \ :java:ref:`tags`\ .

   :param ev: {@inheritDoc}
   :return: {@inheritDoc}

   **See also:** :java:ref:`.tags`

