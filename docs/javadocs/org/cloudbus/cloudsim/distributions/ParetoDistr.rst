.. java:import:: org.apache.commons.math3.distribution ParetoDistribution

.. java:import:: org.apache.commons.math3.random JDKRandomGenerator

.. java:import:: org.apache.commons.math3.random RandomGenerator

ParetoDistr
===========

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class ParetoDistr extends ParetoDistribution implements ContinuousDistribution

   A Pseudo-Random Number Generator following the \ `Pareto <https://en.wikipedia.org/wiki/Pareto_distribution>`_\  distribution.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Constructors
------------
ParetoDistr
^^^^^^^^^^^

.. java:constructor:: public ParetoDistr(double shape, double location)
   :outertype: ParetoDistr

   Creates a Pareto Pseudo-Random Number Generator (RNG) using the current time as seed.

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param shape: the shape parameter of this distribution
   :param location: the location parameter of this distribution

   **See also:** :java:ref:`.ParetoDistr(double,double,long,RandomGenerator)`

ParetoDistr
^^^^^^^^^^^

.. java:constructor:: public ParetoDistr(double shape, double location, long seed)
   :outertype: ParetoDistr

   Creates a Pareto Pseudo-Random Number Generator (RNG).

   Internally, it relies on the \ :java:ref:`JDKRandomGenerator`\ , a wrapper for the \ :java:ref:`java.util.Random`\  class that doesn't have high-quality randomness properties but is very fast.

   :param shape: the shape parameter of this distribution
   :param location: the location parameter of this distribution
   :param seed: the seed

   **See also:** :java:ref:`.ParetoDistr(double,double,long,RandomGenerator)`

ParetoDistr
^^^^^^^^^^^

.. java:constructor:: public ParetoDistr(double shape, double location, long seed, RandomGenerator rng)
   :outertype: ParetoDistr

   Creates a Pareto Pseudo-Random Number Generator (RNG).

   :param shape: the shape parameter of this distribution
   :param location: the location parameter of this distribution
   :param seed: the seed \ **already used**\  to initialize the Pseudo-Random Number Generator
   :param rng: the actual Pseudo-Random Number Generator that will be the base to generate random numbers following a continuous distribution.

Methods
-------
getSeed
^^^^^^^

.. java:method:: @Override public long getSeed()
   :outertype: ParetoDistr

reseedRandomGenerator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reseedRandomGenerator(long seed)
   :outertype: ParetoDistr

