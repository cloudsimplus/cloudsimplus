.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core SimEntity

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners DatacenterBrokerEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

.. java:import:: java.util Comparator

.. java:import:: java.util List

.. java:import:: java.util.function Function

.. java:import:: java.util.function Supplier

DatacenterBroker
================

.. java:package:: org.cloudbus.cloudsim.brokers
   :noindex:

.. java:type:: public interface DatacenterBroker extends SimEntity

   Represents a broker acting on behalf of a cloud customer. It hides VM management such as vm creation, submission of cloudlets to VMs and destruction of VMs.

   A broker implements the policies for selecting a VM to run a Cloudlet and a Datacenter to run the submitted VMs.

   :author: Rodrigo N. Calheiros, Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
DEFAULT_VM_DESTRUCTION_DELAY
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field::  double DEFAULT_VM_DESTRUCTION_DELAY
   :outertype: DatacenterBroker

   A default delay value to indicate that \ **no**\  VM should be immediately destroyed after it becoming idle.

   This is used as the default value returned by the \ :java:ref:`getVmDestructionDelayFunction()`\  if a \ :java:ref:`Function`\  is not set.

   **See also:** :java:ref:`.setVmDestructionDelayFunction(Function)`

LOGGER
^^^^^^

.. java:field::  Logger LOGGER
   :outertype: DatacenterBroker

NULL
^^^^

.. java:field::  DatacenterBroker NULL
   :outertype: DatacenterBroker

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`DatacenterBroker`\  objects.

Methods
-------
addOnVmsCreatedListener
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener)
   :outertype: DatacenterBroker

   Adds an \ :java:ref:`EventListener`\  that will be notified every time VMs in the waiting list are all created.

   Events are fired according to the following conditions:

   ..

   * if all VMs are submitted before the simulation start and all those VMs are created after starting, then the event will be fired just once, during all simulation execution, for every registered Listener;
   * if all VMs submitted at a given time cannot be created due to lack of suitable Hosts, the event will not be fired for that submission;
   * if new VMs are submitted during simulation execution, the event may be fired multiple times. For instance, consider new VMs are submitted during simulation execution at times 10 and 20. If for every submission time, all VMs could be created, then every Listener will be notified 2 times (one for VMs submitted at time 10 and other for those at time 20).

   :param listener: the Listener that will be notified

   **See also:** :java:ref:`.getVmWaitingList()`, :java:ref:`.addOneTimeOnVmsCreatedListener(EventListener)`

addOneTimeOnVmsCreatedListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  DatacenterBroker addOneTimeOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener)
   :outertype: DatacenterBroker

   Adds an \ :java:ref:`EventListener`\  that will be notified \ **just once**\  when VMs in the waiting list are all created. After the first notification, the Listener is removed from the registered Listeners and no further notifications will be sent to that specific Listener.

   Even if VMs were submitted at different simulation times and all of them are created successfully (which means notifications are expected at different times), this Listener will be notified just when the first list of VMs is created and no subsequent notifications will be sent when other List of VMs is created.

   For instance, consider new VMs are submitted during simulation execution at times 10 and 20. If for every submission time, all VMs could be created, then this specific Listener is expected to be notified 2 times (one for VMs submitted at time 10 and other for those at time 20). However, after VMs submitted at time 10 are all created, the Listener is notified and unregistered, so that it will get no next notifications.

   :param listener: the Listener that will be notified

   **See also:** :java:ref:`.getVmWaitingList()`, :java:ref:`.addOnVmsCreatedListener(EventListener)`

bindCloudletToVm
^^^^^^^^^^^^^^^^

