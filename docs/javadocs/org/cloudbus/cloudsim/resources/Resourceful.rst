.. java:import:: org.cloudbus.cloudsim.core Machine

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util List

Resourceful
===========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public interface Resourceful

   An interface to be implemented by a machine such as a \ :java:ref:`Host`\  or \ :java:ref:`Vm`\ , that provides a polymorphic way to access a given resource like \ :java:ref:`Ram`\ , \ :java:ref:`Bandwidth`\ , \ :java:ref:`Storage`\  or \ :java:ref:`Pe`\  from a List containing such different resources.

   :author: Manoel Campos da Silva Filho

Methods
-------
getResource
^^^^^^^^^^^

.. java:method::  ResourceManageable getResource(Class<? extends ResourceManageable> resourceClass)
   :outertype: Resourceful

   Gets a given \ :java:ref:`Machine`\  \ :java:ref:`Resource`\ , such as \ :java:ref:`Ram`\  or \ :java:ref:`Bandwidth`\ , from the List of machine resources.

   :param resourceClass: the class of resource to get
   :return: the \ :java:ref:`Resource`\  corresponding to the given class

getResources
^^^^^^^^^^^^

.. java:method::  List<ResourceManageable> getResources()
   :outertype: Resourceful

   Gets a \ **read-only**\  list of resources the machine has.

   :return: a read-only list of resources

   **See also:** :java:ref:`.getResource(Class)`

