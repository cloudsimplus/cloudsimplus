.. java:import:: java.util ArrayList

.. java:import:: java.util List

SlaMetric
=========

.. java:package:: org.cloudsimplus.slametrics
   :noindex:

.. java:type:: public class SlaMetric

   Represents a metric of a SLA contract. Follows the standard defined by \ `AWS Cloudwatch <http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html>`_\ .

   For more details, check \ `Raysa Oliveira's Master Thesis (only in Portuguese) <http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf>`_\ .

   :author: raysaoliveira

Fields
------
NULL
^^^^

.. java:field:: public static final SlaMetric NULL
   :outertype: SlaMetric

Constructors
------------
SlaMetric
^^^^^^^^^

.. java:constructor:: public SlaMetric()
   :outertype: SlaMetric

SlaMetric
^^^^^^^^^

.. java:constructor:: public SlaMetric(String name)
   :outertype: SlaMetric

Methods
-------
getDimensions
^^^^^^^^^^^^^

.. java:method:: public List<SlaMetricDimension> getDimensions()
   :outertype: SlaMetric

getMaxDimension
^^^^^^^^^^^^^^^

.. java:method:: public SlaMetricDimension getMaxDimension()
   :outertype: SlaMetric

   Gets a \ :java:ref:`SlaMetricDimension`\  representing the maximum value expected for the metric. If the \ :java:ref:`SlaMetricDimension.getValue()`\  is equals to \ :java:ref:`Double.MAX_VALUE`\ , it means there is no maximum value.

getMinDimension
^^^^^^^^^^^^^^^

.. java:method:: public SlaMetricDimension getMinDimension()
   :outertype: SlaMetric

   Gets a \ :java:ref:`SlaMetricDimension`\  representing the minimum value expected for the metric. If the \ :java:ref:`SlaMetricDimension.getValue()`\  is a negative number, it means there is no minimum value.

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: SlaMetric

setDimensions
^^^^^^^^^^^^^

.. java:method:: public SlaMetric setDimensions(List<SlaMetricDimension> dimensions)
   :outertype: SlaMetric

setName
^^^^^^^

.. java:method:: public SlaMetric setName(String name)
   :outertype: SlaMetric

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: SlaMetric

