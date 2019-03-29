.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisionerSimple

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources PeSimple

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.function Function

PeBuilder
=========

.. java:package:: org.cloudsimplus.builders
   :noindex:

.. java:type:: public class PeBuilder implements Builder

   A Builder class to create \ :java:ref:`Pe`\  objects.

   :author: Manoel Campos da Silva Filho

Constructors
------------
PeBuilder
^^^^^^^^^

.. java:constructor:: public PeBuilder()
   :outertype: PeBuilder

Methods
-------
create
^^^^^^

.. java:method:: public List<Pe> create(int amount, double peMips)
   :outertype: PeBuilder

setPeSupplier
^^^^^^^^^^^^^

.. java:method:: public void setPeSupplier(Function<Double, Pe> peSupplier)
   :outertype: PeBuilder

   Sets a \ :java:ref:`Function`\  that is accountable to create \ :java:ref:`Pe`\  by this builder. The \ :java:ref:`Function`\  receives the MIPS for each PE.

   :param peSupplier:

