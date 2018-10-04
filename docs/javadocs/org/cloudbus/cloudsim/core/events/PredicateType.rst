.. java:import:: java.util.function Predicate

PredicateType
=============

.. java:package:: org.cloudbus.cloudsim.core.events
   :noindex:

.. java:type:: public class PredicateType implements Predicate<SimEvent>

   A predicate to select events with specific \ :java:ref:`tag <SimEvent.getTag()>`\ .

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`Predicate`

Constructors
------------
PredicateType
^^^^^^^^^^^^^

.. java:constructor:: public PredicateType(int tag)
   :outertype: PredicateType

   Constructor used to select events with the given tag value.

   :param tag: an event \ :java:ref:`tag <SimEvent.getTag()>`\  value

Methods
-------
test
^^^^

.. java:method:: @Override public boolean test(SimEvent evt)
   :outertype: PredicateType

   Matches any event that has one of the specified \ :java:ref:`tag`\ .

   :param evt: {@inheritDoc}
   :return: {@inheritDoc}

   **See also:** :java:ref:`.tag`

