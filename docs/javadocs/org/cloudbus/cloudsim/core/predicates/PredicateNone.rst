.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: java.util.function Predicate

PredicateNone
=============

.. java:package:: org.cloudbus.cloudsim.core.predicates
   :noindex:

.. java:type:: public class PredicateNone implements Predicate<SimEvent>

   A predicate which will \ **not**\  match any event on the deferred event queue. See the publicly accessible instance of this predicate in \ :java:ref:`org.cloudbus.cloudsim.core.CloudSim.SIM_NONE`\ , so no new instances needs to be created.

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`Predicate`

Methods
-------
test
^^^^

.. java:method:: @Override public boolean test(SimEvent ev)
   :outertype: PredicateNone

   Considers that no event received by the predicate matches.

   :param ev: {@inheritDoc}
   :return: always false to indicate that no event is accepted

