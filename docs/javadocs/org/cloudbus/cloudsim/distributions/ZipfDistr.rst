.. java:import:: org.apache.commons.math3.random JDKRandomGenerator

.. java:import:: org.apache.commons.math3.random RandomGenerator

ZipfDistr
=========

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class ZipfDistr implements ContinuousDistribution

   A Pseudo-Random Number Generator following the \ `Zipf <http://en.wikipedia.org/wiki/Zipf's_law>`_\  distribution.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Constructors
------------
ZipfDistr
^^^^^^^^^

.. java:constructor:: public ZipfDistr(double shape, int population)
   :outertype: ZipfDistr

   Creates a Zipf Pseudo-Random Number Generator (RNG).

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param shape: the shape distribution parameter
   :param population: the population distribution parameter

   **See also:** :java:ref:`.ZipfDistr(double,int,long,RandomGenerator)`

ZipfDistr
^^^^^^^^^

.. java:constructor:: public ZipfDistr(double shape, int population, long seed)
   :outertype: ZipfDistr

   Creates a Zipf Pseudo-Random Number Generator (RNG).

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param shape: the shape distribution parameter
   :param population: the population distribution parameter
   :param seed: the seed

   **See also:** :java:ref:`.ZipfDistr(double,int,long,RandomGenerator)`

ZipfDistr
^^^^^^^^^

.. java:constructor:: public ZipfDistr(double shape, int population, long seed, RandomGenerator rng)
   :outertype: ZipfDistr

   Creates a Zipf Pseudo-Random Number Generator (RNG).

   :param shape: the shape distribution parameter
   :param population: the population distribution parameter
   :param seed: the seed \ **already used**\  to initialize the Pseudo-Random Number Generator
   :param rng: the actual Pseudo-Random Number Generator that will be the base to generate random numbers following a continuous distribution.

Methods
-------
getSeed
^^^^^^^

.. java:method:: @Override public long getSeed()
   :outertype: ZipfDistr

sample
^^^^^^

.. java:method:: @Override public double sample()
   :outertype: ZipfDistr

