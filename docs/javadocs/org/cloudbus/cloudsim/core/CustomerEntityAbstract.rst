.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

CustomerEntityAbstract
======================

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public abstract class CustomerEntityAbstract implements CustomerEntity

   A base class for \ :java:ref:`CustomerEntity`\  implementations.

   :author: Manoel Campos da Silva Filho

Constructors
------------
CustomerEntityAbstract
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected CustomerEntityAbstract()
   :outertype: CustomerEntityAbstract

Methods
-------
getBroker
^^^^^^^^^

.. java:method:: @Override public DatacenterBroker getBroker()
   :outertype: CustomerEntityAbstract

getId
^^^^^

.. java:method:: @Override public long getId()
   :outertype: CustomerEntityAbstract

getLastTriedDatacenter
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Datacenter getLastTriedDatacenter()
   :outertype: CustomerEntityAbstract

   Gets the last Datacenter where VM was tried to be created.

getSimulation
^^^^^^^^^^^^^

.. java:method:: @Override public Simulation getSimulation()
   :outertype: CustomerEntityAbstract

getUid
^^^^^^

.. java:method:: @Override public String getUid()
   :outertype: CustomerEntityAbstract

hashCode
^^^^^^^^

.. java:method:: @Override public int hashCode()
   :outertype: CustomerEntityAbstract

setBroker
^^^^^^^^^

.. java:method:: @Override public final void setBroker(DatacenterBroker broker)
   :outertype: CustomerEntityAbstract

setId
^^^^^

.. java:method:: @Override public final void setId(long id)
   :outertype: CustomerEntityAbstract

setLastTriedDatacenter
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setLastTriedDatacenter(Datacenter lastTriedDatacenter)
   :outertype: CustomerEntityAbstract

   Sets the last Datacenter where VM was tried to be created.

   :param lastTriedDatacenter:

