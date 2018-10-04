.. java:import:: org.apache.commons.math3.util CombinatoricsUtils

.. java:import:: java.util.function BiConsumer

.. java:import:: java.util.function Function

.. java:import:: java.util.stream IntStream

PoissonDistr
============

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class PoissonDistr implements ContinuousDistribution

   A pseudo random number generator which returns numbers following a Poisson Distribution, modeling the probability of an event to happen a number of times in a given time interval.

   :author: Manoel Campos da Silva Filho

   **See also:** \ `Poisson Distribution <https://en.wikipedia.org/wiki/Poisson_distribution>`_\, \ `Poisson Point Process <https://en.wikipedia.org/wiki/Poisson_point_process>`_\

Constructors
------------
PoissonDistr
^^^^^^^^^^^^

.. java:constructor:: public PoissonDistr(double lambda, long seed)
   :outertype: PoissonDistr

   Creates a new Poisson random number generator to check the probability of 1 event (\ :java:ref:`k <getK()>`\ ) to happen at each time interval.

   :param lambda: the average number of events that happen at each 1 time unit. If one considers the unit as minute, this value means the average number of arrivals at each minute.
   :param seed: the seed to initialize the uniform random number generator

   **See also:** :java:ref:`.setK(int)`, :java:ref:`.setLambda(double)`

PoissonDistr
^^^^^^^^^^^^

.. java:constructor:: public PoissonDistr(double lambda)
   :outertype: PoissonDistr

   Creates a new Poisson process that considers you want to check the probability of 1 event (\ :java:ref:`k <getK()>`\ ) to happen at each time.

   :param lambda: average number of events by interval. For instance, if it was defined 1 event to be expected at each 2.5 minutes, it means that 0.4 event is expected at each minute (1/2.5).

   **See also:** :java:ref:`.setK(int)`

Methods
-------
eventsArrivalProbability
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double eventsArrivalProbability()
   :outertype: PoissonDistr

   Gets the probability to arrive \ :java:ref:`K <getK()>`\  events in the current time, considering the expected average arrival time \ :java:ref:`lambda <getLambda()>`\ . It computes the Probability Mass Function (PMF) of the Poisson distribution.

   **See also:** \ `Poisson distribution <https://en.wikipedia.org/wiki/Poisson_distribution>`_\

eventsHappened
^^^^^^^^^^^^^^

.. java:method:: public boolean eventsHappened()
   :outertype: PoissonDistr

   Checks if at the current time, \ :java:ref:`K <getK()>`\  events have happened, considering the \ :java:ref:`probability of these K events <eventsArrivalProbability()>`\  to happen in a time interval.

   :return: true if the K events have happened at current time, false otherwise

getInterArrivalMeanTime
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getInterArrivalMeanTime()
   :outertype: PoissonDistr

   Gets the mean time between arrival of two events, which is the inverse of lambda. The time unit (if seconds, minutes, hours, etc) is the same considered when setting a value to the \ :java:ref:`lambda <getLambda()>`\  parameter.

getK
^^^^

.. java:method:: public int getK()
   :outertype: PoissonDistr

   Gets the number of events to check the probability for them to happen in a time interval (default 1).

getLambda
^^^^^^^^^

.. java:method:: public double getLambda()
   :outertype: PoissonDistr

   Gets the average number of events that are expected to happen at each 1 time unit. It is the expected number of events to happen each time, also called the \ **event rate**\  or \ **rate parameter**\ .

   If the unit is minute, this value means the average number of arrivals at each minute. It's the inverse of the \ :java:ref:`getInterArrivalMeanTime()`\ .

getSeed
^^^^^^^

.. java:method:: @Override public long getSeed()
   :outertype: PoissonDistr

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: PoissonDistr

   Tests the simulations of customers arrivals in a Poisson process. All the code inside this method is just to try the class. That is way it declares internal methods as Functional objects, instead of declaring such methods at the class level and just calling them.

   :param args:

sample
^^^^^^

.. java:method:: @Override public double sample()
   :outertype: PoissonDistr

   Gets a random number that represents the next time for an event to happen, considering the \ :java:ref:`events arrival rate (lambda) <getLambda()>`\ .

setK
^^^^

.. java:method:: public void setK(int k)
   :outertype: PoissonDistr

   Sets the number of events to check the probability to happen in a time time.

   :param k: the value to set