.. java:method::  boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm)
   :outertype: DatacenterBroker

   Specifies that an already submitted cloudlet, which is in the \ :java:ref:`waiting list <getCloudletWaitingList()>`\ , must run in a specific virtual machine.

   :param cloudlet: the cloudlet to be bind to a given Vm
   :param vm: the vm to bind the Cloudlet to
   :return: true if the Cloudlet was found in the waiting list and was bind to the given Vm, false it the Cloudlet was not found in such a list (that may mean it wasn't submitted yet or was already created)

defaultVmMapper
^^^^^^^^^^^^^^^

.. java:method::  Vm defaultVmMapper(Cloudlet cloudlet)
   :outertype: DatacenterBroker

   Defines the default policy used to select a Vm to host a Cloudlet that is waiting to be created. It applies a Round-Robin policy to cyclically select the next Vm from the list of waiting VMs.

   :param cloudlet: the cloudlet that needs a VM to be placed into
   :return: the selected Vm for the cloudlet or \ :java:ref:`Vm.NULL`\  if no suitable VM was found

getCloudletCreatedList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Cloudlet> getCloudletCreatedList()
   :outertype: DatacenterBroker

   Gets a \ **read-only**\  list of cloudlets created inside some Vm.

   :return: the list of created Cloudlets

getCloudletFinishedList
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  <T extends Cloudlet> List<T> getCloudletFinishedList()
   :outertype: DatacenterBroker

   Gets a \ **copy**\  of the list of cloudlets that have finished executing, to avoid the original list to be changed.

   :param <T>: the class of Cloudlets inside the list
   :return: the list of finished cloudlets

getCloudletSubmittedList
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Cloudlet> getCloudletSubmittedList()
   :outertype: DatacenterBroker

getCloudletWaitingList
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  <T extends Cloudlet> List<T> getCloudletWaitingList()
   :outertype: DatacenterBroker

   Gets the list of cloudlets submitted to the broker that are waiting to be created inside some Vm yet.

   :param <T>: the class of Cloudlets inside the list
   :return: the cloudlet waiting list

getVmCreatedList
^^^^^^^^^^^^^^^^

.. java:method::  <T extends Vm> List<T> getVmCreatedList()
   :outertype: DatacenterBroker

   Gets the list of all VMs created so far, independently if they are running yet or were already destroyed. This can be used at the end of the simulation to know which VMs have executed.

   :param <T>: the class of VMs inside the list
   :return: the list of created VMs

   **See also:** :java:ref:`.getVmExecList()`

getVmDestructionDelayFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Function<Vm, Double> getVmDestructionDelayFunction()
   :outertype: DatacenterBroker

   Gets a \ :java:ref:`Function`\  which defines when an idle VM should be destroyed. The Function receives a \ :java:ref:`Vm`\  and returns the delay to wait (in seconds), after the VM becomes idle, to destroy it.

   **See also:** :java:ref:`.DEFAULT_VM_DESTRUCTION_DELAY`, :java:ref:`Vm.getIdleInterval()`

getVmExecList
^^^^^^^^^^^^^

.. java:method::  <T extends Vm> List<T> getVmExecList()
   :outertype: DatacenterBroker

   Gets the list of VMs in execution, if they are running Cloudlets or not. These VMs can receive new submitted Cloudlets.

   :param <T>: the class of VMs inside the list
   :return: the list of running VMs

   **See also:** :java:ref:`.getVmCreatedList()`

getVmWaitingList
^^^^^^^^^^^^^^^^

.. java:method::  <T extends Vm> List<T> getVmWaitingList()
   :outertype: DatacenterBroker

   Gets a List of VMs submitted to the broker that are waiting to be created inside some Datacenter yet.

   :param <T>: the class of VMs inside the list
   :return: the list of waiting VMs

getWaitingVm
^^^^^^^^^^^^

.. java:method::  Vm getWaitingVm(int index)
   :outertype: DatacenterBroker

isThereWaitingCloudlets
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  boolean isThereWaitingCloudlets()
   :outertype: DatacenterBroker

   Indicates if there are more cloudlets waiting to be executed yet.

   :return: true if there are waiting cloudlets, false otherwise

setCloudletComparator
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setCloudletComparator(Comparator<Cloudlet> comparator)
   :outertype: DatacenterBroker

   Sets a \ :java:ref:`Comparator`\  that will be used to sort every list of submitted Cloudlets before mapping each Cloudlet to a Vm. After sorting, the Cloudlet mapping will follow the order of the sorted Cloudlet list.

   :param comparator: the Cloudlet Comparator to set

setDatacenterSupplier
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setDatacenterSupplier(Supplier<Datacenter> datacenterSupplier)
   :outertype: DatacenterBroker

   Sets the \ :java:ref:`Supplier`\  that selects and returns a Datacenter to place submitted VMs.

   The supplier defines the policy to select a Datacenter to host a VM that is waiting to be created.

   :param datacenterSupplier: the datacenterSupplier to set

setFallbackDatacenterSupplier
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setFallbackDatacenterSupplier(Supplier<Datacenter> fallbackDatacenterSupplier)
   :outertype: DatacenterBroker

   Sets the \ :java:ref:`Supplier`\  that selects and returns a fallback Datacenter to place submitted VMs when the Datacenter selected by the \ :java:ref:`Datacenter Supplier <setDatacenterSupplier(java.util.function.Supplier)>`\  failed to create all requested VMs.

   The supplier defines the policy to select a Datacenter to host a VM when all VM creation requests were received but not all VMs could be created. In this case, a different Datacenter has to be selected to request the creation of the remaining VMs in the waiting list.

   :param fallbackDatacenterSupplier: the fallbackDatacenterSupplier to set

setVmComparator
^^^^^^^^^^^^^^^

.. java:method::  void setVmComparator(Comparator<Vm> comparator)
   :outertype: DatacenterBroker

   Sets a \ :java:ref:`Comparator`\  that will be used to sort every list of submitted VMs before requesting the creation of such VMs in some Datacenter. After sorting, the VM creation requests will be sent in the order of the sorted VM list.

   :param comparator: the VM Comparator to set

setVmDestructionDelayFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  DatacenterBroker setVmDestructionDelayFunction(Function<Vm, Double> function)
   :outertype: DatacenterBroker

   Sets a \ :java:ref:`Function`\  to define when an idle VM should be destroyed. The Function receives a \ :java:ref:`Vm`\  and returns the delay to wait (in seconds), after the VM becomes idle, to destroy it.

   :param function: the \ :java:ref:`Function`\  to set (if null is given, it sets the default Function)

   **See also:** :java:ref:`.DEFAULT_VM_DESTRUCTION_DELAY`, :java:ref:`Vm.getIdleInterval()`

setVmMapper
^^^^^^^^^^^

.. java:method::  void setVmMapper(Function<Cloudlet, Vm> vmMapper)
   :outertype: DatacenterBroker

   Sets a \ :java:ref:`Function`\  that maps a given Cloudlet to a Vm. It defines the policy used to select a Vm to host a Cloudlet that is waiting to be created.

   :param vmMapper: the Vm mapper Function to set. Such a Function must receive a Cloudlet and return the Vm where it will be placed into. If the Function is unable to find a VM for a Cloudlet, it should return \ :java:ref:`Vm.NULL`\ .

submitCloudlet
^^^^^^^^^^^^^^

.. java:method::  void submitCloudlet(Cloudlet cloudlet)
   :outertype: DatacenterBroker

   Submits a single \ :java:ref:`Cloudlet`\  to the broker.

   :param cloudlet: the Cloudlet to be submitted

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method::  void submitCloudletList(List<? extends Cloudlet> list)
   :outertype: DatacenterBroker

   Sends a list of cloudlets to the broker so that it requests their creation inside some VM, following the submission delay specified in each cloudlet (if any). All cloudlets will be added to the \ :java:ref:`getCloudletWaitingList()`\ .

   :param list: the list of Cloudlets to request the creation

   **See also:** :java:ref:`.submitCloudletList(java.util.List,double)`

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method::  void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay)
   :outertype: DatacenterBroker

   Sends a list of cloudlets to the broker so that it requests their creation inside some VM just after a given delay. Just the Cloudlets that don't have a delay already assigned will have its submission delay changed. All cloudlets will be added to the \ :java:ref:`getCloudletWaitingList()`\ , setting their submission delay to the specified value.

   :param list: the list of Cloudlets to request the creation
   :param submissionDelay: the delay the broker has to include when requesting the creation of Cloudlets

   **See also:** :java:ref:`.submitCloudletList(java.util.List)`, :java:ref:`Cloudlet.getSubmissionDelay()`

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method::  void submitCloudletList(List<? extends Cloudlet> list, Vm vm)
   :outertype: DatacenterBroker

   Sends a list of cloudlets to the broker so that it requests their creation inside a specific VM, following the submission delay specified in each cloudlet (if any). All cloudlets will be added to the \ :java:ref:`getCloudletWaitingList()`\ .

   :param list: the list of Cloudlets to request the creation
   :param vm: the VM to which all Cloudlets will be bound to

   **See also:** :java:ref:`.submitCloudletList(java.util.List,double)`

