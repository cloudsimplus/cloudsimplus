UtilizationModel
================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public interface UtilizationModel

   The UtilizationModel interface needs to be implemented in order to provide a fine-grained control over resource usage by a Cloudlet. It also implements the Null Object Design Pattern in order to start avoiding \ :java:ref:`NullPointerException`\  when using the \ :java:ref:`UtilizationModel.NULL`\  object instead of attributing \ ``null``\  to \ :java:ref:`UtilizationModel`\  variables.

   :author: Anton Beloglazov

Fields
------
NULL
^^^^

.. java:field::  UtilizationModel NULL
   :outertype: UtilizationModel

   A property that implements the Null Object Design Pattern for \ :java:ref:`UtilizationModel`\  objects using a Lambda Expression.

Methods
-------
getUtilization
^^^^^^^^^^^^^^

.. java:method::  double getUtilization(double time)
   :outertype: UtilizationModel

   Gets the utilization percentage of a given resource (in scale from [0 to 1])..

   :param time: the time to get the resource usage.
   :return: utilization percentage, from [0 to 1]

