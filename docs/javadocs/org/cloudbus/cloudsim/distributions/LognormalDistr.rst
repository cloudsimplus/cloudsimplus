.. java:import:: org.apache.commons.math3.distribution LogNormalDistribution

.. java:import:: org.apache.commons.math3.random RandomGenerator

LognormalDistr
==============

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class LognormalDistr extends LogNormalDistribution implements ContinuousDistribution

   A Pseudo-Random Number Generator following the \ `Lognormal <https://en.wikipedia.org/wiki/Log-normal_distribution>`_\  distribution.

   :author: Marcos Dias de Assuncao

Constructors
------------
LognormalDistr
^^^^^^^^^^^^^^

.. java:constructor:: public LognormalDistr(double shape, double scale)
   :outertype: LognormalDistr

   Creates a Log-normal Pseudo-Random Number Generator (RNG).

   :param shape: the shape parameter of this distribution
   :param scale: the scale parameter of this distribution

LognormalDistr
^^^^^^^^^^^^^^

.. java:constructor:: public LognormalDistr(double shape, double scale, long seed)
   :outertype: LognormalDistr

   Creates a Log-normal Pseudo-Random Number Generator (RNG).

   :param shape: the shape parameter of this distribution
   :param scale: the scale parameter of this distribution
   :param seed: the seed

LognormalDistr
^^^^^^^^^^^^^^

.. java:constructor:: public LognormalDistr(double shape, double scale, long seed, RandomGenerator rng)
   :outertype: LognormalDistr

   Creates a Log-normal Pseudo-Random Number Generator (RNG).

   :param shape: the shape parameter of this distribution
   :param scale: the scale parameter of this distribution
   :param seed: the seed

Methods
-------
getSeed
^^^^^^^

.. java:method:: @Override public long getSeed()
   :outertype: LognormalDistr

reseedRandomGenerator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void reseedRandomGenerator(long seed)
   :outertype: LognormalDistr

