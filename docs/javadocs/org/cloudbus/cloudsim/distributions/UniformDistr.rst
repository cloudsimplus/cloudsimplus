.. java:import:: org.apache.commons.math3.distribution UniformRealDistribution

.. java:import:: java.util Random

UniformDistr
============

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class UniformDistr extends ContinuousDistributionAbstract

   A pseudo random number generator following the \ `Uniform continuous distribution <https://en.wikipedia.org/wiki/Uniform_distribution_(continuous)>`_\ .

   :author: Marcos Dias de Assuncao

Constructors
------------
UniformDistr
^^^^^^^^^^^^

.. java:constructor:: public UniformDistr()
   :outertype: UniformDistr

   Creates new uniform pseudo random number generator that generates values between [0 and 1[ using the current time as seed.

UniformDistr
^^^^^^^^^^^^

.. java:constructor:: public UniformDistr(long seed)
   :outertype: UniformDistr

   Creates new uniform pseudo random number generator that generates values between [0 and 1[ using a given seed.

   :param seed: simulation seed to be used

UniformDistr
^^^^^^^^^^^^

.. java:constructor:: public UniformDistr(double max)
   :outertype: UniformDistr

   Creates new uniform pseudo random number generator that produces values between a 0 (inclusive) and max (exclusive).

   :param max: maximum value (exclusive)

UniformDistr
^^^^^^^^^^^^

.. java:constructor:: public UniformDistr(double min, double max)
   :outertype: UniformDistr

   Creates new uniform pseudo random number generator that produces values between a min (inclusive) and max (exclusive).

   :param min: minimum value (inclusive)
   :param max: maximum value (exclusive)

UniformDistr
^^^^^^^^^^^^

.. java:constructor:: public UniformDistr(double min, double max, long seed)
   :outertype: UniformDistr

   Creates new uniform pseudo random number generator.

   :param min: minimum value (inclusive)
   :param max: maximum value (exclusive)
   :param seed: simulation seed to be used

Methods
-------
isApplyAntitheticVariates
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isApplyAntitheticVariates()
   :outertype: UniformDistr

   Indicates if the pseudo random number generator (PRNG) has to apply the \ `Antithetic Variates Technique <https://en.wikipedia.org/wiki/Antithetic_variates>`_\  in order to reduce variance of experiments using this PRNG. This technique doesn't work for all the cases. However, in the cases it can be applied, in order to it work, one have to perform some actions. Consider an experiment that has to run "n" times. The first half of these experiments has to use the seeds the developer want. However, the second half of the experiments have to set the applyAntitheticVariates attribute to true and use the seeds of the first half of experiments. Thus, the first half of experiments are run using PRNGs that return random numbers as U(0, 1)[seed_1], ..., U(0, 1)[seed_n]. The second half of experiments then uses the seeds of the first half of experiments, returning random numbers as 1 - U(0, 1)[seed_1], ..., 1 - U(0, 1)[seed_n].

   :return: true if the technique has to be applied, false otherwise

sample
^^^^^^

.. java:method:: @Override public double sample()
   :outertype: UniformDistr

sample
^^^^^^

.. java:method:: public static double sample(Random rd, double min, double max)
   :outertype: UniformDistr

   Generates a new pseudo random number based on the generator and values provided as parameters.

   :param rd: the random number generator
   :param min: the minimum value
   :param max: the maximum value
   :return: the next random number in the sequence

setApplyAntitheticVariates
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public UniformDistr setApplyAntitheticVariates(boolean applyAntitheticVariates)
   :outertype: UniformDistr

   Defines if the pseudo random number generator (PRNG) has to apply the \ `Antithetic Variates Technique <https://en.wikipedia.org/wiki/Antithetic_variates>`_\  in order to reduce variance of experiments using this PRNG.

   :param applyAntitheticVariates: true if the technique has to be applied, false otherwise

   **See also:** :java:ref:`.isApplyAntitheticVariates()`

