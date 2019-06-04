.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util Optional

VmAllocationPolicyRandom
========================

.. java:package:: org.cloudbus.cloudsim.allocationpolicies
   :noindex:

.. java:type:: public class VmAllocationPolicyRandom extends VmAllocationPolicyAbstract implements VmAllocationPolicy

   A VM allocation policy which finds a random Host having suitable resources to place a given VM. This is a high time-efficient policy with a best-case complexity O(1) and a worst-case complexity O(N), where N is the number of Hosts.

   \ **NOTES:**\

   ..

   * This policy doesn't perform optimization of VM allocation by means of VM migration.
   * It has a low computational complexity (high time-efficient) but may return and inactive Host that will be activated, while there may be active Hosts suitable for the VM.
   * Despite the low computational complexity, such a policy may increase the number of active Hosts, that increases power consumption.

   :author: Manoel Campos da Silva Filho

Constructors
------------
VmAllocationPolicyRandom
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public VmAllocationPolicyRandom(ContinuousDistribution random)
   :outertype: VmAllocationPolicyRandom

   Instantiates a VmAllocationPolicyRandom.

   :param random: a Pseudo-Random Number Generator (PRNG) used to select a Host. The PRNG must return values between 0 and 1.

Methods
-------
defaultFindHostForVm
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected Optional<Host> defaultFindHostForVm(Vm vm)
   :outertype: VmAllocationPolicyRandom

