.. java:import:: com.google.gson Gson

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: org.cloudsimplus.vmtemplates AwsEc2Template

.. java:import:: java.io InputStream

.. java:import:: java.io InputStreamReader

.. java:import:: java.util ArrayList

.. java:import:: java.util List

SlaContract
===========

.. java:package:: org.cloudsimplus.slametrics
   :noindex:

.. java:type:: public class SlaContract

   Represents a SLA Contract containing a list of metrics. It follows the standard used by \ `Amazon Cloudwatch <http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html>`_\ .

   The constants inside the class define the names of SLA Metrics supported in the JSON SLA Contract format.

   Instances of this class can be created from a JSON file using the \ :java:ref:`getInstanceInternal(InputStream)`\  or \ :java:ref:`getInstance(String)`\  methods. This way, one doesn't need to create instances of this class using its default constructor. This one is just used by the JSON parsing library.

   For more details, check \ `Raysa Oliveira's Master Thesis (only in Portuguese) <http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf>`_\ .

   :author: raysaoliveira

Constructors
------------
SlaContract
^^^^^^^^^^^

.. java:constructor:: public SlaContract()
   :outertype: SlaContract

   Default constructor used to create a \ :java:ref:`SlaContract`\  instance. If you want to get a contract from a JSON file, you shouldn't call the constructor directly. Instead, use some methods of the class methods.

   This constructor is just provided to enable the \ :java:ref:`Gson`\  object to use reflection to instantiate a SlaContract.

   **See also:** :java:ref:`.getInstance(String)`

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

getExpectedMaxPriceForSingleVm
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getExpectedMaxPriceForSingleVm()
   :outertype: SlaContract

   Gets the expected maximum price a single VM can cost, considering the \ :java:ref:`Fault Tolerance Level <getMinFaultToleranceLevel()>`\ .

   :return: the expected maximum price a single VM can cost for the given customer \ :java:ref:`AwsEc2Template`\  for the customer's expected price

getFaultToleranceLevel
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SlaMetric getFaultToleranceLevel()
   :outertype: SlaContract

getInstance
^^^^^^^^^^^

.. java:method:: public static SlaContract getInstance(String jsonFilePath)
   :outertype: SlaContract

   Gets an \ :java:ref:`SlaContract`\  from a JSON file inside the \ **application's resource directory**\ .

   :param jsonFilePath: the \ **relative path**\  to the JSON file representing the SLA contract to read
   :return: a \ :java:ref:`SlaContract`\  read from the JSON file

getMaxPrice
^^^^^^^^^^^

.. java:method:: public double getMaxPrice()
   :outertype: SlaContract

   Gets the maximum price a customer expects to pay hourly for all his/her running VMs.

getMetrics
^^^^^^^^^^

.. java:method:: public List<SlaMetric> getMetrics()
   :outertype: SlaContract

   :return: the metrics

getMinFaultToleranceLevel
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getMinFaultToleranceLevel()
   :outertype: SlaContract

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

setMetrics
^^^^^^^^^^

.. java:method:: public void setMetrics(List<SlaMetric> metrics)
   :outertype: SlaContract

   :param metrics: the metrics to set

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: SlaContract

