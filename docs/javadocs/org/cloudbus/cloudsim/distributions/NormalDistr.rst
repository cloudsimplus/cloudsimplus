.. java:import:: org.apache.commons.math3.distribution NormalDistribution

NormalDistr
===========

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class NormalDistr extends ContinuousDistributionAbstract

   A pseudo random number generator following the \ `Normal (Gaussian) distribution <https://en.wikipedia.org/wiki/Normal_distribution>`_\ .

   :author: Manoel Campos da Silva Filho

Constructors
------------
NormalDistr
^^^^^^^^^^^

.. java:constructor:: public NormalDistr(long seed, double mean, double standardDeviation)
   :outertype: NormalDistr

   Creates a new normal (Gaussian) pseudo random number generator.

   :param seed: the seed to be used.
   :param mean: the mean for the distribution.
   :param standardDeviation: the standard deviation for the distribution.

NormalDistr
^^^^^^^^^^^

.. java:constructor:: public NormalDistr(double mean, double standardDeviation)
   :outertype: NormalDistr

   Creates a new normal (Gaussian) pseudo random number generator.

   :param mean: the mean for the distribution.
   :param standardDeviation: the standard deviation for the distribution.

