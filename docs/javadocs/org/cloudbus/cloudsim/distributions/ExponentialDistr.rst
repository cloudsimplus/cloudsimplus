.. java:import:: org.apache.commons.math3.distribution ExponentialDistribution

.. java:import:: org.apache.commons.math3.random JDKRandomGenerator

.. java:import:: org.apache.commons.math3.random RandomGenerator

ExponentialDistr
================

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class ExponentialDistr extends ExponentialDistribution implements ContinuousDistribution

   A Pseudo-Random Number Generator following the \ `Exponential distribution <https://en.wikipedia.org/wiki/Exponential_distribution>`_\ .

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Constructors
------------
ExponentialDistr
^^^^^^^^^^^^^^^^

.. java:constructor:: public ExponentialDistr(double mean)
   :outertype: ExponentialDistr

   Creates a exponential Pseudo-Random Number Generator (RNG).

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param mean: the mean for the distribution.

   **See also:** :java:ref:`.ExponentialDistr(double,long,RandomGenerator)`

ExponentialDistr
^^^^^^^^^^^^^^^^

.. java:constructor:: public ExponentialDistr(double mean, long seed)
   :outertype: ExponentialDistr

   Creates a exponential Pseudo-Random Number Generator (RNG).

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param mean: the mean for the distribution.
   :param seed: the seed to be used.

   **See also:** :java:ref:`.ExponentialDistr(double,long,RandomGenerator)`

ExponentialDistr
^^^^^^^^^^^^^^^^

.. java:constructor:: public ExponentialDistr(double mean, long seed, RandomGenerator rng)
   :outertype: ExponentialDistr

   Creates a exponential Pseudo-Random Number Generator (RNG).

   :param mean: the mean for the distribution.
   :param seed: the seed \ **already used**\  to initialize the Pseudo-Random Number Generator
   :param rng: the actual Pseudo-Random Number Generator that will be the base to generate random numbers following a continuous distribution.

Methods
-------
getSeed
^^^^^^^

.. java:method:: @Override public long getSeed()
   :outertype: ExponentialDistr

reseedRandomGenerator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reseedRandomGenerator(long seed)
   :outertype: ExponentialDistr

