UtilizationModelFull
====================

.. java:package:: org.cloudbus.cloudsim.utilizationmodels
   :noindex:

.. java:type:: public class UtilizationModelFull extends UtilizationModelAbstract

   A \ :java:ref:`UtilizationModel`\  that according to which, a Cloudlet always utilizes a given allocated resource from its Vm at 100%, all the time.

   :author: Anton Beloglazov

Methods
-------
getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization(double time)
   :outertype: UtilizationModelFull

   Gets the utilization percentage (in scale from [0 to 1]) of resource at a given simulation time.

   :param time: the time to get the resource usage.
   :return: Always return 1 (100% of utilization), independent of the time.

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization()
   :outertype: UtilizationModelFull

   Gets the utilization percentage (in scale from [0 to 1]) of resource at the current simulation time.

   :return: Always return 1 (100% of utilization), independent of the time.

