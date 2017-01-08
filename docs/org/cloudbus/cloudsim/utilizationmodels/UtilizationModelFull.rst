UtilizationModelFull
====================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public class UtilizationModelFull implements UtilizationModel

   The UtilizationModelFull class is a simple model, according to which a Cloudlet always utilizes a given allocated resource at 100%, all the time.

   :author: Anton Beloglazov

Methods
-------
getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization(double time)
   :outertype: UtilizationModelFull

   Gets the utilization percentage of a given resource in relation to the total capacity of that resource allocated to the cloudlet.

   :param time: the time to get the resource usage, that isn't considered for this UtilizationModel.
   :return: Always return 1 (100% of utilization), independent of the time.

