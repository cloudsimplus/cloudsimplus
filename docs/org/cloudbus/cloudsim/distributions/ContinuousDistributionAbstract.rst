.. java:import:: org.apache.commons.math3.distribution RealDistribution

ContinuousDistributionAbstract
==============================

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public abstract class ContinuousDistributionAbstract implements ContinuousDistribution

   An base class for implementation of \ :java:ref:`ContinuousDistribution`\ s.

   :author: Manoel Campos da Silva Filho

Constructors
------------
ContinuousDistributionAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected ContinuousDistributionAbstract(RealDistribution numGen)
   :outertype: ContinuousDistributionAbstract

   Creates a new continuous random number generator using the current time as seed.

   :param numGen: the actual random number generator that will be the base to generate random numbers following a continuous distribution.

ContinuousDistributionAbstract
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected ContinuousDistributionAbstract(RealDistribution numGen, long seed)
   :outertype: ContinuousDistributionAbstract

   Creates a new continuous random number generator.

   :param numGen: the actual random number generator that will be the base to generate random numbers following a continuous distribution.
   :param seed: the seed to initialize the random number generator. If it is passed -1, the current time will be used

Methods
-------
getSeed
^^^^^^^

.. java:method:: @Override public final long getSeed()
   :outertype: ContinuousDistributionAbstract

sample
^^^^^^

.. java:method:: @Override public double sample()
   :outertype: ContinuousDistributionAbstract

setSeed
^^^^^^^

.. java:method:: protected final void setSeed(long seed)
   :outertype: ContinuousDistributionAbstract

