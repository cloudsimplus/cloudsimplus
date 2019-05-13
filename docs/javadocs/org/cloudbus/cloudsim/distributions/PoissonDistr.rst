.. java:import:: org.apache.commons.math3.util CombinatoricsUtils

.. java:import:: java.util.function BiConsumer

.. java:import:: java.util.function Function

.. java:import:: java.util.stream IntStream

PoissonDistr
============

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class PoissonDistr implements ContinuousDistribution

   A Pseudo-Random Number Generator which returns numbers following a Poisson Distribution, modeling the probability of an event to happen a number of times in a given time interval.

   :author: Manoel Campos da Silva Filho

   **See also:** \ `Poisson Distribution <https://en.wikipedia.org/wiki/Poisson_distribution>`_\

Constructors
------------
PoissonDistr
^^^^^^^^^^^^

.. java:constructor:: public PoissonDistr(double lambda, long seed)
   :outertype: PoissonDistr

   Creates a Poisson Pseudo-Random Number Generator to check the probability of 1 event (\ :java:ref:`k = 1 <getK()>`\ ) to happen at each time interval.

   :param lambda: the average number of events that happen at each 1 time unit. If one considers the unit as minute, this value means the average number of arrivals at each minute.
   :param seed: the seed to initialize the internal uniform Pseudo-Random Number Generator

   **See also:** :java:ref:`.setK(int)`, :java:ref:`.setLambda(double)`

PoissonDistr
^^^^^^^^^^^^

.. java:constructor:: public PoissonDistr(double lambda)
   :outertype: PoissonDistr

   Creates a Poisson Pseudo-Random Number Generator to check the probability of 1 event (\ :java:ref:`k = 1 <getK()>`\ ) to happen at each time interval.

   :param lambda: average number of events by interval. For instance, if it was defined 1 event to be expected at each 2.5 minutes, it means that 0.4 event is expected at each minute (1/2.5).

   **See also:** :java:ref:`.setK(int)`

Methods
-------
eventsArrivalProbability
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double eventsArrivalProbability()
   :outertype: PoissonDistr

   Gets the probability to arrive \ :java:ref:`k <getK()>`\  events in the current time, considering the mean arrival time \ :java:ref:`lambda (λ) <getLambda()>`\ , which is represented as \ ``Pr(k events in time period)``\ . It computes the Probability Mass Function (PMF) of the Poisson distribution.

   :return: the probability of a \ :java:ref:`random variable <sample()>`\  to be equal to k

   **See also:** \ `Poisson Probability Mass Function <https://en.wikipedia.org/wiki/Poisson_distribution#Definition>`_\

eventsHappened
^^^^^^^^^^^^^^

.. java:method:: public boolean eventsHappened()
   :outertype: PoissonDistr

   Checks if at the current time, \ :java:ref:`k <getK()>`\  events have happened, considering the \ :java:ref:`probability of these k events <eventsArrivalProbability()>`\  to happen in a time interval.

   :return: true if k events have happened at the current time, false otherwise

getInterArrivalMeanTime
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getInterArrivalMeanTime()
   :outertype: PoissonDistr

   Gets the mean time between arrival of two events, which is the inverse of \ :java:ref:`lambda (λ) <getLambda()>`\ . The time unit (if seconds, minutes, hours, etc) is the same considered when setting a value to the \ :java:ref:`lambda <getLambda()>`\  attribute.

getK
^^^^

.. java:method:: public int getK()
   :outertype: PoissonDistr

   Gets the number of events to check the probability for them to happen in a time interval (default 1).

getLambda
^^^^^^^^^

.. java:method:: public double getLambda()
   :outertype: PoissonDistr

   Gets the average number of events (λ) that are expected to happen at each 1 time unit. It is the expected number of events to happen each time, also called the \ **event rate**\  or \ **rate parameter**\ .

   If one considers the unit as minute, this value means the average number of arrivals at each minute. It's the inverse of the \ :java:ref:`getInterArrivalMeanTime()`\ .

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

   Gets a random number that represents the next time (from current time or last generated event) that an event will happen, considering the events arrival rate defined by \ :java:ref:`lambda (λ) <getLambda()>`\ . The time unit (if seconds, minutes, hours, etc) is the same considered when setting a value to the \ :java:ref:`lambda <getLambda()>`\  attribute.

   Calling this method for the first time returns the next event arrival time. The retuning values for consecutive calls can be dealt in one of the following ways:

   ..

   * If you are generating all random event arrivals at the beginning of the simulation, you need to add the previous time to the next event arrival time. This way, the arrival time of the previous event is added to the next one. For instance, if consecutive calls to this method return the values 60 and 25, from the current time, that means: (i) the first event will arrive in 60 seconds; (ii) the next event will arrive in 85 seconds, that is 25 seconds after the first one.
   * If you are generating event arrivals during simulation runtime, you must NOT add the previous time to the generated event time, just use the returned value as the event arrival time.

   Poisson inter-arrival times are independent and identically distributed exponential random variables with mean 1/λ.

   **See also:** \ `Monte Carlo Methods and Models in Finance and Insurance. Ralf Korn, Elke Korn, et al. 1st edition, 2010. Section 2.4.1: Exponential distribution. Page 33. <https://books.google.com.br/books?isbn=1420076191>`_\, \ `Related distributions <https://en.wikipedia.org/wiki/Poisson_distribution#Related_distributions>`_\

setK
^^^^

.. java:method:: public void setK(int k)
   :outertype: PoissonDistr

   Sets the number of events to check the probability to happen in a time interval.

   :param k: the value to set

