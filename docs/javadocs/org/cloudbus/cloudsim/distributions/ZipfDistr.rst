.. java:import:: org.apache.commons.math3.distribution UniformRealDistribution

ZipfDistr
=========

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class ZipfDistr extends ContinuousDistributionAbstract

   A pseudo random number generator following the \ `Zipf <http://en.wikipedia.org/wiki/Zipf's_law>`_\  distribution.

   :author: Marcos Dias de Assuncao

Constructors
------------
ZipfDistr
^^^^^^^^^

.. java:constructor:: public ZipfDistr(long seed, double shape, int population)
   :outertype: ZipfDistr

   Instantiates a new Zipf pseudo random number generator.

   :param seed: the seed
   :param shape: the shape
   :param population: the population

ZipfDistr
^^^^^^^^^

.. java:constructor:: public ZipfDistr(double shape, int population)
   :outertype: ZipfDistr

   Instantiates a new Zipf pseudo random number generator.

   :param shape: the shape
   :param population: the population

Methods
-------
sample
^^^^^^

.. java:method:: @Override public double sample()
   :outertype: ZipfDistr

