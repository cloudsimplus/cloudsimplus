.. java:import:: java.io FileInputStream

.. java:import:: java.io FileOutputStream

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutputStream

.. java:import:: java.util HashMap

.. java:import:: java.util Map

.. java:import:: java.util Random

UtilizationModelStochastic
==========================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelStochastic implements UtilizationModel

   Implements a model, according to which a Cloudlet generates random resource utilization every time frame.

   :author: Anton Beloglazov

Constructors
------------
UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic()
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic.

UtilizationModelStochastic
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UtilizationModelStochastic(long seed)
   :outertype: UtilizationModelStochastic

   Instantiates a new utilization model stochastic.

   :param seed: the seed

Methods
-------
getHistory
^^^^^^^^^^

.. java:method:: protected Map<Double, Double> getHistory()
   :outertype: UtilizationModelStochastic

   Gets the utilization history.

   :return: the history

getRandomGenerator
^^^^^^^^^^^^^^^^^^

.. java:method:: public Random getRandomGenerator()
   :outertype: UtilizationModelStochastic

   Gets the random generator.

   :return: the random generator

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization(double time)
   :outertype: UtilizationModelStochastic

loadHistory
^^^^^^^^^^^

.. java:method:: @SuppressWarnings public void loadHistory(String filename) throws Exception
   :outertype: UtilizationModelStochastic

   Load an utilization history from a file.

   :param filename: the filename
   :throws Exception: the exception

saveHistory
^^^^^^^^^^^

.. java:method:: public void saveHistory(String filename) throws Exception
   :outertype: UtilizationModelStochastic

   Save the utilization history to a file.

   :param filename: the filename
   :throws Exception: the exception

setHistory
^^^^^^^^^^

.. java:method:: protected final void setHistory(Map<Double, Double> history)
   :outertype: UtilizationModelStochastic

   Sets the utilization history.

   :param history: the history

setRandomGenerator
^^^^^^^^^^^^^^^^^^

.. java:method:: public final void setRandomGenerator(Random randomGenerator)
   :outertype: UtilizationModelStochastic

   Sets the random generator.

   :param randomGenerator: the new random generator

