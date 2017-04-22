.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHost

.. java:import:: org.cloudbus.cloudsim.hosts.power PowerHostUtilizationHistory

PowerVmAllocationPolicyMigrationDynamicUpperThreshold
=====================================================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies.power
   :noindex:

.. java:type:: public interface PowerVmAllocationPolicyMigrationDynamicUpperThreshold extends PowerVmAllocationPolicyMigration

   An interface to be implemented by Power-aware VM allocation policies that use a dynamic over utilization threshold computed using some statistical method such as Median absolute deviation (MAD), InterQuartileRange (IRQ), Local Regression, etc, depending on the implementing class.

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Methods
-------
computeHostUtilizationMeasure
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double computeHostUtilizationMeasure(PowerHostUtilizationHistory host) throws IllegalArgumentException
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThreshold

   Computes the measure used to generate the dynamic host over utilization threshold using some statistical method (such as the Median absolute deviation - MAD, InterQuartileRange - IRQ, Local Regression, etc), depending on the implementing class. The method uses Host utilization history to compute such a metric.

   :param host: the host to get the current utilization
   :throws IllegalArgumentException: when the measure could not be computed (for instance, because the Host doesn't have enought history to use)

   **See also:** :java:ref:`.getOverUtilizationThreshold(PowerHost)`

getFallbackVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  PowerVmAllocationPolicyMigration getFallbackVmAllocationPolicy()
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThreshold

   Gets the fallback VM allocation policy to be used when the over utilization host detection doesn't have data to be computed.

   :return: the fallback vm allocation policy

getSafetyParameter
^^^^^^^^^^^^^^^^^^

.. java:method::  double getSafetyParameter()
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThreshold

   Gets the safety parameter for the over utilization threshold in percentage, at scale from 0 to 1. For instance, a value 1 means 100% while 1.5 means 150%. It is a tuning parameter used by the allocation policy to define when a host is overloaded. The overload detection is based on a dynamic defined host utilization threshold.

   Such a threshold is computed based on the host's usage history using different statistical methods (such as Median absolute deviation - MAD, that is similar to the Standard Deviation) depending on the implementing class, as defined by the method \ :java:ref:`computeHostUtilizationMeasure(PowerHostUtilizationHistory)`\ .

   This safety parameter is used to increase or decrease the utilization threshold. As the safety parameter increases, the threshold decreases, what may lead to less SLA violations. So, as higher is that parameter, safer the algorithm will be when defining a host as overloaded. A value equal to 0 indicates that the safery parameter doesn't affect
   the computed CPU utilization threshold.

   Let's take an example of a class that uses the MAD to compute the over utilization threshold. Considering a host's resource usage mean of 0.6 (60%) and a MAD of 0.2, meaning the usage may vary from 0.4 to 0.8. Now take a safety parameter of 0.5 (50%). To compute the usage threshold, the MAD is increased by 50%, being equals to 0.3. Finally, the threshold will be 1 - 0.3 = 0.7. Thus, only when the host utilization threshold exceeds 70%, the host is considered overloaded.

   Here, safer doesn't mean a more accurate overload detection but that the algorithm will use a lower host utilization threshold that may lead to lower SLA violations but higher resource wastage. Thus this parameter has to be tuned in order to
   trade-off between SLA violation and resource wastage.

setFallbackVmAllocationPolicy
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setFallbackVmAllocationPolicy(PowerVmAllocationPolicyMigration fallbackPolicy)
   :outertype: PowerVmAllocationPolicyMigrationDynamicUpperThreshold

   Sets the fallback VM allocation policy to be used when the over utilization host detection doesn't have data to be computed.

   :param fallbackPolicy: the new fallback vm allocation policy

