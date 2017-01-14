.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: java.util.function Predicate

PredicateAny
============

.. java:package:: org.cloudbus.cloudsim.core.predicates
   :noindex:

.. java:type:: public class PredicateAny implements Predicate<SimEvent>

   A predicate which will match any event on the deferred event queue. See the publicly accessible instance of this predicate in \ :java:ref:`org.cloudbus.cloudsim.core.CloudSim.SIM_ANY`\ , so no new instances needs to be created.

   :author: Marcos Dias de Assuncao

   **See also:** :java:ref:`Predicate`

Methods
-------
test
^^^^

.. java:method:: @Override public boolean test(SimEvent ev)
   :outertype: PredicateAny

   Considers that any event received by the predicate will match.

   :param ev: {@inheritDoc}
   :return: always true to indicate that any received event is accepted

