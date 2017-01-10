org.cloudsimplus.builders
=========================

Provides \ :java:ref:`org.cloudsimplus.builders.Builder`\  classes that implement the \ `Builder Design Pattern <https://en.wikipedia.org/wiki/Builder_pattern>`_\  to allow instantiating multiple simulation objects more easily.

Since that creating and setting up some simulation objects such as a \ :java:ref:`org.cloudbus.cloudsim.datacenters.Datacenter`\  requires a considerable amount of code, that usually becomes duplicated along different simulations, the builder classes work as object factories that make it easier to create multiple simulation objects with the same configuration.

The builders allow to set the parameters for creating a given object such as a Host, and then, after all parameters are set, a single class can create as many objects with the same configuration as desired.

:author: Manoel Campos da Silva Filho

.. java:package:: org.cloudsimplus.builders

.. toctree::
   :maxdepth: 1

   BrokerBuilder
   BrokerBuilderDecorator
   BrokerBuilderInterface
   Builder
   CloudletBuilder
   DatacenterBuilder
   HostBuilder
   PeBuilder
   SimulationScenarioBuilder
   VmBuilder

