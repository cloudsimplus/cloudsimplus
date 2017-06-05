.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.vms Vm

CloudletToVmMappingHeuristic
============================

.. java:package:: org.cloudsimplus.heuristics
   :noindex:

.. java:type:: public interface CloudletToVmMappingHeuristic extends Heuristic<CloudletToVmMappingSolution>

   Provides the methods to be used for implementing a heuristic to get a sub-optimal solution for mapping Cloudlets to Vm's.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  CloudletToVmMappingHeuristic NULL
   :outertype: CloudletToVmMappingHeuristic

   A property that implements the Null Object Design Pattern for \ :java:ref:`Heuristic`\  objects.

Methods
-------
getCloudletList
^^^^^^^^^^^^^^^

.. java:method::  List<Cloudlet> getCloudletList()
   :outertype: CloudletToVmMappingHeuristic

   :return: the list of cloudlets to be mapped to \ :java:ref:`available Vm's <getVmList()>`\ .

getVmList
^^^^^^^^^

.. java:method::  List<Vm> getVmList()
   :outertype: CloudletToVmMappingHeuristic

   :return: the list of available Vm's to host Cloudlets.

setCloudletList
^^^^^^^^^^^^^^^

.. java:method::  void setCloudletList(List<Cloudlet> cloudletList)
   :outertype: CloudletToVmMappingHeuristic

   Sets the list of Cloudlets to be mapped to \ :java:ref:`available Vm's <getVmList()>`\ .

   :param cloudletList: the list of Cloudlets to set

setVmList
^^^^^^^^^

.. java:method::  void setVmList(List<Vm> vmList)
   :outertype: CloudletToVmMappingHeuristic

   Sets the list of available VMs to host Cloudlets.

   :param vmList: the list of VMs to set

