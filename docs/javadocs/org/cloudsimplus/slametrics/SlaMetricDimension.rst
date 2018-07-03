SlaMetricDimension
==================

.. java:package:: org.cloudsimplus.slametrics
   :noindex:

.. java:type:: public final class SlaMetricDimension

   Represents a value for a specific metric of a SLA contract, following the format defined by the \ `AWS CloudWatch <http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html>`_\ .

   Each dimension contains the name of the metric, the minimum and maximum acceptable values, and the metric unit. Each metric may have multiple dimensions.

   For more details, check \ `Raysa Oliveira's Master Thesis (only in Portuguese) <http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf>`_\ .

   :author: raysaoliveira

Constructors
------------
SlaMetricDimension
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SlaMetricDimension()
   :outertype: SlaMetricDimension

SlaMetricDimension
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SlaMetricDimension(double value)
   :outertype: SlaMetricDimension

Methods
-------
getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: SlaMetricDimension

getUnit
^^^^^^^

.. java:method:: public String getUnit()
   :outertype: SlaMetricDimension

   Gets the unit of the dimension, if "Percent" or "Absolute". When the unit is "Percent", the values are defined in scale from 0 to 100%, but they are stored in this class in scale from 0 to 1, because everywhere percentage values are defined in this scale.

getValue
^^^^^^^^

.. java:method:: public double getValue()
   :outertype: SlaMetricDimension

   Gets the value of the dimension, in absolute or percentage, according to the \ :java:ref:`getUnit()`\ .

   When the unit is "Percent", the values are defined in scale from 0 to 100%, but they are stored in this class in scale from 0 to 1, because everywhere percentage values are defined in this scale.

isMaxValue
^^^^^^^^^^

.. java:method:: public boolean isMaxValue()
   :outertype: SlaMetricDimension

isMinValue
^^^^^^^^^^

.. java:method:: public boolean isMinValue()
   :outertype: SlaMetricDimension

isPercent
^^^^^^^^^

.. java:method:: public boolean isPercent()
   :outertype: SlaMetricDimension

   Checks if the unit is defined in percentage values.

setName
^^^^^^^

.. java:method:: public SlaMetricDimension setName(String name)
   :outertype: SlaMetricDimension

setUnit
^^^^^^^

.. java:method:: public SlaMetricDimension setUnit(String unit)
   :outertype: SlaMetricDimension

setValue
^^^^^^^^

.. java:method:: public SlaMetricDimension setValue(double value)
   :outertype: SlaMetricDimension

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: SlaMetricDimension

