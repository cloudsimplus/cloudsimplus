.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.distributions UniformDistr

.. java:import:: java.util HashMap

.. java:import:: java.util Map

.. java:import:: java.util Objects

UtilizationModelStochastic
==========================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelStochastic extends UtilizationModelAbstract

   Implements a model, according to which a Cloudlet generates random resource utilization every time frame.

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Constructors
------------
UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic()
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic that defines the resource utilization in percentage. The resource utilization history is disabled by default.

   **See also:** :java:ref:`.setUnit(Unit)`, :java:ref:`.setHistoryEnabled(boolean)`, :java:ref:`.isAlwaysGenerateNewRandomUtilization()`

UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic(Unit unit)
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic where the resource utilization is defined in the given unit. The resource utilization history is disabled by default.

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)

   **See also:** :java:ref:`.setHistoryEnabled(boolean)`, :java:ref:`.isAlwaysGenerateNewRandomUtilization()`

UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic(Unit unit, long seed)
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic where the resource utilization is defined in the given unit. The resource utilization history is disabled by default.

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)
   :param seed: the seed to initialize the random number generator. If -1 is passed, the current time will be used.

   **See also:** :java:ref:`.setHistoryEnabled(boolean)`, :java:ref:`.isAlwaysGenerateNewRandomUtilization()`

UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic(ContinuousDistribution prng)
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic based on a given Pseudo Random Number Generator (PRNG) It defines the resource utilization in percentage. The resource utilization history is disabled by default.

   :param prng: the Pseudo Random Number Generator (PRNG) to generate utilization values

   **See also:** :java:ref:`.setUnit(Unit)`, :java:ref:`.setHistoryEnabled(boolean)`, :java:ref:`.isAlwaysGenerateNewRandomUtilization()`

UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic(Unit unit, ContinuousDistribution prng)
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic based on a given Pseudo Random Number Generator (PRNG). The resource utilization history is disabled by default.

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)
   :param prng: the Pseudo Random Number Generator (PRNG) to generate utilization values

   **See also:** :java:ref:`.setHistoryEnabled(boolean)`, :java:ref:`.isAlwaysGenerateNewRandomUtilization()`

Methods
-------
getRandomGenerator
^^^^^^^^^^^^^^^^^^

.. java:method:: public ContinuousDistribution getRandomGenerator()
   :outertype: UtilizationModelStochastic

   Gets the random number generator.

   :return: the random number generator

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization(double time)
   :outertype: UtilizationModelStochastic

   {@inheritDoc}

   The method may return different utilization values for the same requested time. For performance reasons, this behaviour is dependent of the \ :java:ref:`isHistoryEnabled()`\  and \ :java:ref:`isAlwaysGenerateNewRandomUtilization()`\ .

   :param time: {@inheritDoc}
   :return: {@inheritDoc}

   **See also:** \ `Issue #197 for more details <https://github.com/manoelcampos/cloudsim-plus/issues/197>`_\

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Double getUtilizationHistory(double time)
   :outertype: UtilizationModelStochastic

   Gets the utilization percentage for a given time from the internal \ :java:ref:`historyMap`\ .

   :param time: the time to get the utilization history for
   :return: the stored utilization percentage or \ **null**\  if it has never been generated an utilization value for the given time

isAlwaysGenerateNewRandomUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isAlwaysGenerateNewRandomUtilization()
   :outertype: UtilizationModelStochastic

   Checks if every time the \ :java:ref:`getUtilization()`\  or \ :java:ref:`getUtilization(double)`\  methods are called, a new randomly generated utilization will be returned or not. This attribute is false by default, meaning that consecutive utilization requests for the same time may return the same previous generated utilization value. Check the documentation in the return section at the end for details.

   Using one instance of this utilization model for every Cloudlet in a large simulation scenario may be very expensive in terms of simulation time and memory consumption. This way, the researcher may want to use a single utilization model instance for every Cloudlet. The side effect is that, if this attribute is false (the default), it will usually return the same utilization value for the same requested time for distinct Cloudlets. That commonly is not what the researcher wants. He/she usually wants that every Cloudlet has an independent resource utilization.

   To reduce simulation time and memory consumption, you can use a single utilization model instance for a given Cloudlet resource (such as CPU) and set this attribute to false. This way, it will always generate different utilization values for every time an utilization is requested (even if the same previous time is given).

   :return: true if a new randomly generated utilization will always be returned; false if for the same requested time, the same utilization must be returned. In this last case, it's just ensured that, for a given time, the same utilization will always be returned, if the \ :java:ref:`history is enabled <isHistoryEnabled()>`\ .

   **See also:** :java:ref:`.setAlwaysGenerateNewRandomUtilization(boolean)`

isHistoryEnabled
^^^^^^^^^^^^^^^^

.. java:method:: public boolean isHistoryEnabled()
   :outertype: UtilizationModelStochastic

   Checks if the history of resource utilization along simulation time is to be kept or not.

   :return: true if the history is to be kept, false otherwise

   **See also:** :java:ref:`.setHistoryEnabled(boolean)`

loadHistory
^^^^^^^^^^^

.. java:method:: @SuppressWarnings public void loadHistory(String filename)
   :outertype: UtilizationModelStochastic

   Load an utilization history from a file.

   :param filename: the filename
   :throws UncheckedIOException: when the file cannot be accessed

saveHistory
^^^^^^^^^^^

.. java:method:: public void saveHistory(String filename)
   :outertype: UtilizationModelStochastic

   Save the utilization history to a file.

   :param filename: the filename
   :throws UncheckedIOException: when the file cannot be accessed

setAlwaysGenerateNewRandomUtilization
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public UtilizationModelStochastic setAlwaysGenerateNewRandomUtilization(boolean alwaysGenerateNewRandomUtilization)
   :outertype: UtilizationModelStochastic

   Enables or disables the resource utilization history, so that utilization values is stored along all the simulation execution. Check information about trade-off between memory and CPU utilization in \ :java:ref:`setHistoryEnabled(boolean)`\ .

   :param alwaysGenerateNewRandomUtilization: true to enable the utilization history, false to disable

   **See also:** :java:ref:`.isAlwaysGenerateNewRandomUtilization()`

setHistoryEnabled
^^^^^^^^^^^^^^^^^

.. java:method:: public UtilizationModelStochastic setHistoryEnabled(boolean enable)
   :outertype: UtilizationModelStochastic

   Enables or disables the resource utilization history, so that utilization values are stored along all the simulation execution.

   If utilization history is disable, more pseudo-random numbers will be generated, decreasing simulation performance. Changing this attribute is a trade-off between memory and CPU utilization:

   ..

   * enabling reduces CPU utilization but increases RAM utilization;
   * disabling reduces RAM utilization but increases CPU utilization.

   :param enable: true to enable the utilization history, false to disable

setRandomGenerator
^^^^^^^^^^^^^^^^^^

.. java:method:: public final void setRandomGenerator(ContinuousDistribution randomGenerator)
   :outertype: UtilizationModelStochastic

   Sets the random number generator.

   :param randomGenerator: the new random number generator

