org.cloudbus.cloudsim.cloudlets
===============================

Provides \ :java:ref:`org.cloudbus.cloudsim.cloudlets.Cloudlet`\  implementations, that represent an application that will run inside a \ :java:ref:`org.cloudbus.cloudsim.vms.Vm`\ . Each Cloudlet is abstractly defined in terms of its characteristics, such as the number of Million Instructions (MI) to execute, the number of required \ :java:ref:`org.cloudbus.cloudsim.resources.Pe`\  and a \ :java:ref:`org.cloudbus.cloudsim.utilizationmodels.UtilizationModel`\  for CPU, RAM and bandwidth.

Each utilization model defines how a given resource will be used by the Cloudlet along the time. Some basic utilization models implementations are provided, such as the \ :java:ref:`org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull`\ , which indicates that a given available resource will be used 100% all the time.

Specific Cloudlet implementations can be, for instance, network-aware, enabling the simulation of network communication. For more information see \ :java:ref:`org.cloudbus.cloudsim.datacenters`\  package documentation.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.cloudlets

.. toctree::
   :maxdepth: 1

   Cloudlet
   Cloudlet-Status
   CloudletAbstract
   CloudletDatacenterExecution
   CloudletExecution
   CloudletNull
   CloudletSimple

