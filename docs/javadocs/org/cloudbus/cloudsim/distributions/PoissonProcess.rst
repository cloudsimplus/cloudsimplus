.. java:import:: java.util.function BiConsumer

.. java:import:: java.util.function Function

.. java:import:: java.util.stream IntStream

.. java:import:: org.apache.commons.math3.util CombinatoricsUtils

PoissonProcess
==============

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class PoissonProcess

   Represents a Poisson Process which models the probability of an event to happen a number of times in a given time interval.

   :author: Manoel Campos da Silva Filho

   **See also:** \ `Poisson Distribution <https://en.wikipedia.org/wiki/Poisson_distribution>`_\, \ `Poisson Point Process <https://en.wikipedia.org/wiki/Poisson_point_process>`_\

Constructors
------------
PoissonProcess
^^^^^^^^^^^^^^

.. java:constructor:: public PoissonProcess(double lambda, long seed)
   :outertype: PoissonProcess

   Creates a new Poisson process to check the probability of 1 event (\ :java:ref:`k <getK()>`\ ) to happen at each time interval.

   :param lambda: the average number of events that happen at each 1 time unit.
   :param seed: the seed to initialize the uniform random number generator

   **See also:** :java:ref:`.setK(int)`, :java:ref:`.setLambda(double)`

PoissonProcess
^^^^^^^^^^^^^^

.. java:constructor:: public PoissonProcess(double lambda)
   :outertype: PoissonProcess

   Creates a new Poisson process that considers you want to check the probability of 1 event (\ :java:ref:`k <getK()>`\ ) to happen at each time.

   :param lambda: average number of events by interval

   **See also:** :java:ref:`.setK(int)`

Methods
-------
eventsArrivalProbability
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double eventsArrivalProbability()
   :outertype: PoissonProcess

   Gets the probability to arrive \ :java:ref:`K <getK()>`\  events in the current time, considering the expected average arrival time \ :java:ref:`lambda <getLambda()>`\ .

   **See also:** \ `Poisson distribution <https://en.wikipedia.org/wiki/Poisson_distribution>`_\

eventsHappened
^^^^^^^^^^^^^^

.. java:method:: public boolean eventsHappened()
   :outertype: PoissonProcess

   Checks if at the current time, \ :java:ref:`K <getK()>`\  events have happened, considering the probability of these K events to happen in a time interval.

   :return: true if the K events have happened at current time, false otherwise

   **See also:** :java:ref:`.eventsArrivalProbability()`

getInterarrivalMeanTime
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getInterarrivalMeanTime()
   :outertype: PoissonProcess

   Gets the mean time between arrival of two events.

getK
^^^^

.. java:method:: public int getK()
   :outertype: PoissonProcess

   Gets the number of events to check the probability to happen in a time interval (default 1).

getLambda
^^^^^^^^^

.. java:method:: public double getLambda()
   :outertype: PoissonProcess

   Gets the average number of events that are expected to happen at each 1 time unit. It is the expected number of events to happen each time, also called the \ **event rate**\  or \ **rate parameter**\ .

   If the unit is minute, this value means the average number of arrivals at each minute. It's the inverse of the \ :java:ref:`getInterarrivalMeanTime()`\ .

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: PoissonProcess

   Tests the simulations of customers arrivals in a Poisson process.

   :param args:

setK
^^^^

.. java:method:: public void setK(int k)
   :outertype: PoissonProcess

   Sets the number of events to check the probability to happen in a time time.

   :param k: the value to set

