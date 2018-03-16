.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util List

.. java:import:: java.util.function Predicate

.. java:import:: java.util.stream Collectors

PredicateNotType
================

.. java:package:: org.cloudbus.cloudsim.core.predicates
   :noindex:

.. java:type:: public class PredicateNotType implements Predicate<SimEvent>

   A predicate to select events that don't match specific tags.

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`PredicateType`, :java:ref:`Predicate`

Constructors
------------
PredicateNotType
^^^^^^^^^^^^^^^^

.. java:constructor:: public PredicateNotType(int tag)
   :outertype: PredicateNotType

   Constructor used to select events whose tags do not match a given tag.

   :param tag: An event \ :java:ref:`tag <SimEvent.getTag()>`\  value

PredicateNotType
^^^^^^^^^^^^^^^^

.. java:constructor:: public PredicateNotType(int[] tags)
   :outertype: PredicateNotType

   Constructor used to select events whose tag values do not match any of the given tags.

   :param tags: the list of \ :java:ref:`tags <SimEvent.getTag()>`\

Methods
-------
test
^^^^

.. java:method:: @Override public boolean test(SimEvent ev)
   :outertype: PredicateNotType

   Matches any event that hasn't one of the specified \ :java:ref:`tags`\ .

   :param ev: {@inheritDoc}
   :return: {@inheritDoc}

   **See also:** :java:ref:`.tags`

