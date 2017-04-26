org.cloudbus.cloudsim.brokers
=============================

Provides \ :java:ref:`org.cloudbus.cloudsim.brokers.DatacenterBroker`\  classes that act on behalf of a cloud customer, attending his/her requests for creation and destruction of \ :java:ref:`Cloudlets <org.cloudbus.cloudsim.cloudlets.Cloudlet>`\  and \ :java:ref:`VMs <org.cloudbus.cloudsim.vms.Vm>`\ , assigning such Cloudlets to specific VMs. These brokers can implement decision making algorithms to prioritize submission of Cloudlets to the cloud, to define how a VM is selected to run a given Cloudlets, etc.

The most basic implementation is the \ :java:ref:`org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple`\  that uses a Round-robin algorithm to select a VM from a list to place a submitted Cloudlet, which is called a Cloudlet to VM mapping. Other class such as the \ :java:ref:`org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic`\  allows setting a \ :java:ref:`org.cloudsimplus.heuristics.Heuristic`\  to find an sub-optimal mapping.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.brokers

.. toctree::
   :maxdepth: 1

   DatacenterBroker
   DatacenterBrokerAbstract
   DatacenterBrokerHeuristic
   DatacenterBrokerNull
   DatacenterBrokerSimple

