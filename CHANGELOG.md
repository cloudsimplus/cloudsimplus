# Change Log

Lists the main changes in the project.

## [Current Development Version]
- xxx

## [2.0.0] - 2018-04-20

- PowerVm class was removed and its methods were moved to a new VmUtilizationHistory class.
  The Vm now has an attribute VmUtilizationHistory that enables collecting CPU utilization
  data. The VmUtilizationHistory.enabled is set by default to false.
  This way, the user have to manually enable the history to start collecting utilization
  data. It was disabled by default to reduce memory usage.

- PowerHostUtilizationHistory class was removed and its single method getUtilizationHistory
  was moved to the PowerHost. Since the method gets the history from VMs, the host
  doesn't store any data. The VM utilization history must be enabled to allow
  getting such data (as described above).

- PowerHost class removed and its methods moved to Host.
  A PowerSupply interface was introduced to group power
  consumption data (including a PowerModel).
  This way, any Host can extract power consumption data.
  It's just required a PowerModel to be set in the
  PowerSupply.

- Removes PowerVmAllocationPolicySimple because it was doing nothing
  than other policies weren't doing.

## [1.2.3] - 2017-06-05

### Added / Changed
- Renamed the `DatacenterBroker` `getVmsCreatedList()` method to `getVmCreatedList()`
  and added a `getVmExecList()` one ([#100](https://github.com/manoelcampos/cloudsim-plus/issues/100)).
  Now the `getVmExecList()` method is the one which stores the list of all currently running VMs.
  These VMs may or may not be running Cloudlets, but they are available
  for new submitted Cloudlets to be placed into them.
  The old `getVmCreatedList()` stores all created VMs for the broker
  along the entire simulation time.
  This way, the list of all created VMs can be queried after the simulation finishes.

### Changed
- Updates Host Fault Injection Mechanism ([#105](https://github.com/manoelcampos/cloudsim-plus/pull/105)).

## [1.2.2] - 2017-06-03

### Added 
- [Amazon EC2](http://aws.amazon.com/ec2/) instance templates in JSON format ([#97](https://github.com/manoelcampos/cloudsim-plus/pull/97)).
- `Vm.getStartTime()`, `Vm.getStopTime()` and `Vm.getTotalExecutionTime()` methods to, respectively: get the time a VM started running for the first time; the time it was destroyed in the last Host it executed; and the total execution time across all Hosts the VM possibly have migrated across ([#98](https://github.com/manoelcampos/cloudsim-plus/issues/98)).
-  Allow a VM belonging to a broker to be destroyed after all its Cloudlets have finished, independently of the state of other running VMs and according to a given delay ([#99](https://github.com/manoelcampos/cloudsim-plus/issues/99)).

### Changed
- Host Fault Injection Mechanism ([#81](https://github.com/manoelcampos/cloudsim-plus/issues/81)).

### Fixed
- Cloudlets executed with `CloudletSchedulerSpaceShared` were giving incorrect results ([#96](https://github.com/manoelcampos/cloudsim-plus/issues/96)).

## [1.2.1] - 2017-05-28

### Added
- `PowerVmAllocationPolicyMigrationBestFitStaticThreshold` to select the Host to place or migrate a VM
  using a Best Fit policy, that is, it selects the Host with less available resources
  that is enough to place a given VM.
- Enables dynamically adding new columns to the simulation results table  ([#87](https://github.com/manoelcampos/cloudsim-plus/issues/87)).
- Enables changing the CPU migration overhead for any VmScheduler.
  A new constructor was added to these schedulers to enable setting this value once 
  ([#88](https://github.com/manoelcampos/cloudsim-plus/issues/88)).
- Enables Hosts to be powered on and off ([#89](https://github.com/manoelcampos/cloudsim-plus/issues/89)).
- `EventListener` to notify subscribers when all VMs in the `DatacenterBroker` waiting list were created ([#92](https://github.com/manoelcampos/cloudsim-plus/issues/92)).

### Changed
- Updates the Host Fault Injection Mechanism to allow creating a snapshot of a VM
  when all VMs belonging to a broker have failed ([#93](https://github.com/manoelcampos/cloudsim-plus/pull/93)).

### Fixed
- Allocated MIPS for VM was not being reduced during VM migration ([#95](https://github.com/manoelcampos/cloudsim-plus/issues/95)) 

## [1.2.0] - 2017-05-17

### Added

- [Vertical VM Scaling Mechanism](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/autoscaling/) ([#7](https://github.com/manoelcampos/cloudsim-plus/issues/7)) 
  for up and down scaling of VM resources such as Ram, Bandwidth and PEs (CPUs).
- `double getUtilization()` method in the `UtilizationModel` class to get the utilization percentage of a given resource
  at the current simulation time.
- Methods `getUtilizationOfRam()`, `getUtilizationOfBw()`, `getUtilizationOfCpu()` added to Cloudlet in order
  to get utilization percentage of RAM, BW and CPU, respectively, for the current simulation time.
- `UtilizationModel.Unit` enum to define the measuring unit in which a Cloudlet resource, to which a `UtilizationModel` is associated to,
  will be used. The enum values can be `PERCENTAGE` or `ABSOLUTE`, that respectively defines that the Cloudlet resource usage
  will be in percentage or absolute values. The existing UtilizationModels continue to define the value in percentage,
  as describe in their documentation. The `UtilizationModelDynamic` (previously called `UtilizationModelArithmeticProgression`) allows setting a different unit
  for such an `UtilizationModel` ([#62](https://github.com/manoelcampos/cloudsim-plus/issues/62)).
- `UtilizationModelDynamic` now allows
  defining the resource usage increment behavior using a Lambda Expression, enabling the developer
  to give a function that performs the increment in an arithmetic, geometric, exponential or any other
  kind of progression he/she needs ([#64](https://github.com/manoelcampos/cloudsim-plus/issues/64)). 
- Updated the `DatacenterBroker` interface and implementing classes, including the methods `setVmComparator` and `setCloudletComparator` to enable
  a developer to set a `Comparator` object (which can be given as a Lambda Expression) to sort VMs and Cloudlets before they are
  actually requested to be created in some Datacenter. This enables defining priorities to request the creation of such objects.
  If no `Comparator` is defined, the objects creation request follows the order in which they were submitted.
- [Host Fault Injection Mechanism (under development)](https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/HostFaultInjectionExample1.java) to enable injection of random failures into Hosts PEs: it injects failures into Host PEs and reallocates working PEs to running   VMs. When all PEs from a Host fail, it starts clones of failed VMs to recovery from failure. This way, it is simulated the instantiation of VM snapshots into different Hosts ([#81](https://github.com/manoelcampos/cloudsim-plus/issues/81)).
  - Added the method Host.getWorkingPesList().
  - Poisson Distribution implementation enabling the simulation of inter-arrival times of events such as Host failures.
- Added the method void `submitCloudletList(List<? extends Cloudlet> list, Vm vm)` to the `DatacenterBroker`,
  enabling submission of a list of cloudlets to run inside a specific VM.
- Added the method void `submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay)` to the `DatacenterBroker`,
  enabling submission of a list of cloudlets to run inside a specific VM, and at the same time,
  allowing delaying the creation of such cloudlets.
- Added a `getCloudletList()` method int the `CloudletScheduler` to get the list of all cloudlets which are executing
  or waiting inside a given VM.
- Includes new boolean methods to the Pe interface to make easier to check the PE status.
  New methods are `isFree()`, `isBuzy()`, `isFailed(`) and `isWorking()`.
- Allowed disabling log on every `SimEntity` by calling the new method `SimEntity.setLog()`.
  If `Log.disable(`) is called, it disables all simulation logs,
  independent of the configuration on each entity. 
  This new feature enables fine-grained control of entities log.

### Changed

- Changed the methods `getRam()`, `getBw()` and `getSize()` from Vm interface to instead of returning a long value that represents the resource capacity,
  to return an actual Resource object that provides information about the capacity and usage. The method getSize() was renamed to getStorage().
- Changed the methods `getRamCapacity()`, `getBwCapacity()` and `getStorageCapacity()` from Host interface to instead of returning a long value that represents the resource capacity, to return an actual Resource object that provides information about the capacity and usage. The methods were renamed, removing the "Capacity" suffix.
- Automatic generation of IDs for Hosts, VMs and Cloudlets. 
    - Hosts IDs can be manually defined using the `setId()` method, but the constructor parameter was removed.
      The IDs of Hosts across different Datacenters can be duplicated, 
      since when a search for a Host is made using the HostList method,
      just the Hosts of a given Datacenter are considered.
    - Hosts IDs are generated when a List of Hosts is attached to a Datacenter. 
    - Cloudlets and VMs IDs are generated when they are submitted to a Broker.
- Hosts constructors require the RAM and bandwidth capacity, since storage already was required.
- Instantiating a `ResourceProvisionerSimple` now requires just a default no-args constructor.
- The `DatacenterBroker` interfaces now allow using Java 8 Lambda Expressions to define selection policies.
  - It provides a functional way of defining the policies to select a Datacenter to host a Vm, a fallback Datacenter when the creation of requested VMs failed in the previous   selected Datacenter and to select a VM to run a Cloudlet.
  - The DatacenterBrokerSimple is yet selecting the first Datacenter to place VMs and uses a round-robin policy to select a VM to run the Cloudlets. If such behaviors need to be changed, it is not required to create a new DatacenterBroker class. 
  - Since there are 3 selection policies to override (the selection of default datacenter, fallback datacenter and VM), the combination of different implementations for these 3 policies will require the creation of several DatacenterBroker implementations that will be impossible to maintain.
  - Using the new functional implementation there is no need to create a new DatacenterBroker class and the implementations can be exchanged just using the new `setDatacenterSupplier`, `setFallbackDatacenterSupplier` and `setVmMapper` methods, passing a Lambda Expression to them.
  - Automatically set the DatacenterBroker when Cloudlets and VMS are submitted. ([#83](https://github.com/manoelcampos/cloudsim-plus/issues/83))
  - Refactored the method `CloudletScheduler.getCloudletFinishedList()` to keep the list of finished Cloudlets after the simulation
    ends. The `DatacenterBroker.getCloudletsFinishedList()` method returns the list of finished Cloudlets, but for all VMs of the broker.
    The `CloudletScheduler` method allows getting the finished list for a specific VM, enabling the researcher to compute
    some metrics using the data stored in Cloudlets attributes after the simulation ends. ([#78](https://github.com/manoelcampos/cloudsim-plus/issues/78))

## [v1.1.0] - 2017-01-14

### Fixed

- Removed all the duplicated code from `PeProvioner` implementations ([#60](https://github.com/manoelcampos/cloudsim-plus/issues/60))
- Fixed the issue of allocating the same physical PE for multiple Virtual PEs inside the `VmSchedulerTimeShared` class.

### Changed

- Changed the signature of the `PeSimple` constructor that now requires the PE MIPS capacity instead of an ID,
  because it is in fact an attribute that must belong to a PE. The PE is the one that has a MIPS capacity.
  If a PE ID is not defined, when a PE list is assigned to a Host, the host automatically defines the IDs.
- The `PeProvisionerSimple` constructor doesn't require any parameter anymore. It internally creates
  an association with the PE that receives the `PeProvisioner` instance when a Pe is created.
- All the duplicated code inside the `PeProvisioner` and `PeProvisionerSimple` were removed.
  The `PeProvisionerSimple` class now extends the `ResourceProvisionerSimple` and the `PeProvisioner` is now an interface.

## [v1.0.0] - 2017-01-06

### Changed
- Removed the `NetworkCloudletSchedulerSpaceShared` and moved the specific code that was dealing with packets forwarding to the new
`PacketSchedulerSimple` class. The network examples just worked with the `NetworkCloudletSchedulerSpaceShared` and providing such
a class also required other schedulers such as a `NetworkCloudletSchedulerTimeShared` to include the same packets forwarding code.
Further, there was no way to force the developer to use such specific schedulers for network examples
and using a different one caused runtime errors. With the new `PacketSchedulerSimple` class, there is no
need to use a specific scheduler such as the `NetworkCloudletSchedulerSpaceShared`. Just
a regular one like the `CloudletSchedulerSpaceShared` or any other works accordingly.
The developer doesn't even have to create instances of the new `PacketSchedulerSimple`, since
a `NetworkHost` does this job automatically ([#57](https://github.com/manoelcampos/cloudsim-plus/issues/57).)

## [v0.9-beta.2] - 2017-01-04

### Added
- Introduced Horizontal Vm Scaling, allowing dynamic creation of VMs according to an overload condition. Such a condition is defined
  by a predicate that can check different VM resources usage such as CPU, RAM or BW ([#41](https://github.com/manoelcampos/cloudsim-plus/issues/41)).
  See the new [LoadBalancerByHorizontalVmScalingExample](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/autoscaling/LoadBalancerByHorizontalVmScalingExample.java) for a usage example.
- Added the methods `addOnClockTickListener` and `removeOnClockTickListener` to `Simulation` interface to allow defining a Listener to be notified every time the simulation clock advances.
- Added methods `addOnClockTickListener()` and `removeOnClockTickListener()` in Simulation interface in order to add
  and remove listeners for the new OnClockTick event, that is fired every time that the simulation clock advances.
- Added `Vm.getTotalUtilizationOfCpu()` to get Vm's CPU utilization percentage for the current simulation time.
- Added `Broker.submitVm` and `Broker.submitCloudlet` to add a single Vm or Cloudlet to a broker.

### Changed
- Renamed the class `GraphReader` to `TopologyReader`.
- Network module was almost totally refactored and redesigned to provide a module that just works.
  The module was not reusasble at all and it had a huge amount of duplicated code. Some classes
  such as Switch were just an entire copy from each other.
  The packet classes relied on ID to identify the sender and receiver entities and it was
  very difficult to know if a given packet class was storing the ID of a Host, Vm or Cloudlet.
  Now the packets use actual objects such as Host, Vm and Cloudlet to make clear
  what kind of entities are communicating.
- The network examples were just confusing and useless. They have been completely refactored
  and redesigned in order to provide a clear and understandable code that can in fact be reusable.

## [v0.9-beta.1] - 2016-12-22

### Added
- Allowed to delay the submission of VMs by a `DatacenterBroker`, simulating the dynamic arrival of VMs (closes the feature request #23)
- Included extremelly helpful package documentation that can be viewed directly on your IDE or online [here](http://cloudsimplus.org/apidocs/).
  Such a package documentation gives a general overview of the classes used to build a cloud simulation.


### Changed
- Renamed `Simulation` class method `abruptallyTerminate` to `abort`.
- Renamed the class `HostPacket` to `VmPacket` because such a kind of packet is sent between VMs.
- Renamed the class `NetworkPacket` to `HostPacket` because such a kind of packet is sent between Hosts.
- Renamed the class `InfoPacket` to `IcmpPacket` because such a kind of packet is sent to simulate ping requests (ICMP protocol).
- Classes `IcmpPacket`, `HostPacket` and `VmPacket` now implement the new interface `NetworkPacket`
- Re-designed event notification mechanisms that use the `EventListener` class to enable researchers to get notifications
  about some events during simulation execution. The changes are described below:
  - Classes renamed: `HostToVmEventInfo` to `VmHostEventInfo`, `DatacenterToVmEventInfo` to `VmDatacenterEventInfo`, 
    `VmToCloudletEventInfo` to `CloudletVmEventInfo`;
  - Methods `Vm.setOnHostAllocationListener`, `Vm.setOnHostDeallocationListener`, `Vm.setOnVmCreationFailureListener`, `Vm.setOnUpdateVmProcessingListener`, `Cloudlet.setOnUpdateCloudletProcessingListener`, `Cloudlet.setOnCloudletFinishListener`,
  `Simulation.setOnSimulationPausedListener`, `Simulation.setOnEventProcessingListener` and `Simulation.setOnEventProcessingListener`
  were renamed, changing the prefix *set* to *add* because now it is possible to add multiple `EventListener`s to the same event 
  that you want to be notified about;
  - Respective methods starting with the prefix *remove* were added for each one of the methods presented above,
    allowing to remove (unregister) an `EventListener` from the list.
  

## [v0.8-beta.8] - 2016-12-12

### Added
- Enabled dynamic creation of VMs and Cloudlets without requiring creation of Datacenter Brokers at runtime, enabling VMs to be created on-demand according to arrived cloudlets.
  During simulation execution, new VMs and Cloudlets can be submitted using the methods `submitVmList` and `submitCloudletList` that they will be dynamically created. See the [DynamicCreationOfVmsAndCloudlets.java](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/DynamicCreationOfVmsAndCloudlets.java) example for more details.
- Enabled the complete navigation from Cloudlet up to the Datacenter. Now it is possible to call `cloudlet.getVm().getHost().getDatacenter()` and navigate between all the relationships that were introduced in CloudSim Plus for such classes. And it is totally safe to make such a call, even before starting the simulation, that you will not get a `NullPointerException`. In case you make such a call before the simulation starts, as any allocation of Cloudlet or VMs was made, you will get default objects that follow the Null - Included the example [/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/ParallelSimulationsExample.java]
  that shows how to use Java 8 Parallel Streams to execute mutiple simulations scenarios in parallel.
Object Design Pattern, namely `Vm.NULL` for `getVm()`, `Host.NULL` for `getHost()` and `Datacenter.NULL` for `getDatacenter()`.
- Included examples that show how to [schedule the simulation termination at a given time](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/TerminateSimulationAtGivenTime.java), how to terminate it [when a specific condition is met](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/TerminateSimulationAtGivenCondition.java) and how to [schedule a simulation pause at an specific time, before the simulation has started, and collect partial results](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/PauseSimulationAtGivenTime1.java) and [at a given time, after the simulation has started](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/PauseSimulationAtGivenTime2.java).

### Changed
- Changed requirement of cloudsim-plus-examples to Java 8 in order to start providing more advanced examples using Java 8 Lambda Expressions and Streams API.
  Existing examples were not changed and run as before in Java 7.
- Renamed method in Simulation interface: `pause(int src, double delay)` to `Simulation.pauseEntity(int src, double delay)`;
  `hold(int src, long delay)` to `holdEntity(int src, long delay)` and `running()` to `isRunning()`.

### Removed
- Removed the `numUsers` parameter, that represents the number of created DatacenterBrokers, from CloudSim constructor. Now when a Broker is created, it automatically 
  increases the `numberOfUsers` attribute in the given CloudSim instance. This automatically makes possible to create brokers dynamically (during simulation execution) without worring about such an attribute. Usually, dynamic creation of brokers was performed to allow dynamic submission of VMs and Cloudlets during simulation execution. However, as currently the `submitVmList` and `submitCloudletList` methods in DatacenterBroker implementations in CloudSim Plus allow submission of Cloudlets and VMs even after the simulation has started, it is not a requirement anymore to create a new broker just to perform such tasks. 
- Removed the `Simulation.stop()` method because it in fact was doing nothing. When the Simulation.start() is called, it already waits for the simulation to
  finish executing and automatically stops when there is not more events to process.

## [v0.8-beta.7] - 2016-11-29

### Changed
- Removed the PE list parameter from the VmScheduler constructors. Now classes that implement VmScheduler have a host attribute from where the PE list is got directly.
  This attribute is automatically set by a host when the Host.setScheduler setter is called. By this way, the user doesn't have to worry about this VmScheduler attribute. 
  This change makes it easier to create a VmScheduler and consequently a Host.
- Changed DatacenterBroker.bindCloudletToVm(int cloudletId, int vmId) to DatacenterBroker.bindCloudletToVm(Cloudlet cloudlet, Vm vm),
  requiring a Cloudlet and a Vm instead of just int values. Once that the method accepted any int value, even an inexisting Vm or Cloudlet ID 
  could be given, what caused NullPointerException when trying to find the Vm or Cloudlet. Now this problem is completely avoided.
- Changed Cloudlet's vmId attribute from int to Vm and renamed it to vm to conform with the previous change.
- Packages restructuring by placing classes with similar responsibilities together, that makes the project more organized and easier to understand:
  - Moved Datacenter, DatacenterSimple, DatacenterCharacteristics and DatacenterCharacteristicsSimple to `org.cloudbus.cloudsim.datacenters` new package.
  - Moved Host, HostSimple, HostDynamicWorkload, HostDynamicWorkloadSimple and HostStateHistoryEntry to `org.cloudbus.cloudsim.hosts` new package.
  - Moved Vm, VmSimple and VmStateHistoryEntry to `org.cloudbus.cloudsim.vms` new package. 
  - Moved Cloudlet, CloudletAbstract, CloudletExecutionInfo and CloudletSimple to `org.cloudbus.cloudsim.cloudlets` new package.
  - Moved CloudletScheduler interface and implementing classes to `org.cloudbus.cloudsim.schedulers.cloudlet` new package.
  - Moved VmScheduler interface and implementing classes to `org.cloudbus.cloudsim.schedulers.vm` new package.
  - As the package org.cloudbus.cloudsim.power contained a lot of classes with completely different responsibilities (such as Datacenters, Hosts, VMs), 
    such classes were moved to appropriate packages as presented below:  
    - Moved PowerDatacenter and PowerDatacenterNonPowerAware to `org.cloudbus.cloudsim.datacenters.power` new package.
    - Moved PowerHost, PowerHostSimple and PowerHostUtilizationHistory to `org.cloudbus.cloudsim.hosts.power` new package.
    - Moved PowerVm to org.cloudbus.cloudsim.vms.power new package. 
    - Moved PowerVmAllocationPolicy interface and implementing classes to `org.cloudbus.cloudsim.allocationpolicies.power` new package.
    - Moved PowerVmSelectionPolicy interface and implementing classes to `org.cloudbus.cloudsim.selectionpolicies.power` new package.
  - Classes from org.cloudbus.cloudsim.brokers.network also were moved to specific packages:
    - Moved PowerDatacenterBroker to `org.cloudbus.cloudsim.brokers.power` new package.
    - Renamed NetDatacenterBroker to NetworkDatacenterBroker and moved it to `org.cloudbus.cloudsim.brokers.network` new package.
    - Moved NetworkDatacenter to `org.cloudbus.cloudsim.datacenters.network` new package.
    - Moved NetworkHost to `org.cloudbus.cloudsim.hosts.network` new package.
    - Moved AppCloudlet, NetworkCloudlet, CloudletTask, CloudletExecutionTask, CloudletReceiveTask and CloudletSendTask to `org.cloudbus.cloudsim.cloudlets.network` new package.
    - Moved NetworkVm to `org.cloudbus.cloudsim.vms.network` new package.
    - Moved NetworkCloudletSpaceSharedScheduler to `org.cloudbus.cloudsim.schedulers.cloudlet.network` new package.
    - Renamed package org.cloudbus.cloudsim.network.datacenter to `org.cloudbus.cloudsim.network.switches`.  
    - Moved HostPacket, NetworkPacket one package up.
    - Renamed GraphReaderIF to GraphReader.
    - Moved TopologicalNode, TopologicalLink, TopologicalGraph, NetworkTopology to `org.cloudbus.cloudsim.network.topologies` new package.
    - Moved GraphReader and GraphReaderBrite to `org.cloudbus.cloudsim.network.topologies.readers` new package.

## [v0.8-beta.6] - 2016-11-24

- Enabled parallel execution of simulation scenarios

Methods and attributes of the `CloudSim` class aren't static anymore. By this way, each simulation now requires an instance of `CloudSim` instead of calling  methods directly from such a class. Despite this change appears to introduce more complexity when creating a simulation, in fact, it makes it simpler. All classes that extend `SimEntity` required a name to be passed when calling their constructors. Since that name usually was just the name of the class followed by its id, it wasn't meaningful.
The name is just used for log purposes. Accordingly, such constructor parameter was removed and a default name is given for each `SimEntity` object. All `SimEntity` objects now require an `CloudSim` instance. Thus, the constructor parameter "name" was removed and in its place a `CloudSim simulation` parameter was introduced.

Classes such as `Datacenter`, `DatacenterBroker`, `Host`, `Vm` and `Cloudlet` all require a `CloudSim` instance. However, just for the classes that extend `SimEntity` that a `CloudSim` instance has to be manually provided when calling the constructor. For other objects such as `Host`, `Vm` and `Cloudlet`, a `CloudSim` instance is set automatically and the framework users don't have to worry about it.

The code snippet below shows what changed for creating a simulation scenario:

```java
//Initialization is now performed by instantianting a CloudSim object:
//See that currently just the number of cloud users (the number of brokers) is required
CloudSim simulation = new CloudSim(numberOfCloudUsers);
//CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents); //old way

//Creating a Datacenter:
//See that instead of passing a name, it is now passed a CloudSim instance
Datacenter datacenter = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
//Datacenter datacenter = new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple()); //old way

//Creating a Broker:
//The DatacenterBroker (and any SimEntity subclass) requires a CloudSim instance not a name anymore.
//If you want to define a specific name, you can call the setName method yet.
DatacenterBroker broker0 = new DatacenterBrokerSimple(simulation);
//DatacenterBroker broker0 = new DatacenterBrokerSimple("Broker0"); //old way

//Methods to start and stop the simulation now are called from a CloudSim instance and
//are named just start and stop.
simulation.start();
simulation.stop();

```  

As can be seen, this improvement makes it easier to create simulations and brings great benefits:
- Allows several simulation scenarios to be instantiated and executed in parallel. Using Java 8 Lambda Expressions and Streams API it is 
  extremely easy to run such simulations in parallel, that are just possible because CloudSim Plus does not rely on static
  classes that store attributes shared among every simulation.
- Since `CloudSim` class no longer has static attributes that store data specific to a given simulation scenario, it is possible to run multiple simulations at the same time.
- It makes easier to write unit tests for classes that depends on `CloudSim` methods. As all `CloudSim` methods were static, this made very difficult to write tests
  that depend on it, since the class initialization was complex. Due to that complexity, the most obvious way was to create mock objects for `CloudSim`.
  But as the class has just static methods and it in fact couldn't be instantiated, the regular approach to create a mock object using
  libraries such as EasyMock couldn't be applied. The PowerMock library provides a way to mock static methods, but it adds more complexity 
  for test writing. Now that the simulation in fact required a `CloudSim` instance, it is very easy to create a mock `CloudSim` object using EasyMock.   

Additional changes are presented as follows.

### Added
- Introduction of public constructors for `CloudSim` class, that now must be used to create simulations. Such constructors are in fact the old CloudSim.init methods.  
- The most useful constructor introduced accepts just the number of cloud users (number of brokers). The traceFlag and calendar attributes
  are set to default values in this constructor, since the examples commonly use the same values for such parameters. Using such a constructor 
  makes it easier to create a `CloudSim` instance (that in fact initializes the simulation). 

### Changed
- Removed the `name` parameter from `SimEntity` constructors and included a parameter `CloudSim simulation`.  
- Remomve the word "Simulation" from the name of the  methods `startSimulation`, `stopSimulation`, `pauseSimulation`, `terminateSimulation`.
- The method `boolean terminateSimulation(double time)` was renamed to `boolean terminateAt(double time)` to provide a clear name, indicanting
  that it, different from the `terminate` method, will terminate the simulation just at the given time. 

## [v0.8-beta.5] - 2016-11-23

### Added
Introduction of the class CloudletSchedulerCompletelyFair that implements the Completely Fair Scheduler used in recent version of the Linux Kernel. The scheduler performs an actual Cloudlet preemption, assigning a timeslice for each Cloudlet (according to its priority), that allow it  to use the CPU for a specific time interval. After this time slice expires, such Cloudlets are moved to the waiting list, allowing previous waiting ones to execute. This is the first scheduler that in fact considers Cloudlets priorities.
  
The CloudletSchedulerTimeShared does not implements a preemption mechanism and makes an oversimplification assuming that all Cloudlets can run simultaneously, even there are less PEs in the Vm than required by all Cloudlets.

Such a scheduler assumes that if two Cloudlets are concurring for the same PE, they will execute simultaneously by assigning 50% of the PE capacity to each PE. This oversimplification makes that all Cloudlets finish at the exact same  time, what is not possible in a real scheduler. Furthermore, it increases the task completion time of all cloudlets, because it simply distribute the waiting time among all submited Cloudlets, harming all Cloudelets performance.

### Changed
- Constructors of Cloudlet, Vm, Host, Datacenter and DatacenterCharacteristics with a
  too long parameter list were deprecated and simpler constructors were introduced.
- Refactored Resource and ResourceProvisioner classes to remove the generic type for Number, simplifying the class usage.
- Standardized the type to represent amount of RAM, Bandwidth (BW) and Storage to the primitive type long.
- Clearly defined the unit for some resources, updating the documentation. The units are as follows:
  - MB for RAM and Storage
  - Megabits/s for Bandwidth

### Removed
- Removed the hostList parameter from the VmAllocationPolicy constructors. The host list is now automatically got directly from the Datacenter.
  An example of Datacenter instantiation can be `new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple());`

## [v0.8-beta.4] - 2016-11-03

### Changed
- Moved almost all exclusive features of CloudSim Plus to specific packages prefixed with org.cloudsimplus
- The exclusive features moved are now in packages:
    - org.cloudsimplus.builders
    - org.cloudsimplus.heuristics
    - org.cloudsimplus.listeners
    - org.cloudsimplus.util
    - org.cloudsimplus.util.tablebuilder
- The exclusive examples are now in packages:
    - org.cloudsimplus.examples
    - org.cloudsimplus.examples.listeners
    - org.cloudsimplus.examples.migration
    - org.cloudsimplus.examples.workload
- Simplified the CloudletsTableBuilderHelper used to print simulation results in examples
- Several internal refactorings for upcoming features
- Just remembering that all the Testbeds module is exclusive of CloudSim Plus

## [v0.8-beta.3] - 2016-10-15

### Added
- Included documentation about CloudSim Plus modules into the README file
- Inclusion of new module "cloudsim-plus-benchmarks" that uses [JMH (Java Microbenchmark Harness framework)](http://openjdk.java.net/projects/code-tools/jmh/) that implement some
  benchmarks in order to assess CloudSim Plus performance.
- Inclusion of new module "cloudsim-plus-testbeds" to provide a set of more complex and comprehensive 
  testbeds used to assess implementation of algorithms for different purposes such as:
	mapping of Cloudlets to VMs, allocation and scheduling policies, resource utilization models or VM placement and migration policies.
  These testbeds also show how to apply statistical methods to get scientifically valid simulation results. 
  Some method to reduce confidence interval and correlation are implemented and can be used for several experiments.

### Changed 
- The examples module now generates an uber jar file, a jar containing all required dependencies,
  including CloudSim Plus itself. This jar makes easier to run the examples directly from the terminal
  using the traditional java command. Having Maven installed, below are the command line instructions to build and run the examples:
  - at the root folder of the CloudSim Plus, build the project typing `mvn package`
  - at the cloudsim-plus-examples/target folder, run a example (such as the number 1), typing `java -cp cloudsim-plus-examples-1.0-with-dependencies.jar org.cloudbus.cloudsim.examples.CloudSimExample1`
  - realise that you have to change the 1.0 version at the name of the jar file in the instruction above to the actual CloudSim Plus version you are using
- The script in `script/bootstrap.sh` was also updated to make it easier to use. Now, if it is called whitout parameters, it shows
  an usage help. It is in fact the easier way to build the project and run examples. 
  To run the same example 1, it is as easy as typing `./bootstrap.sh org.cloudbus.cloudsim.examples.CloudSimExample1` inside the `script` folder.
  It will automatically discover the exact name of the examples jar file to run, whatever its version number is.
- Some issues closed. 

## [v0.8-beta.2] - 2016-09-15

### Added
- Introduction of classes and interfaces to allow implementation of [heuristics](http://en.wikipedia.org/wiki/Heuristic) such as 
  [Tabu Search](http://en.wikipedia.org/wiki/Tabu_search), [Simulated Annealing](http://en.wikipedia.org/wiki/Simulated_annealing), 
  [Ant Colony Systems](http://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms) and so on.
- Introduction of new package `org.cloudsimplus.heuristics` with classes and interfaces that are the base for implementation
  of heuristics. The `Heuristic` is the base interface and classes such as the `SimulatedAnnealing` one implements it.
  It is an abstract class to be extended in order to provide a simulated annealing implementation for specific problems
  such as mapping of Cloudlets to Vm's.
- A first implementation of a Simulated Annealing heuristic to find a suboptimal mapping between Cloudlets and Vm's
  is provided by class `CloudletToVmMappingSimulatedAnnealing`.
- The new `DatacenterBrokerHeuristic` extends the `DatacenterBrokerSimple` and receives a heuristic implementation
  to find a suboptimal mapping between submitted Cloudlets and Vm's.
- New examples for these features were included in the package `org.cloudsimplus.experiments` of the examples project.

## [v0.8-beta.1] - 2016-09-04

### Changed & Added
- Completey refactored `DatacenterBroker` classes and interface. The `DatacenterBrokerSimple` class had methods with several 
  lines of code performing several different tasks that were very confusing. Method names were completely difficult to understand.
  These classes and interfaces were refactored in order to provide methods that have just one goal. 
- A new `DatacenterBrokerAbstract` class was introduced as the base implementation for `DatacenterBroker`s. Such an interface 
 now enables real extensibility, providing methods to allow defining specific policies for: 
 selection of `Vm`s to host `Cloudlet`s; selection of `Datacenter`s to host `Vm`s;
 and fallback policies to select a different `Datacenter` for `Vm`s when the previous selected one fails to host the requested `Vm`s.
- Now, implementations such as `DatacenterBrokerSimple` are very reduced and completely clear.

## [v0.7-beta] - 2016-08-28

### Added
- A new feature of subtitle columns was added to the TableBuilder interface in order to allow adding a subtitle row below the title row of a table.
  The CloudletsTableBuilderHelper class uses this new feature to make the examples to show the unit of some data presented in the simulation results table.
  By this way, the presented data will be clearer, mainly for new users.
- Included an AbstractCloudlet class that is the base class for Cloudlet implementations.

### Changed
- Finished re-engineering of network.datacenter package, making it now re-usable,
  without hard-coded values defined inside core classes.
- Renamed the ResCloudlet class to CloudletExecutionInfo, giving a clear and helpful name 
- Renamed the internal Cloudlet's Resource class to ExecutionInDatacenterInfo, giving a clear and helpful name 
- Renamed and changed the setSubmissionTime(double clockTime) of the Cloudlet interface
  to registerArrivalOfCloudletIntoDatacenter that now doesn't accept any parameter and
  sets the current simulation time as the cloudlet arrival time at the datacenter.
  The name of the method was causing confusion, because it in fact doesn't represent
  the submission time, but the time that the cloudlet arrived at a datacenter.
- The getter getSubmissionTime was renamed to getDatacenterArrivalTime. Both methods now
  return Cloudlet.NOT_ASSIGNED when the cloudlet hasn't been assigned to a datacenter yet.

## [v0.6-beta] - 2016-06-10 

### Added
- [Examples](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/listeners/) using the new listener features of Vm and Cloudlet classes. 
- Examples showing how to create listeners objects to be notified when: a host is allocated to a VM; a host is desallocated for a VM
  (that can mean the VM finished executing or was migrated); the placement of a VM fails due to lack of a suitable host.
- Examples showing how to reuse the same listener objects to several VMs. 
- [Example showing](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/listeners/VmListenersExample3_DynamicVmCreation.java) how to dynamically create a VM when another one finishes executing, simulating dynamic VM arrival.
  It allows the sequential execution of VMs into a host that just have resources for a VM a time. 
  The use of listeners simplify the dynamic creation of VMs and allows the code to be clear
  and direct, without recurring to threads and sleep tricks. 
- Converted the relationship between Vm and CloudletScheduler to a bi-directional one (now CloudletScheduler has access to Vm).
- Included new listener `onUpdateCloudletProcessingListener` for Cloudlet, that gets notified when 
  the execution of the Cloudlet inside a Vm is updated. A new example of this feature was introduced in the  
  [listeners](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/listeners/) example package.
- Allowed to delay the submission of cloudlets by a `DatacenterBroker`, simulating the dynamic arrival of Cloudlets (closes the feature request #11)

## [v0.5-beta] - 2016-04-28 

### Changed
- Changed the name of the method getCloudResourceList at CloudSim class to getDatacenterIdsList once it
  in fact returns only Datacenter IDs and is only used by DatacenterBroker classes.
- Changed the name of the method getList at CloudInformationService class to getDatacenterIdsList once it
  in fact returns only Datacenter IDs and is only used by CloudSim class.
- Changed the name of the protected method submitCloudlets() at DatacenterBrokerSimple class to createCloudletsInVms()
  because it in fact create each cloudlet into some Vm. The method was changed to avoid confusion with the submitCloudletList too
  and to conform the name convention used in the createVmInDatacenter method.
- Changed the name of the method getCloudletList() at the DatacenterBroker interface to  getCloudletsWaitingList()
  because in fact, this is the list of cloudlets that were submitted and are waiting to be created inside some Vm.
- Changed the name of the method getVmList() at the DatacenterBroker interface to  getVmsWaitingList()
  because in fact, this is the list of VMs that were submitted and are waiting to be created inside some Datacenter.
  The method name was changed to conform the name convention used by the getCloudletsWaitingList method.
- Changed the name of the method getCloudletReceivedList() at the DatacenterBroker interface to  getCloudletsFinishedList()
  because in fact, this is the list of cloudlets that have finished executing.
- Changed the name of the method getDatacenterCharacteristicsList at the DatacenterBrokerSimple class to getDatacenterCharacteristicsMap
  because in fact it is returning a map, not a list. 
- Renamed the class TaskStage at the package org.cloudbus.cloudsim.network.datacenter to CloudletTask
  and made it an abstract class, because it in fact doesn't represent the stage of a task
  but a task itself. New sub-classes were introduced. See the section "Added" below.
  
### Added
- Created new subclasses CloudletDataTask and CloudletExecutionTask from CloudletTask.
  The CloudletDataTask can be used to send or receive data, according to it's type attribute
  (that now is an enum).


## [v0.4-beta] - 2016-04-25 

### Fixed
- The `HarddriveStorage` class had an issue when calling the method `addReservedFile` without priorly reserving space for the file by 
calling the `reserveSpace` method. Consider the situation where the allocated space is 0. When adding a reserved file of size 50 without 
reserving the space, the method `addReservedFile` was decreasing the allocated space to -50. After adding the file it increased 
the allocated space to 0 again. However, the allocated space couldn't be zero once a file (reserved or not) was added. 
Other issue occurred when reserving a space and adding a file of size different from the reserved. 
These issues were corrected by introducing the `reservedStorage` attribute to control the reserved space. 
Several unit tests were included to test the class.

- Corrected the value of the const org.cloudbus.cloudsim.Consts.WEEK that the values was duplicated from the DAY constant.

- Updated the DatacenterSimple class to use the scheduleInterval to define the interval to update cloudlets processing, 
in the same way as in PowerDatacenter class

- All the implementations of the method VmAllocationPolicy.optimizeAllocation are now returning an empty map instead 
of null when the method in fact doesn't perform any VM placement optimization. 
This change was performed to reduce null checks and avoid NullPointerException's.

- The method getCloudletFinishedSoFar of CloudletSimple class now returns 0 when the cloudlet hasn't started executing yet,
instead of returning the cloudlet length. If it hasn't started yet, the executed length is abviously 0.

- Changed the return value of the VmAllocationPolicy.optimizeAllocation method from List<Map<String, Object>> to Map<Vm, Host>
	- The return value was completely strange and didn't correctly use generics. 
    The method return was a List of Maps where the keys were either the strings "vm" or "host". 
    If the key was the string "vm", it meant the value was a Vm instance. If the key was the string "host", 
    it meant the value was a Host instance. Thus, each Map represented the relation between a Vm and the Host where it was to be placed.
    
    - This design was very confusing, requiring a lot of explanation and several unsafe typecasts. Now the method, 
    and all related ones that were returning a List<Map<String, Object>>, are returning a Map<Vm, Host> 
    that doesn't need many explanation. It is the map between a Vm and the Host where it has to be placed.
    
    - The method DatacenterSimple.processVmMigrate that expected the ev.getData() to be a Map<String, Object>, 
    now expects a Map.Entry<Vm, Host>, making to code clearer, less error-prone and reduces typecasts.


### Removed 
- The parameters of the method `File.deleteFile(final String fileName, File file)` were not being used as defined in the method documentation. 
It was supposed to pass the name of the file to be removed and the `file` parameter should return the removed `File` object. 
By this way, it was supposed to pass a `File` object as parameter and get the deleted `File` into this object. 
However, Java passes object references by value (not by reference), what means that if the reference to the `file` parameter 
is changed inside the method, the reference of the original object outside the method is not updated. 
Such a Java behaviour makes impossible to the method to work as proposed. Therefore, the method was removed.

- Removal of duplicated `fileSize` and `fileName` attributes at `FileAttribute` class. Now `FileAttribute` class has a relationship with `File`.

- Classes `RamProvisioner`, `RamProvisionerSimple`, `BwProvisioner` and `BwProvisionerSimple` were deleted and 
new classes and interfaces were introduced (PeProvisioner and PeProvisionerSimple are working in progress yet). 
The new `ResourceProvisionerSimple` class is the only concrete class that you have to use in order to instantiate a provisioner for 
`Ram`, `Bandwidth` or a `FileStorage` (such as a `HarddriveStorage`) resource object. 
See more details of what changed in the [items below](#resource-interface).

- Deleted the ParameterException class and changed its use to the unchecked native InvalidArgumentException. 
Unchecked exceptions doesn't oblige the developer to always insert try ... catch blocks, makes the code clearer and 
is the default exception used to invalidate given parameters. Lets say that a method "A" throws a checked exception, 
and it is called by method "B", that is called by method "C". 
All these methods are required to throws the exception or catch them. Using unchecked exception you just 
have to place your try...catch at the last method that called method (method "C" in this example), 
reducing [boilerplate code](https://en.wikipedia.org/wiki/Boilerplate_code).

- The class NetworkConstants was deleted because all of its values were just values used by the network examples. 
Thus, they were accordingly moved to the classes that they belong. Some constants included in this class were just 
values to be used by Switch related classes. Placing these constants outside the classes that 
in fact use them makes the classes design low cohesive. 
Accordingly, to increase the cohesion, making easy to understand how the elements are related, 
the constants were moved to the appropriated classes. 

- Classes WorkflowApp and BagOfTasksAppCloudlet were deleted because they were in fact just creating hard-coded VMs and Cloudlets
and they weren't providing any specific code. In replacement, they were created the NetworkVmsExampleWorkflowAppCloudlet 
and NetworkVmsExampleBagOfTasksAppCloudlet classes that just instantiate regular AppCloudlet and NetworkCloudlet 
classes with specific values, in order to provide the desired kind of tasks to be simulated.

- See more details about [network changes below](#network-changes).


### Changed
- Throughout documentation update and improvement  
- It was changed the requirements of the cloudsim project to Java 8 (the examples remain in Java 7).
- Updated CloudSimExample7 
	- Moved the anonymous Thread class to a specific class named MonitorThread, outside the CloudSimExample7 class, but in the same file.
    - Included the attribute DatacenterBroker broker inside the  MonitorThread class in order to get the broker dynamically created inside the Thread.
    - Printed the list of cloudlets submitted to the broker created by the MonitorThread.

- Changed the Vm.addStateHistoryEntry method to receive a VmStateHistoryEntry object instead of a
separated value for each of its attributes, providing a more OO design

- Renamed the methods getBw and getRam to getBwCapacity and getRamCapacity
- Refactored the WorkloadFileReader to completely remove code duplication. Included new test cases for gz, zip and swf workload files.

- All classes that usually have to be extended by CloudSim users in order to provide some specific features for their 
simulations were moved to new appropriated packages in order to increase class's organization:
	- Moved all UtilizationModel classes and interfaces to new package org.cloudbus.cloudsim.utilizationmodels
	- Moved all DatacenterBroker classes and interfaces to new package org.cloudbus.cloudsim.brokers
	- Moved all CloudletScheduler and VmScheduler classes and interfaces to new package org.cloudbus.cloudsim.schedulers
	- Moved all VmAllocationPolicy classes and interfaces to new package org.cloudbus.cloudsim.allocationpolicies
	- Moved classes Packet, InfoPacket and NetworkTopology to the network package

- Moved the duplicated methods updateVmProcessing, getNextFinishedCloudlet, areThereFinishedCloudlets, 
getTotalUtilizationOfCpu, cloudletCancel, cloudletStatus, cloudletPause, cloudletSubmit(Cloudlet cloudlet) and other ones from
CloudletSchedulerSpaceShared and CloudletSchedulerTimeShared to CloudletSchedulerAbstract class.
	- Some of these methods were exactly equal. Other ones such as the updateVmProcessing, cloudletCancel 
    and cloudletPause at the CloudletSchedulerSpaceShared class were just looking at the list of waiting cloudlets.
	However, this list can be just empty in the CloudletSchedulerTimeShared class,
	since all cloudlets will share the processor time (not waiting for a previous cloudlet to completely finishe executing after it can start running).
	By this way, moving the methods to the super class doesn't change the behaviour of these different schedulers. 
    Other subclasses in fact completely override methods such as updateVmProcessing.
	- By this way, now it is easier to extend and test these classes, since a bunch of duplicated code was removed, increasing code reuse.

- <a name="network-changes"></a>THE MOST CRITICAL UPDATE IN NETWORK RELATED CLASSES
    - The classes related to the examples at the org.cloudbus.cloudsim.network.datacenter package of the
    cloudsim-examples project were almost totally remade.
    - The network related classes such as AppCloudlet, NetworkCloudlet, NetDatacenterBroker had an extremely amount of duplicated code.
    The NetDatacenterBroker was a copy of the original DatacenterBroker class, instead of extending it and overriding
    necessary methods. It was noticed that a lot of code in the created class was exactly equal to the DatacenterBroker,
    which shows that such methods should be inherited from the already existing class.
    - The code that in fact belonged only to the NetDatacenterBroker class was completely repetitive and confuse.
    There were very long methods that should be divided into smaller and specific ones in order to avoid
    code redundancy and make clear what the code does.
    - The class used a lot of hard-coded values for creating objects such as Cloudlets and VMs,
    that doesn't make sense once these values have to be defined by the developer creating
    the simulation. It was clear that the values were defined just to perform the simulations
    presented at the paper ["NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations"](https://doi.org/10.1109/UCC.2011.24). 
    By this way, they aren't useful for other users
    which desire to create their own simulations. Further, the creation of hard-coded Cloudlets and VMs inside
    the NetDatacenterBroker doesn't make it to be reusable and even violates the 
    [Open/Closed Principle (OCP)](https://en.wikipedia.org/wiki/Open/closed_principle).
    - Once the mentioned paper doesn't provide low level details about how every class's method works (and it is not supposed that
    a paper will present such in deep code details) and that there weren't any unit test for the network related classes,
    it was not even feasible to know if the classes and examples were working correctly.
    The examples didn't show any useful information, just some vague messages such as "App1, App2, ... AppN" to inform the creation of AppCloudlets,
    not providing actual information. In face of that, the best to do was to perform a in-depth refactoring of the classes and examples
    in order to define a actual class hierarchy, eliminate code redundancy and provide short and meaningful methods
    that make clear to understand the code and to just further (unfortunately) introduced test units.
    - Refactoring of Switch classes in order to move constants from NetworkConstants to them, removing unnecessary parameters in their constructors


### Added & Changed
- A complete new set of interfaces was introduced in order to start providing a more structured class hierarchy and to reduce code duplication. 
The classes `Datacenter`, `DatacenterCharacteristics`, `DatacenterBroker`, `Host`, `PowerHost`, `Pe`, `Vm`, `VmAllocationPolicy`, `VmScheduler`, 
`Cloudlet` and `CloudletScheduler` (and maybe others) had their names suffixed with the word "Simple", as already has been used in other classes 
such as `VmAllocationPolicySimple`. Further, they were introduced new interfaces with the same name of the original classes 
(without the suffix "Simple"), defining the common public methods to be present in each class that implements one of these interfaces. 
By this way, it was paved the way to start applying the "Liskov Substitution Principle", one of the SOLID principles that says to 
"program to an interface, not to an implementation".

- All the examples were accordingly updated in order to use the new classes. Thus, for all mentioned classes, instead of
instantiating an object such as `Cloudlet cloudlet = new Cloudlet(required_parameters_here)` you have to use the new corresponding class.
In the example, code line of cloud would be `Cloudlet cloudlet = new CloudletSimple(required_parameters_here)`. Realize that the declared variable
doesn't have to be declared as CloudletSimple, but just as Cloudlet (as previously). This follows the "program to an interface" principle just mentioned.
However, you can declare and instantiate the object using `CloudletSimple cloudlet = new CloudletSimple(required_parameters_here)`.

- Implementation of the Null Object Design Pattern in order to start avoiding null checks and `NullPointerException` 
when using the classes just mentioned above.
The `NULL` attributes created are broadly used by classes of the new `org.cloudsimplus.builders` package in order to avoid `NullPointerException`. 
This package will be presented in the next sections.

- Each interface that implements the Null Object Design Pattern has a public static final attribute named `NULL`, that implements the interface itself, 
returning: zero for all numeric methods; false for all boolean methods; an empty string for all String methods; 
an empty unmodifiable list for all List methods; 
and an empty unmodifiable Map for all Map methods. Taking the `Vm` interface as example, using the new `NULL` attribute defined on it, 
instead of returning `null` when a method doesn't find a `Vm`, such a method can return `Vm.NULL`. 
To verify if a valid `Vm` was returned, you have to use `if(!vm.equals(Vm.NULL))` instead of `if(vm == null)`.

- Created an enum Cloudlet.Status and changed the cloudlet status to that type. 
The use of an integer attribute to define the status of the cloudlet inccurs more code to validate the attribute.
It is not transparent to the user what are the acceptable values. Further, passing an integer value out of the
acceptable range will only raise exception in runtime.
With the enum these problems were solved.

- Created an enum Pe.Status and changed the status of a Pe to that type, avoiding the use of int constants

- Several refactorings to: reduce code duplication; improve class hierarchy and project’s extensibility; 
reduce bug probability; clean the code and unit tests in order to improve code readability; correct minor bugs; 
reduce code duplication of unit tests; include extensive set of unit tests to validate performed changes.
    - Refactorings in classes such as `HostSimple`, `VmSimple` and provisioners, changing the way that the capacity and allocated resources are stored.    
    - It has been noticed a repetitive set of attributes and methods over several classes. 
    For instance: attributes to store resource capacity and allocation; and methods to get the amount of free available resource 
    and amount of allocated resource. 
    These repetitive codes were found in classes such as VmSimple and provisioners, what made clear the need of a new class with these behaviours and data. 
    Accordingly, a new set of classes was created inside the new package `org.cloudbus.cloudsim.resources`.
    
    - <a name="resource-interface"></a>A `Resource` interface was created, that is used to define resource capacity and allocation for Hosts and VMs. 
    It uses generics to identify the type associated with the resource (if `Double`, `Long`, `Integer`, etc). 
    It defines the signature of methods to manage a given resource such as CPU, RAM or BW of VMs and Hosts.
    
    - The new `ResourceAbstract` class implements basic behaviours of a `Resource`, managing its capacity, allocated and available amount of resources. 
    New validations were introduced to avoid setting invalid values to the resource, such as negative values or a zero capacity. 
    The class also has a new setter that allows changing the capacity, provided that the new one is greater or equals to the amount of allocated resource. 
    It is also avoided to set an available resource amount greater than the capacity. 
    All methods that try to change the resource capacity or allocated size now returns a boolean to indicate if the operation succeeded or not.
    
    - The method `isSuitablForVm` of provisioner classes previously changed the current allocated VM resource just to check if it was 
    possible to change the total allocated resource to another value. 
    The method was changed to perform the verification, without actually changing the current allocated resource. 
    It only performs the necessary calculations to find out that.  The method in fact was moved to the new `Resource` 
    interface and is just called `isSuitable`.
    
    - Concrete classes were created for specific resources, namely `Ram` and `Bandwidth` classes 
    (the already existing `HarddriveStorage` and `SanStorage` classes now implement `FileStorage` interface that is presented in the next item). 
    They hide the abstract type of the `Resource` (if `Double`, `Integer`, etc), facilitating the class use and ensuring that the same type 
    is always used for a given resource (`Bandwidth` always use `Long`, `Ram` always use `Integer`, `FileStorage` always use `Long`). 
    It also standardize the type of each resource, avoiding some inconsistencies found throughout the code.
    
    - Several refactoring on classes that control storage space, in order to remove code duplication, improve class hierarchy and 
    reduce bugs probabilities (once that duplicated code doesn't have to be tested several times in different classes). 
    The major part of duplicated code was related to dealing with storage capacity, used space and available space. 
    As the introduced `Resource` interface and related classes implement these features, there isn't duplicated code for that anymore.
        - The interface `Storage` is now called `FileStorage` in order to differentiate it from the new class `RawStorage`. 
        The first one has methods to perform file system operations (such as add and delete a file). 
        The last one is a raw storage that doesn't keep track of files, but just manages the  capacity, used space and free space. 
        For instance, the `Host` and `Vm` objects just control their storage capacity and available space. 
        Accordingly, they just use a storage attribute (an instance of the previously mentioned `Resource` interface) to manage this storage data.
        - The `HarddriveStorage` class now implements the `FileStorage` interface.
        - With the new `FileStorage` interface, the public interface of the `DatacenterSimple` class changed. 
        Now, to instantiate a `DatacenterSimple` it has to be passed a `List<FileStorage>` instead of `List<Storage>`.
        - The classes `HarddriveStorage` and `SanStorage` were moved to the new package `org.cloudbus.cloudsim.resources`, 
        including the `FileStorage` interface.
        - The public interface of the `Datacenter` class and all its subclasses was changed due to renaming the `Storage` interface to FileStorage. 
        Now the constructor of these classes has to receive a `List<FileStorage>` instead of `List<Storage>`.
        - The "double storage" attribute of the `HostSimple` class originally was being decreased every time a new `Vm` was placed at the host, 
        instead of managing the current allocated storage (as it is being made for other resources such as `Ram` and `Bandwidth`). 
        Now, the `HostSimple` uses an internal `RawStorage` object to manage the capacity and allocated space instead of just a primitive double attribute. 
        The method `getStorage` now always return the actual host storage capacity, not the remaining space. 
        The data returned by the method changed, however, it was verified that just the `Host` itself was calling this public method. 
        Thus, the method was renamed to `getStorageCapacity` (and the setter to `setStorageCapacity`) to avoid confusion. 
        A new protected method `getStorage` was introduced to return the `RawStorage` object.

    - Now that each kind of resource uses a specific generic class, the `Vm` and `VmSimple` had to conform with these new changes, 
    in order to get the object that represents a specific resource of the VM (such as `Ram` or `Bandwidth`) from the class of such a resource. 
    To implement that, a new `Map` was introduced in the `VmSimple` class, where: each key is a resource class; 
    each value is the object that represents such a VM resource. By this way, the new method `Vm.getResource` is used by classes such as the 
    `ResourceProvisionerSimple` to get information of a given `Vm` resource (such as capacity and available amount). 
    For instance, if a `ResourceProvisioner` manages allocation of `Ram` resource, the class uses the getResource method of the 
    `Vm` interface to get the information about the `Ram` object assigned to the VM. 
    Despite these modifications, it doesn't change how a Vm is used.
    
    - Now, when instantiating a `Host`, it doesn’t have to be used a different `ResourceProvisioner` class for each resource such as Ram and Bw
    (at least if you don’t want to). It only has to be passed a different instance of a `ResourceRrovisioner` for each `Host` resource. 
    Instead of each provisioner instance receiving the resource capacity (int, long or double), now each one has to receive an instance of a `Resource`, 
    which in turn receives the resource capacity. So, the `ResourceProvisioner` interface was changed to receive a `Resource` object, 
    that will store all information about itself (such as its capacity, the amount of free resource, etc). 
    Below it is shown a example of how to instantiate a `Host`, before and after the performed changes:
        - **Before changes** : `new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), new VmSchedulerTimeShared(peList));`
        - **After changes**: `new HostSimple(hostId, new ResourceProvisionerSimple<>(new Ram(ram)), new ResourceProvisionerSimple<>(new Bandwidth(bw)), storage, peList, new VmSchedulerTimeShared(peList);`

### Added 
- Added the SwfWorkloadFormatExample1 example that shows how to use the WorkloadFileReader to create cloudlets from a trace file in the
[Standard Workload Format from the Hebrew University of Jerusalem](http://www.cs.huji.ac.il/labs/parallel/workload/).
The example uses a new DatacenterBroker class created to allow submission of VMs to a datacenter, ordering VMs descendingly, according
to their PEs number. By this way, VMs requiring more PEs will be submitted first for creation inside a datacenter's host.

- Inclusion of MigrationExample1 class, a simple simulation example that performs VM migration due to under and over host utilization.  
    - The original examples inside packages org.cloudbus.cloudsim.examples.power.planetlab and
    org.cloudbus.cloudsim.examples.power.random are over complex to allow a novice CloudSim user to understand how to perform VM migration simulations.
    These examples create too much Hosts, VMs and Cloudlets to enable any reasoning about the obtained results. 
    They don't make clear why, how and when the migrations were performed.
    - The included example implements a Worst Fit VmAllocationPolicy that selects the first host with the most available CPU capacity to host a VM.
    - It was also included a custom UtilizationModel to implement progressive cloudlet CPU usage along the simulation time. 
    The cloudlet CPU usage changes by Arithmetic Progression, allowing to know exactly how it will be along the time. 
    By this way, it is easy to understand simulation results.

- Added the interface TableBuilder and classes AbstractTableBuilder, TextTableBuilder, CsvTableBuilder and HtmlTableBuilder to the 
org.cloudsimplus.util.tablebuilder package
    -  They provide standard features for printing CloudSim generated data.
    The set of classes use the Builder Design Pattern in order to create a table in different formats such as Text, CSV and HTML.
    The class hierarchy was designed to enable the easy creation of new table formats (such as Latex and others).
    Any kind of data can be printed into the tables, enabling the specification of a format for each column (if desired).
    
    - Updated all examples in order to remove the duplicated code to print simulation results (the list of executed cloudlets)
    and start using the new set of classes for generating tabular data.
    Simulation results were presented in a not completely organized tabular format, that caused some data to
    appear misplaced. A new CloudletsTableBuilderHelper class was created into the package org.cloudsimplus.util.tablebuilder
    in order to easily print simulation results and to present them in a well formatted table that
    makes results analysis easier.

- Inclusion of Integration Tests (IT)
    - Configuration of the pom.xml file of the cloudsim project to perform execution of Functional/Integration Tests
    in order to test entire simulation scenarios instead of just isolated units.
    With the integration tests, it is safer to perform changes in the code and make sure that
    changes don't brake anything. For instance, the created Integration Test called `VmCreationFailureIntegrationTest`,
    verifies if:
        - a given VM failed to be created due to lack of host resources;
        - the time a host is allocated or deallocated to a given VM; 
        - finishing and execution time of different cloudlets using a specific `CloudletScheduler`.
    - It was used the already included maven-surefire-plugin instead of the maven-failsafe-plugin to run
    these new tests. By this way, it is possible to see the Integration/Functional tests results directly at the NetBeans JUnit
    graphical test results interface.
    - Functional/Integration Tests have to be included in the package `org.cloudsimplus.IntegrationTests`
    (as configured in maven profiles inside the pom.xml).
    - To run all tests at NetBeans, including the Functional/Integration ones, you can right click on the project root,
    select "Set Configurtion >> integration-test", right click again and select "Custom >> integration-tests" 
    (that is configured in the nbactions.xml). To run in other IDE's, you can see the maven parameters at the nbactions.xml file.
    - Included the CheckHostAvailableMips Integration Test. The IT checks if the Host CPU utilization is as expected along the simulation run. 


- Inclusion of the interface `EventListener` in order to provide event notification features for user applications running CloudSim simulations.
These notifications can be about the change in state of CloudSim entities.
	- The package `org.cloudsimplus.listeners` was introduced to place the classes and interfaces related to event listeners.

	- First listeners were included in the `Vm` class. The following listeners attributes were introduced to allow CloudSim users
    to set listeners to receive notifications about Vm state changes:
	    - `onHostAllocationListener`: gets notified when a Host is allocated to a Vm
	    - `onHostDeallocationListener`: gets notified when a Host is deallocated to a Vm
	    - `onVmCreationFailureListener:` gets notified when a Vm fail being placed at a Host due to lack of resources
	- The inclusion of the Vm listeners doesn't change the way VMs are instantiated.

	- The `EventListener` interface implements the Null Object Design Pattern in order to avoid `NullPointerException` when a
	EventListener is not set. In order to ensure that, listener properties are being initialized with the `Listener.NULL` object.

	- These listeners allow CloudSim users to perform specific tasks when different events happen,
	    enabling implementation of comprehensive functional and integration tests.
	    Examples of these new features can be seen in the `VmCreationFailureIntegrationTest` Integration Test class.

	- Introduced a onUpdateVmsProcessingListener on the Host interface in order to notify listeners when a host updates its VMs processing

	- Update the CloudSim class to include the EventListener<SimEvent> eventProcessingListener attribute. It uses the new EventListener 
    interface to notify observer objects when any event of any kind is processed by CloudSim simulator. See the setEventProcessingListener method 
    to define a observer object to get notified when any event is processed. The inclusion of the listener doesn't break applications using previous 
    CloudSim versions.

	- Due to the use of the new eventProcessingListener at the CloudSim class, it was introduced the method getInstance() 
    that implements the Singleton Design Pattern in order to avoid multiple instances of the CloudSim class. However, 
    CloudSim continues working through its static method calls.


- Inclusion of the package `org.cloudsimplus.builders` with classes and interfaces that implement the [Builder Design Pattern](https://en.wikipedia.org/wiki/Builder_pattern) 
	- These classes and interfaces were introduced as an alternative way to help creating CloudSim objects, such as 
    `Host`, `Datacenter`, `DatacenterBroker`, `Vm` and `Cloudlet`.

	- The creation of these objects requires a lot of parameters and some of them have to be created before other ones
	(e.g: Hosts have to be created before a `Datacenter`). The constructor of these objects are complex and the creation of multiple objects
    of the same class is a repetitive task that can lead to code redundancy.

	- The table classes were introduced to reduce complexity when creating these objects.
    They help setting values to be used when creating such objects, by providing default values for each object property.
    Once these values are set, just one method needs to be called to create as many copies of the object as desired.
    For instance, if you want to create 2 hosts with the same configuration,
    you just have to set the host attributes first and then, call the `createHosts(int amount).getHosts()` method of the `HostBuilder` class
    in order to create the amount of Hosts you want and get the list of created hosts. 

	- The new builders use the Decorator Design Pattern in order to ensure a given object creation flow.
    For instance, VMs and Cloudlets have to be submitted to a given `DatacenterBroker`.
    By this way, just after calling the `createBroker()` method at the `BrokerBuilder` class that is possible to make a chained call to the
    `getVmBuilderForTheCreatedBroker().createAndSubmitVms(int amount)`.
    This method allows the creation of VM and submission to the broker just created.
    For instance, you can't make the call `brokerBuilder.getVmBuilderForTheCreatedBroker().createAndSubmitVms(2)`.
    You have to call `brokerBuilder.createBroker().getVmBuilderForTheCreatedBroker().createAndSubmitVms(2)`
    which ensures that VMs are created only after creating a broker.

	- An entire example of the use of these builders can be seen at the `VmCreationFailureIntegrationTest` Integration Test class.
	- The builders can be used by Unit and Integration Tests and by CloudSim users that want to create cloud simulations.
    
