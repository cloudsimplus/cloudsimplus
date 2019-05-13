.. java:import:: org.apache.commons.math3.distribution WeibullDistribution

.. java:import:: org.apache.commons.math3.random JDKRandomGenerator

.. java:import:: org.apache.commons.math3.random RandomGenerator

WeibullDistr
============

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class WeibullDistr extends WeibullDistribution implements ContinuousDistribution

   A Pseudo-Random Number Generator following the \ `Weibull distribution <https://en.wikipedia.org/wiki/Weibull_distribution>`_\ .

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Constructors
------------
WeibullDistr
^^^^^^^^^^^^

.. java:constructor:: public WeibullDistr(double alpha, double beta)
   :outertype: WeibullDistr

   Creates a Weibull Pseudo-Random Number Generator (RNG) using a given seed.

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param alpha: the alpha distribution parameter
   :param beta: the beta distribution parameter

   **See also:** :java:ref:`.WeibullDistr(double,double,long,RandomGenerator)`

WeibullDistr
^^^^^^^^^^^^

.. java:constructor:: public WeibullDistr(double alpha, double beta, long seed)
   :outertype: WeibullDistr

   Creates a Weibull Pseudo-Random Number Generator (RNG).

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param alpha: the alpha distribution parameter
   :param beta: the beta distribution parameter
   :param seed: the seed

   **See also:** :java:ref:`.WeibullDistr(double,double,long,RandomGenerator)`

WeibullDistr
^^^^^^^^^^^^

.. java:constructor:: public WeibullDistr(double alpha, double beta, long seed, RandomGenerator rng)
   :outertype: WeibullDistr

   Creates a Weibull Pseudo-Random Number Generator (RNG).

   :param alpha: the alpha distribution parameter
   :param beta: the beta distribution parameter
   :param seed: the seed \ **already used**\  to initialize the Pseudo-Random Number Generator
   :param rng: the actual Pseudo-Random Number Generator that will be the base to generate random numbers following a continuous distribution.

Methods
-------
getSeed
^^^^^^^

.. java:method:: @Override public long getSeed()
   :outertype: WeibullDistr

reseedRandomGenerator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reseedRandomGenerator(long seed)
   :outertype: WeibullDistr

