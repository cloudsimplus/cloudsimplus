org.cloudbus.cloudsim.resources
===============================

Provides classes that represent different physical and logical \ :java:ref:`org.cloudbus.cloudsim.resources.Resource`\  used by simulation objects such as Hosts and VMs.

There are different interfaces that enable the existence of resources with different features such as if the capacity of the resource can be changed after defined, if the resource can be managed (meaning that some amount of it can be allocated or freed in runtime), etc.

The most basic resources are \ :java:ref:`org.cloudbus.cloudsim.resources.HarddriveStorage`\ , \ :java:ref:`org.cloudbus.cloudsim.resources.Ram`\ , \ :java:ref:`org.cloudbus.cloudsim.resources.Bandwidth`\ , \ :java:ref:`org.cloudbus.cloudsim.resources.Pe`\  and \ :java:ref:`org.cloudbus.cloudsim.resources.File`\ .

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudbus.cloudsim.resources

.. toctree::
   :maxdepth: 1

   Bandwidth
   DatacenterStorage
   File
   FileAttribute
   FileStorage
   HarddriveStorage
   Pe
   Pe-Status
   PeNull
   PeSimple
   Processor
   Ram
   Resource
   ResourceAbstract
   ResourceCapacity
   ResourceManageable
   ResourceManageableAbstract
   ResourceManageableNull
   ResourceNull
   Resourceful
   SanStorage
   Storage

