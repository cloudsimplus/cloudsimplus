.. java:import:: org.apache.commons.math3.distribution GammaDistribution

.. java:import:: org.apache.commons.math3.random JDKRandomGenerator

.. java:import:: org.apache.commons.math3.random RandomGenerator

GammaDistr
==========

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class GammaDistr extends GammaDistribution implements ContinuousDistribution

   A Pseudo-Random Number Generator following the \ `Gamma <https://en.wikipedia.org/wiki/Gamma_distribution>`_\  distribution.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Constructors
------------
GammaDistr
^^^^^^^^^^

.. java:constructor:: public GammaDistr(int shape, double scale)
   :outertype: GammaDistr

   Creates a Gamma Pseudo-Random Number Generator (RNG) using the current time as seed.

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param shape: the shape parameter of this distribution
   :param scale: the scale parameter of this distribution

   **See also:** :java:ref:`.GammaDistr(int,double,long,RandomGenerator)`

GammaDistr
^^^^^^^^^^

.. java:constructor:: public GammaDistr(int shape, double scale, long seed)
   :outertype: GammaDistr

   Creates a Gamma Pseudo-Random Number Generator (RNG).

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param shape: the shape parameter of this distribution
   :param scale: the scale parameter of this distribution
   :param seed: the seed

   **See also:** :java:ref:`.GammaDistr(int,double,long,RandomGenerator)`

GammaDistr
^^^^^^^^^^

.. java:constructor:: public GammaDistr(int shape, double scale, long seed, RandomGenerator rng)
   :outertype: GammaDistr

   Creates a Gamma Pseudo-Random Number Generator (RNG).

   :param shape: the shape parameter of this distribution
   :param scale: the scale parameter of this distribution
   :param seed: the seed \ **already used**\  to initialize the Pseudo-Random Number Generator
   :param rng: the actual Pseudo-Random Number Generator that will be the base to generate random numbers following a continuous distribution.

Methods
-------
getSeed
^^^^^^^

.. java:method:: @Override public long getSeed()
   :outertype: GammaDistr

reseedRandomGenerator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reseedRandomGenerator(long seed)
   :outertype: GammaDistr

