.. java:import:: com.google.gson Gson

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io FileReader

.. java:import:: java.util ArrayList

.. java:import:: java.util List

SlaContract
===========

.. java:package:: org.cloudsimplus.slametrics
   :noindex:

.. java:type:: public class SlaContract

   Represents a SLA Contract containing a list of metrics. It follows the standard used by \ `Amazon Cloudwatch <http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html>`_\ .

   Instances of this class can be created from a JSON file using the \ :java:ref:`getInstance(String)`\  or \ :java:ref:`getInstanceFromResourcesDir(Class,String)`\  methods. This way, one doesn't need to create instances of this class using its default constructor. This one is just used by the JSON parsing library.

   :author: raysaoliveira

Constructors
------------
SlaContract
^^^^^^^^^^^

.. java:constructor:: public SlaContract()
   :outertype: SlaContract

   Default constructor used to create a \ :java:ref:`SlaContract`\  instance. If you want to get a contract from a JSON file, you shouldn't call the constructor directly. Instead, use some methods such as the \ :java:ref:`getInstance(String)`\ .

Methods
-------
getAvailabilityMetric
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SlaMetric getAvailabilityMetric()
   :outertype: SlaContract

getCpuUtilizationMetric
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SlaMetric getCpuUtilizationMetric()
   :outertype: SlaContract

getInstance
^^^^^^^^^^^

.. java:method:: public static SlaContract getInstance(String jsonFilePath) throws FileNotFoundException
   :outertype: SlaContract

   Gets an \ :java:ref:`SlaContract`\  from a JSON file.

   :param jsonFilePath: the full path to the JSON file representing the SLA contract to read
   :return: a \ :java:ref:`SlaContract`\  read from the JSON file

getInstanceFromResourcesDir
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static SlaContract getInstanceFromResourcesDir(Class klass, String jsonFilePath) throws FileNotFoundException
   :outertype: SlaContract

   Gets an \ :java:ref:`SlaContract`\  from a JSON file inside the application's resource directory.

   :param klass: a class from the project which will be used just to assist in getting the path of the given resource. It can can any class inside the project where a resource you are trying to get from the resources directory
   :param jsonFilePath: the relative path to the JSON file representing the SLA contract to read
   :return: a \ :java:ref:`SlaContract`\  read from the JSON file

getMetrics
^^^^^^^^^^

.. java:method:: public List<SlaMetric> getMetrics()
   :outertype: SlaContract

   :return: the metrics

getPriceMetric
^^^^^^^^^^^^^^

.. java:method:: public SlaMetric getPriceMetric()
   :outertype: SlaContract

getTaskCompletionTimeMetric
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SlaMetric getTaskCompletionTimeMetric()
   :outertype: SlaContract

getWaitTimeMetric
^^^^^^^^^^^^^^^^^

.. java:method:: public SlaMetric getWaitTimeMetric()
   :outertype: SlaContract

main
^^^^

.. java:method:: public static void main(String[] args) throws FileNotFoundException
   :outertype: SlaContract

   A main method just to try the class implementation.

   :param args:

setMetrics
^^^^^^^^^^

.. java:method:: public void setMetrics(List<SlaMetric> metrics)
   :outertype: SlaContract

   :param metrics: the metrics to set

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: SlaContract

