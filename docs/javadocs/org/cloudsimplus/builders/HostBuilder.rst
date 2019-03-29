.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.hosts HostSimple

.. java:import:: org.cloudbus.cloudsim.provisioners ResourceProvisionerSimple

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.vm VmScheduler

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners HostUpdatesVmsProcessingEventInfo

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.function Function

.. java:import:: java.util.function Supplier

HostBuilder
===========

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class HostBuilder implements Builder

   A Builder class to create \ :java:ref:`Host`\  objects using the default configurations defined in \ :java:ref:`Host`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`HostSimple.setDefaultRamCapacity(long)`, :java:ref:`HostSimple.setDefaultBwCapacity(long)`, :java:ref:`HostSimple.setDefaultStorageCapacity(long)`

Constructors
------------
HostBuilder
^^^^^^^^^^^

.. java:constructor:: public HostBuilder()
   :outertype: HostBuilder

Methods
-------
create
^^^^^^

.. java:method:: public HostBuilder create()
   :outertype: HostBuilder

   Creates a single Host and stores it internally.

   **See also:** :java:ref:`.getHosts()`

create
^^^^^^

.. java:method:: public HostBuilder create(int amount)
   :outertype: HostBuilder

   Creates a list of Hosts and stores it internally.

   **See also:** :java:ref:`.getHosts()`

getHosts
^^^^^^^^

.. java:method:: public List<Host> getHosts()
   :outertype: HostBuilder

   Gets the list of all created Hosts.

   **See also:** :java:ref:`.create()`, :java:ref:`.create(int)`

getMips
^^^^^^^

.. java:method:: public double getMips()
   :outertype: HostBuilder

getPes
^^^^^^

.. java:method:: public int getPes()
   :outertype: HostBuilder

setHostCreationFunction
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setHostCreationFunction(Function<List<Pe>, Host> hostCreationFunction)
   :outertype: HostBuilder

   Sets a \ :java:ref:`Function`\  used to create Hosts. It must receive a list of \ :java:ref:`Pe`\  for the Host it will create.

   :param hostCreationFunction:

setMips
^^^^^^^

.. java:method:: public HostBuilder setMips(double defaultMIPS)
   :outertype: HostBuilder

setOnUpdateVmsProcessingListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public HostBuilder setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener)
   :outertype: HostBuilder

setPes
^^^^^^

.. java:method:: public HostBuilder setPes(int defaultPEs)
   :outertype: HostBuilder

setVmSchedulerSupplier
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public HostBuilder setVmSchedulerSupplier(Supplier<VmScheduler> vmSchedulerSupplier)
   :outertype: HostBuilder

