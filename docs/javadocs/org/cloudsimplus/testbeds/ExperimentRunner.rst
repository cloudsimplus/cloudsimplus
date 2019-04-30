.. java:import:: ch.qos.logback.classic Level

.. java:import:: org.apache.commons.math3.distribution TDistribution

.. java:import:: org.apache.commons.math3.exception MathIllegalArgumentException

.. java:import:: org.apache.commons.math3.stat.descriptive SummaryStatistics

.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.distributions UniformDistr

.. java:import:: org.cloudsimplus.util Log

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util.stream IntStream

ExperimentRunner
================

.. java:package:: org.cloudsimplus.testbeds
   :noindex:

.. java:type:: public abstract class ExperimentRunner<T extends Experiment> implements Runnable

   A base class to run a given experiment a defined number of times and collect statistics about the execution. The runner represents a testbed compounded of a set of experiments that it runs.

   :author: Manoel Campos da Silva Filho
   :param <T>: the type of \ :java:ref:`Experiment`\  the runner will execute

Constructors
------------
ExperimentRunner
^^^^^^^^^^^^^^^^

.. java:constructor:: public ExperimentRunner(boolean antitheticVariatesTechnique)
   :outertype: ExperimentRunner

   Creates an experiment runner, setting the \ :java:ref:`base seed <getBaseSeed()>`\  as the current time.

   :param antitheticVariatesTechnique: indicates if it's to be applied the \ `antithetic variates technique <https://en.wikipedia.org/wiki/Antithetic_variates>`_\ .

ExperimentRunner
^^^^^^^^^^^^^^^^

.. java:constructor:: public ExperimentRunner(boolean antitheticVariatesTechnique, long baseSeed)
   :outertype: ExperimentRunner

   Creates an experiment runner with a given \ :java:ref:`base seed <getBaseSeed()>`\ .

   :param antitheticVariatesTechnique: indicates if it's to be applied the \ `antithetic variates technique <https://en.wikipedia.org/wiki/Antithetic_variates>`_\ .
   :param baseSeed: the seed to be used as base for each experiment seed

Methods
-------
addSeed
^^^^^^^

.. java:method::  void addSeed(long seed)
   :outertype: ExperimentRunner

   Adds a seed to the list of seeds used for each experiment.

   :param seed: seed of the current experiment to add to the list

batchSizeCeil
^^^^^^^^^^^^^

.. java:method:: public int batchSizeCeil()
   :outertype: ExperimentRunner

   :return: the batch size rounded by the \ :java:ref:`Math.ceil(double)`\  method.

computeAntitheticMeans
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Double> computeAntitheticMeans(List<Double> samples)
   :outertype: ExperimentRunner

   Computes the antithetic means for the given samples if the \ :java:ref:`"Antithetic Variates Technique" is to be applied <isApplyAntitheticVariatesTechnique()>`\ . These values are the mean between the first half of samples with the second half. By this way, the resulting value is an array with half of the samples length.

   \ **NOTE:**\  To correctly compute the antithetic values the seeds from the first half of experiments must be used for the second half.

   :param samples: the list of samples to compute the antithetic means from
   :return: the computed antithetic means from the given samples if the "Antithetic Variates Technique" is to be applied, otherwise return the same given samples list.

   **See also:** :java:ref:`.createRandomGen(int,double,double)`

computeBatchMeans
^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Double> computeBatchMeans(List<Double> samples)
   :outertype: ExperimentRunner

   Gets an list of samples and apply the "Batch Means Method" to reduce samples correlation, if the "Batch Means Method" \ :java:ref:`is to be applied <isApplyBatchMeansMethod()>`\ .

   :param samples: the list with samples to apply the "Batch Means Method". Samples size is defined by the \ :java:ref:`getSimulationRuns()`\ .
   :return: the samples list after applying the "Batch Means Method", in case the method is enabled to be applied, which will reduce the array to the number of batches defined by \ :java:ref:`getNumberOfBatches()`\  (each value in the returned array will be the mean of every sample batch). Otherwise, returns the same given array

computeConfidenceErrorMargin
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected double computeConfidenceErrorMargin(SummaryStatistics stats, double confidenceLevel)
   :outertype: ExperimentRunner

   Computes the confidence interval error margin for a given set of samples in order to enable finding the interval lower and upper bound around a mean value. By this way, the confidence interval can be computed as [mean + errorMargin .. mean - errorMargin].

   To reduce the confidence interval by half, one have to execute the experiments 4 more times. This is called the "Replication Method" and just works when the samples are i.i.d. (independent and identically distributed). Thus, if you have correlation between samples of each simulation run, a different method such as a bias compensation, \ :java:ref:`batch means <isApplyBatchMeansMethod()>`\  or regenerative method has to be used.

   \ **NOTE:**\  How to compute the error margin is a little bit confusing. The Harry Perros' book states that if less than 30 samples are collected, the t-Distribution has to be used to that purpose. However, this article \ `Wikipedia article <https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps>`_\  says that if the standard deviation of the real population is known, it has to be used the z-value from the Standard Normal Distribution. Otherwise, it has to be used the t-value from the t-Distribution to calculate the critical value for defining the error margin (also called standard error). The book "Numeric Computation and Statistical Data Analysis on the Java Platform" confirms the last statement and such approach was followed.

   :param stats: the statistic object with the values to compute the error margin of the confidence interval
   :param confidenceLevel: the confidence level, in the interval from ]0 to 1[, such as 0.95 to indicate 95% of confidence.
   :return: the error margin to compute the lower and upper bound of the confidence interval

   **See also:** \ `Critical Values of the Student's t Distribution <http://www.itl.nist.gov/div898/handbook/eda/section3/eda3672.htm>`_\, \ `t-Distribution <https://en.wikipedia.org/wiki/Student%27s_t-distribution>`_\, \ `Harry Perros, "Computer Simulation Techniques: The definitive introduction!," 2009 <http://www4.ncsu.edu/~hp/files/simulation.pdf>`_\, \ `Numeric Computation and Statistical Data Analysis on the Java Platform <http://www.springer.com/gp/book/9783319285290>`_\

computeFinalStatistics
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected SummaryStatistics computeFinalStatistics(List<Double> values)
   :outertype: ExperimentRunner

   Creates a SummaryStatistics object from a list of Double values, allowing computation of statistics such as mean over these values. The method also checks if the \ :java:ref:`Antithetic Variates <isApplyAntitheticVariatesTechnique()>`\  and the \ :java:ref:`Batch Means <isApplyBatchMeansMethod()>`\  techniques are enabled and then apply them over the given list of Doubles. These techniques are used for variance reduction.

   :param values: the List of values to add to the \ :java:ref:`SummaryStatistics`\  object
   :return: the \ :java:ref:`SummaryStatistics`\  object containing the double values, after applying the the techniques for variance reduction.

createExperiment
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract T createExperiment(int i)
   :outertype: ExperimentRunner

   Creates an experiment to be run for the i'th time.

   :param i: a number that identifies the experiment
   :return: the created experiment

createMetricsMap
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract Map<String, List<Double>> createMetricsMap()
   :outertype: ExperimentRunner

   Creates a Map adding a List of values for each metric to be computed. The computation of final experiments results are performed on this map.

   Each key is the name of metric and each value is a List of Double containing the values collected for that metric, for each experiment run. These values will be then summarized to compute the final value for each metric.

   :return: the populated metricsMap

createRandomGen
^^^^^^^^^^^^^^^

.. java:method:: public ContinuousDistribution createRandomGen(int experimentIndex)
   :outertype: ExperimentRunner

   Creates a pseudo random number generator (PRNG) for a experiment run that generates uniform values between [0 and 1[. If it is to apply the \ :java:ref:`"Antithetic Variates Technique" <isApplyAntitheticVariatesTechnique()>`\  to reduce results variance, the second half of experiments will used the seeds from the first half.

   :param experimentIndex: index of the experiment run to create a PRNG
   :return: the created PRNG

   **See also:** :java:ref:`UniformDistr.isApplyAntitheticVariates()`, :java:ref:`.createRandomGen(int,double,double)`

createRandomGen
^^^^^^^^^^^^^^^

.. java:method:: public ContinuousDistribution createRandomGen(int experimentIndex, double minInclusive, double maxExclusive)
   :outertype: ExperimentRunner

   Creates a pseudo random number generator (PRNG) for a experiment run that generates uniform values between [min and max[. If it is to apply the \ :java:ref:`"Antithetic Variates Technique" <isApplyAntitheticVariatesTechnique()>`\  to reduce results' variance, the second half of experiments will use the seeds from the first half.

   :param experimentIndex: index of the experiment run to create a PRNG
   :param minInclusive: the minimum value the generator will return (inclusive)
   :param maxExclusive: the maximum value the generator will return (exclusive)
   :return: the created PRNG

   **See also:** :java:ref:`UniformDistr.isApplyAntitheticVariates()`, :java:ref:`.createRandomGen(int)`

getBaseSeed
^^^^^^^^^^^

.. java:method:: public long getBaseSeed()
   :outertype: ExperimentRunner

   Gets the seed to be used for the first executed experiment. The seed for each subsequent experiment is this seed plus the index of the experiment.

getExperimentsFinishTime
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public long getExperimentsFinishTime()
   :outertype: ExperimentRunner

   Time in seconds the experiments finished.

getExperimentsStartTime
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public long getExperimentsStartTime()
   :outertype: ExperimentRunner

   Time in seconds the experiments started.

getNumberOfBatches
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getNumberOfBatches()
   :outertype: ExperimentRunner

   Gets the number of batches in which the simulation runs will be divided. If this number is greater than 1, the "Batch Means Method" is used to reduce the correlation between experiment runs.

getSeed
^^^^^^^

.. java:method::  long getSeed(int experimentIndex)
   :outertype: ExperimentRunner

getSimulationRuns
^^^^^^^^^^^^^^^^^

.. java:method:: public int getSimulationRuns()
   :outertype: ExperimentRunner

   Gets the number of times the experiment will be executed in order to get values such as means and standard deviations. It has to be an even number if the \ :java:ref:`"Antithetic Variates Technique" <isApplyAntitheticVariatesTechnique()>`\  is to be used.

halfSimulationRuns
^^^^^^^^^^^^^^^^^^

.. java:method:: public int halfSimulationRuns()
   :outertype: ExperimentRunner

   :return: the half of \ :java:ref:`getSimulationRuns()`\

isApplyAntitheticVariatesTechnique
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isApplyAntitheticVariatesTechnique()
   :outertype: ExperimentRunner

   Checks if the "Antithetic Variates Technique" is to be applied to reduce results variance.

   **See also:** \ `Antithetic variates <https://en.wikipedia.org/wiki/Antithetic_variates>`_\

isApplyBatchMeansMethod
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isApplyBatchMeansMethod()
   :outertype: ExperimentRunner

   Checks if the "Batch Means Method" is to be applied to reduce correlation between the results for different experiment runs.

isToReuseSeedFromFirstHalfOfExperiments
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isToReuseSeedFromFirstHalfOfExperiments(int currentExperimentIndex)
   :outertype: ExperimentRunner

isVerbose
^^^^^^^^^

.. java:method:: public boolean isVerbose()
   :outertype: ExperimentRunner

   Indicates if the runner will output execution logs or not. This doesn't affect the verbosity of individual experiments executed. Each \ :java:ref:`Experiment`\  has its own verbose attribute.

printFinalResults
^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printFinalResults(String metricName, SummaryStatistics stats)
   :outertype: ExperimentRunner

   Prints final simulation results such as means, standard deviations and confidence intervals.

   :param metricName: the name of the metric to be printed
   :param stats: the \ :java:ref:`SummaryStatistics`\  containing means of each experiment run that will be used to computed an overall mean and other statistics

printSimulationParameters
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void printSimulationParameters()
   :outertype: ExperimentRunner

run
^^^

.. java:method:: @Override public void run()
   :outertype: ExperimentRunner

   Setups and starts the execution of all experiments.

setBaseSeed
^^^^^^^^^^^

.. java:method:: public final ExperimentRunner setBaseSeed(long baseSeed)
   :outertype: ExperimentRunner

setNumberOfBatches
^^^^^^^^^^^^^^^^^^

.. java:method:: public final ExperimentRunner setNumberOfBatches(int numberOfBatches)
   :outertype: ExperimentRunner

   Sets the number of batches in which the simulation runs will be divided.

   :param numberOfBatches: number of simulation run batches

   **See also:** :java:ref:`.getNumberOfBatches()`

setSimulationRuns
^^^^^^^^^^^^^^^^^

.. java:method:: protected ExperimentRunner setSimulationRuns(int simulationRuns)
   :outertype: ExperimentRunner

setSimulationRunsAsMultipleOfBatchNumber
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected ExperimentRunner setSimulationRunsAsMultipleOfBatchNumber()
   :outertype: ExperimentRunner

   Adjusts the current number of simulations to be equal to its closer multiple of the number of batches.

setVerbose
^^^^^^^^^^

.. java:method:: public ExperimentRunner setVerbose(boolean verbose)
   :outertype: ExperimentRunner

   Defines if the runner will output execution logs or not. This doesn't affect the verbosity of individual experiments executed. Each \ :java:ref:`Experiment`\  has its own verbose attribute.

   :param verbose: true if results have to be output, false otherwise

setup
^^^^^

.. java:method:: protected abstract void setup()
   :outertype: ExperimentRunner

   Setup experiment attributes considering the dependency between each other. The method is called by the \ :java:ref:`run()`\  method, just after all the attributes were set. By this way, it initializes internal attributes and validates other ones.

   \ **NOTE:**\  As a good practice, it is tried to reduce the number of parameters for the class constructor, as it tends to increase as the experiment code evolves. Accordingly, all the parameters have to be defined using the corresponding setters. By this way, it has to be avoided setting up attributes inside the constructor,
   once they can become invalid or out-of-date because dependency between
   parameters. The constructor has just to initialize objects to avoid \ :java:ref:`NullPointerException`\ . By this way, one have to set all the parameters inside this method. For instance, if the constructor creates and Random Number Generator (PRNG) using a default seed but the method setSeed is called after the constructor, the PRNG will not be update to use the new seed.

simulationRunsAndNumberOfBatchesAreCompatible
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean simulationRunsAndNumberOfBatchesAreCompatible()
   :outertype: ExperimentRunner

   Checks if the number of simulation runs and the number of batches are compatible