submitCloudletList
^^^^^^^^^^^^^^^^^^

.. java:method::  void submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay)
   :outertype: DatacenterBroker

   Sends a list of cloudlets to the broker so that it requests their creation inside a specific VM just after a given delay. Just the Cloudlets that don't have a delay already assigned will have its submission delay changed. All cloudlets will be added to the \ :java:ref:`getCloudletWaitingList()`\ , setting their submission delay to the specified value.

   :param list: the list of Cloudlets to request the creation
   :param vm: the VM to which all Cloudlets will be bound to
   :param submissionDelay: the delay the broker has to include when requesting the creation of Cloudlets

   **See also:** :java:ref:`.submitCloudletList(java.util.List)`, :java:ref:`Cloudlet.getSubmissionDelay()`

submitVm
^^^^^^^^

.. java:method::  void submitVm(Vm vm)
   :outertype: DatacenterBroker

   Submits a single \ :java:ref:`Vm`\  to the broker.

   :param vm: the Vm to be submitted

submitVmList
^^^^^^^^^^^^

.. java:method::  void submitVmList(List<? extends Vm> list)
   :outertype: DatacenterBroker

   Sends to the broker a list with VMs that their creation inside a Host will be requested to some \ :java:ref:`Datacenter`\ . The Datacenter that will be chosen to place a VM is determined by the \ :java:ref:`setDatacenterSupplier(Supplier)`\ .

   :param list: the list of VMs to request the creation

submitVmList
^^^^^^^^^^^^

.. java:method::  void submitVmList(List<? extends Vm> list, double submissionDelay)
   :outertype: DatacenterBroker

   Sends a list of VMs for the broker so that their creation inside some Host will be requested just after a given delay. Just the VMs that don't have a delay already assigned will have its submission delay changed. All VMs will be added to the \ :java:ref:`getVmWaitingList()`\ , setting their submission delay to the specified value.

   :param list: the list of VMs to request the creation
   :param submissionDelay: the delay the broker has to include when requesting the creation of VMs

   **See also:** :java:ref:`.submitVmList(java.util.List)`, :java:ref:`Vm.getSubmissionDelay()`

