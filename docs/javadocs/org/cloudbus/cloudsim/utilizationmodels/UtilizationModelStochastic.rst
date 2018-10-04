.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.distributions UniformDistr

.. java:import:: java.util HashMap

.. java:import:: java.util Map

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

   Instantiates a new utilization model stochastic that defines the resource utilization in percentage.

UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic(Unit unit)
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic where the resource utilization is defined in the given unit.

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)

UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic(Unit unit, long seed)
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic using a given seed and where the resource utilization is defined in the given unit.

   :param unit: the \ :java:ref:`Unit`\  that determines how the resource is used (for instance, if resource usage is defined in percentage of the Vm resource or in absolute values)
   :param seed: the seed to generate the pseudo random utilization values

UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic(long seed)
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic.

   :param seed: the seed to generate the pseudo random utilization values

Methods
-------
getHistory
^^^^^^^^^^

.. java:method:: protected Map<Double, Double> getHistory()
   :outertype: UtilizationModelStochastic

   Gets the utilization history map, where each key is a time and each value is the resource utilization in that time.

   :return: the utilization history

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

loadHistory
^^^^^^^^^^^

.. java:method:: @SuppressWarnings public void loadHistory(String filename)
   :outertype: UtilizationModelStochastic

   Load an utilization history from a file.

   :param filename: the filename
   :throws IOException: when the file cannot be accessed

saveHistory
^^^^^^^^^^^

.. java:method:: public void saveHistory(String filename)
   :outertype: UtilizationModelStochastic

   Save the utilization history to a file.

   :param filename: the filename
   :throws IOException: when the file cannot be accessed

setHistory
^^^^^^^^^^

.. java:method:: protected final void setHistory(Map<Double, Double> history)
   :outertype: UtilizationModelStochastic

   Sets the utilization history map, where each key is a time and each value is the resource utilization in that time.

   :param history: the history to set

setRandomGenerator
^^^^^^^^^^^^^^^^^^

.. java:method:: public final void setRandomGenerator(ContinuousDistribution randomGenerator)
   :outertype: UtilizationModelStochastic

   Sets the random number generator.

   :param randomGenerator: the new random number generator

