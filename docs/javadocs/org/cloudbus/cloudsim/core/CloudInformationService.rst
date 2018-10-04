.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util Collection

.. java:import:: java.util Set

.. java:import:: java.util TreeSet

CloudInformationService
=======================

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public class CloudInformationService extends CloudSimEntity

   A Cloud Information Service (CIS) is an entity that provides cloud resource registration, indexing and discovery services. The Cloud hostList tell their readiness to process Cloudlets by registering themselves with this entity. Other entities such as the resource broker can contact this class for resource discovery service, which returns a list of registered resource IDs.

   In summary, it acts like a yellow page service. This class will be created by CloudSim upon initialisation of the simulation. Hence, do not need to worry about creating an object of this class.

   :author: Manzur Murshed, Rajkumar Buyya

Constructors
------------
CloudInformationService
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor::  CloudInformationService(CloudSim simulation)
   :outertype: CloudInformationService

   Instantiates a new CloudInformationService object.

   :param simulation: The CloudSim instance that represents the simulation the Entity is related to

Methods
-------
getDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method:: public Set<Datacenter> getDatacenterList()
   :outertype: CloudInformationService

   Gets the list of all registered Datacenters.

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(SimEvent evt)
   :outertype: CloudInformationService

shutdownEntity
^^^^^^^^^^^^^^

.. java:method:: @Override public void shutdownEntity()
   :outertype: CloudInformationService

startEntity
^^^^^^^^^^^

.. java:method:: @Override protected void startEntity()
   :outertype: CloudInformationService

   The method has no effect at the current class.

