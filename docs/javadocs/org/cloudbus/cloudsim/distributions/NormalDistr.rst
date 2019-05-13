.. java:import:: org.apache.commons.math3.distribution NormalDistribution

.. java:import:: org.apache.commons.math3.random JDKRandomGenerator

.. java:import:: org.apache.commons.math3.random RandomGenerator

NormalDistr
===========

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class NormalDistr extends NormalDistribution implements ContinuousDistribution

   A Pseudo-Random Number Generator following the \ `Normal (Gaussian) distribution <https://en.wikipedia.org/wiki/Normal_distribution>`_\ .

   :author: Manoel Campos da Silva Filho

Constructors
------------
NormalDistr
^^^^^^^^^^^

.. java:constructor:: public NormalDistr(double mean, double standardDeviation)
   :outertype: NormalDistr

   Creates a Normal (Gaussian) Pseudo-Random Number Generator (RNG) using the current time as seed.

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param mean: the mean for the distribution.
   :param standardDeviation: the standard deviation for the distribution.

   **See also:** :java:ref:`.NormalDistr(double,double,long,RandomGenerator)`

NormalDistr
^^^^^^^^^^^

.. java:constructor:: public NormalDistr(double mean, double standardDeviation, long seed)
   :outertype: NormalDistr

   Creates a Normal (Gaussian) Pseudo-Random Number Generator (RNG).

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param mean: the mean for the distribution.
   :param standardDeviation: the standard deviation for the distribution.
   :param seed: the seed to be used.

   **See also:** :java:ref:`.NormalDistr(double,double,long,RandomGenerator)`

NormalDistr
^^^^^^^^^^^

.. java:constructor:: public NormalDistr(double mean, double standardDeviation, long seed, RandomGenerator rng)
   :outertype: NormalDistr

   Creates a Normal (Gaussian) Pseudo-Random Number Generator (RNG).

   :param mean: the mean for the distribution.
   :param standardDeviation: the standard deviation for the distribution.
   :param seed: the seed \ **already used**\  to initialize the Pseudo-Random Number Generator
   :param rng: the actual Pseudo-Random Number Generator that will be the base to generate random numbers following a continuous distribution.

Methods
-------
getSeed
^^^^^^^

.. java:method:: @Override public long getSeed()
   :outertype: NormalDistr

reseedRandomGenerator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reseedRandomGenerator(long seed)
   :outertype: NormalDistr

