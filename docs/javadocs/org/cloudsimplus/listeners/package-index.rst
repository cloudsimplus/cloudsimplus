org.cloudsimplus.listeners
==========================

Provides \ :java:ref:`org.cloudsimplus.listeners.EventListener`\  implementations to enable event notifications during simulation execution.

These notifications are related to changes in the state of simulation entities. The listeners enable, for instance, notifying when:

..

* a Host updates the processing of its VMs, it is allocated to a Vm or it is deallocated from a Vm;
* a Vm has its processing updated or fails to be placed at a Host due to lack of resources;
* a Cloudlet has its processing updated, it finishes its execution inside a Vm;
* a simulation processes any kind of event.

These listeners were implemented using Java 8 functional interfaces, enabling the use of \ `Lambda Expressions <http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html>`_\  that allow a function reference to be passed as parameter to another function. Such a reference will be used to automatically call the function every time the listened event is fired. Researchers developing using just Java 7 features can also use these listeners in the old-way by passing an anonymous class to them.

Listeners allow developers to perform specific tasks when different events happen and can be largely used for monitoring purposes, metrics collection and dynamic creation of objects, such as VMs and Cloudlets, at runtime.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudsimplus.listeners

.. toctree::
   :maxdepth: 1

   CloudletEventInfo
   CloudletVmEventInfo
   DatacenterBrokerEventInfo
   DatacenterEventInfo
   EventInfo
   EventListener
   HostEventInfo
   HostUpdatesVmsProcessingEventInfo
   VmDatacenterEventInfo
   VmEventInfo
   VmHostEventInfo

