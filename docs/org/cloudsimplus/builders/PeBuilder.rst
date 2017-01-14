.. java:import:: java.lang.reflect Constructor

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources PeSimple

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisionerSimple

PeBuilder
=========

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class PeBuilder extends Builder

   A Builder class to create \ :java:ref:`PeSimple`\  objects.

   :author: Manoel Campos da Silva Filho

Methods
-------
create
^^^^^^

.. java:method:: public List<Pe> create(double amount, double mipsOfEachPe)
   :outertype: PeBuilder

getProvisionerClass
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Class<? extends PeProvisioner> getProvisionerClass()
   :outertype: PeBuilder

setProvisioner
^^^^^^^^^^^^^^

.. java:method:: public PeBuilder setProvisioner(Class<? extends PeProvisioner> defaultProvisioner)
   :outertype: PeBuilder

