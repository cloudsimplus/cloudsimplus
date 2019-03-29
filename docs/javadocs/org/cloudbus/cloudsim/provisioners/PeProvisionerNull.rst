.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.vms Vm

PeProvisionerNull
=================

.. java:package:: org.cloudbus.cloudsim.provisioners
   :noindex:

.. java:type:: final class PeProvisionerNull extends ResourceProvisionerNull implements PeProvisioner

   A class that implements the Null Object Design Pattern for \ :java:ref:`PeProvisioner`\  class.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`PeProvisioner.NULL`

Methods
-------
allocateResourceForVm
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean allocateResourceForVm(Vm vm, double newTotalVmResource)
   :outertype: PeProvisionerNull

getUtilization
^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilization()
   :outertype: PeProvisionerNull

setPe
^^^^^

.. java:method:: @Override public void setPe(Pe pe)
   :outertype: PeProvisionerNull

