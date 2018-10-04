.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util.stream Collectors

CloudletToVmMappingSolution
===========================

.. java:package:: org.cloudsimplus.heuristics
   :noindex:

.. java:type:: public class CloudletToVmMappingSolution implements HeuristicSolution<Map<Cloudlet, Vm>>

   A possible solution for mapping a set of Cloudlets to a set of Vm's. It represents a solution generated using a \ :java:ref:`Heuristic`\  implementation.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Heuristic`

Fields
------
MIN_DIFF
^^^^^^^^

.. java:field:: public static final double MIN_DIFF
   :outertype: CloudletToVmMappingSolution

   When two double values are subtracted to check if they are equal zero, there may be some precision issues. This value is used to check the absolute difference between the two values to avoid that solutions with little decimal difference be considered different one of the other.

Constructors
------------
CloudletToVmMappingSolution
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletToVmMappingSolution(Heuristic heuristic)
   :outertype: CloudletToVmMappingSolution

   Creates a new solution for mapping a set of cloudlets to VMs using a given heuristic implementation.

   :param heuristic: the heuristic implementation used to find the solution being created.

CloudletToVmMappingSolution
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletToVmMappingSolution(CloudletToVmMappingSolution solution)
   :outertype: CloudletToVmMappingSolution

   Clones a given solution.

   :param solution: the solution to be cloned

Methods
-------
bindCloudletToVm
^^^^^^^^^^^^^^^^

.. java:method:: public void bindCloudletToVm(Cloudlet cloudlet, Vm vm)
   :outertype: CloudletToVmMappingSolution

   Binds a cloudlet to be executed by a given Vm.

   :param cloudlet: the cloudlet to be added to a Vm
   :param vm: the Vm to assign a cloudlet to

compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(HeuristicSolution solution)
   :outertype: CloudletToVmMappingSolution

   Compares this solution with another given one, based on the solution cost. The current object is considered to be: equal to the given object if they have the same cost; greater than the given object if it has a lower cost; lower than the given object if it has a higher cost;

   :param solution: the solution to compare this instance to
   :return: {@inheritDoc}

getCost
^^^^^^^

.. java:method:: @Override public double getCost()
   :outertype: CloudletToVmMappingSolution

   {@inheritDoc} It computes the cost of the entire mapping between Vm's and Cloudlets.

   :return: {@inheritDoc}

getCost
^^^^^^^

.. java:method:: public double getCost(boolean forceRecompute)
   :outertype: CloudletToVmMappingSolution

   It computes the costs of the entire mapping between Vm's and cloudlets.

   :param forceRecompute: indicate if the cost has to be recomputed anyway
   :return: the cost of the entire mapping between Vm's and cloudlets

   **See also:** :java:ref:`.getCost()`

getHeuristic
^^^^^^^^^^^^

.. java:method:: @Override public Heuristic<HeuristicSolution<Map<Cloudlet, Vm>>> getHeuristic()
   :outertype: CloudletToVmMappingSolution

getRandomMapEntries
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Map.Entry<Cloudlet, Vm>> getRandomMapEntries()
   :outertype: CloudletToVmMappingSolution

   Try to get 2 randomly selected entries from the \ :java:ref:`cloudletVmMap`\ .

   :return: a List with 2 entries from the \ :java:ref:`cloudletVmMap`\  if the map has at least 2 entries, an unitary List if the map has only one entry, or an empty List if there is no entry.

   **See also:** :java:ref:`.swapVmsOfTwoMapEntries(List)`

getResult
^^^^^^^^^

.. java:method:: @Override public Map<Cloudlet, Vm> getResult()
   :outertype: CloudletToVmMappingSolution

   :return: the actual solution, providing the mapping between Cloudlets and Vm's.

getVmCost
^^^^^^^^^

.. java:method:: public double getVmCost(Map.Entry<Vm, List<Map.Entry<Cloudlet, Vm>>> entry)
   :outertype: CloudletToVmMappingSolution

   Computes the cost of all Cloudlets hosted by a given Vm. The cost is based on the number of PEs from the VM that will be idle or overloaded.

   :param entry: a Map Entry where the key is a VM hosting some Cloudlets and the value is the Cloudlets hosted in this VM.
   :return: the VM cost to host the Cloudlets

getVmCost
^^^^^^^^^

.. java:method:: public double getVmCost(Vm vm, List<Cloudlet> cloudlets)
   :outertype: CloudletToVmMappingSolution

   Computes the cost of all Cloudlets hosted by a given Vm. The cost is based on the number of PEs from the VM that will be idle or overloaded.

   :param vm: the VM to compute the cost to host some Cloudlets
   :param cloudlets: the list of Cloudlets to be hosted by the VM in order to compute the cost
   :return: the VM cost to host the Cloudlets

swapVmsOfTwoMapEntries
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected final boolean swapVmsOfTwoMapEntries(List<Map.Entry<Cloudlet, Vm>> entries)
   :outertype: CloudletToVmMappingSolution

   Swap the Vm's of 2 randomly selected cloudlets in the \ :java:ref:`cloudletVmMap`\  in order to provide a neighbor solution. The method change the given Map entries, moving the cloudlet of the first entry to the Vm of the second entry and vice-versa.

   :param entries: a List of 2 entries containing Cloudlets to swap their VMs. If the entries don't have 2 elements, the method will return without performing any change in the entries.
   :return: true if the VMs of the Cloudlets where swapped, false otherwise

swapVmsOfTwoRandomSelectedMapEntries
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean swapVmsOfTwoRandomSelectedMapEntries()
   :outertype: CloudletToVmMappingSolution

   Swap the Vm's of 2 randomly selected cloudlets in the \ :java:ref:`cloudletVmMap`\  in order to provide a neighbor solution. The method change the given Map entries, moving the cloudlet of the first entry to the Vm of the second entry and vice-versa.

   :return: true if the Cloudlet's VMs where swapped, false otherwise

   **See also:** :java:ref:`.swapVmsOfTwoMapEntries(List)`

