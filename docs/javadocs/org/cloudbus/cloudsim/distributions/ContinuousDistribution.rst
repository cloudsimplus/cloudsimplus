.. java:import:: org.apache.commons.math3.distribution RealDistribution

.. java:import:: org.apache.commons.math3.random RandomGenerator

.. java:import:: org.apache.commons.math3.random Well19937c

ContinuousDistribution
======================

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public interface ContinuousDistribution

   Interface to be implemented by a Pseudo-Random Number Generator (PRNG) that follows a defined statistical continuous distribution.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  ContinuousDistribution NULL
   :outertype: ContinuousDistribution

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`ContinuousDistribution`\  objects.

Methods
-------
defaultSeed
^^^^^^^^^^^

.. java:method:: static long defaultSeed()
   :outertype: ContinuousDistribution

getSeed
^^^^^^^

.. java:method::  long getSeed()
   :outertype: ContinuousDistribution

   Gets the seed used to initialize the generator

newDefaultGen
^^^^^^^^^^^^^

.. java:method:: static RandomGenerator newDefaultGen(long seed)
   :outertype: ContinuousDistribution

   Instantiates a \ :java:ref:`Well19937c`\  as the default \ :java:ref:`Pseudo-Random Number Generator <RandomGenerator>`\  (PRNG) used by \ ``ContinuousDistribution``\ .

   \ :java:ref:`Well19937c`\  is the PRNG used by \ :java:ref:`RealDistribution`\  implementations of the \ :java:ref:`org.apache.commons.math3`\ . Classes in such a library are used internally by \ ``ContinuousDistribution``\  implementations to provide PRNGs following some statistical distributions.

   Despite the classes from \ :java:ref:`org.apache.commons.math3`\  use the same \ :java:ref:`RandomGenerator`\  defined here, providing a \ :java:ref:`RandomGenerator`\  when instantiate a \ ``ContinuousDistribution``\  allow the researcher to define any PRNG by calling the appropriate \ ``ContinuousDistribution``\  constructor. For instance, the \ :java:ref:`UniformDistr.UniformDistr(long,RandomGenerator)`\  constructor enables providing a different PRNG, while the \ :java:ref:`UniformDistr.UniformDistr(long)`\  uses the PRNG instantiated here.

   By calling a constructor that accepts a \ :java:ref:`RandomGenerator`\ , the researcher may provide a different PRNG with either higher performance or better statistical properties (it's difficult to have both properties on the same PRNG).

   :param seed: the seed to set

sample
^^^^^^

.. java:method::  double sample()
   :outertype: ContinuousDistribution

   Generate a new pseudo random number.

   :return: the next pseudo random number in the sequence, following the implemented distribution.

