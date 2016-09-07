# Change Log

Lists the main changes in the project.

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
- [Examples](cloudsim-plus-examples/src/main/java/org/cloudbus/cloudsim/examples/listeners/) using the new listener features of Vm and Cloudlet classes. 
- Examples showing how to create listeners objects to be notified when: a host is allocated to a VM; a host is desallocated for a VM
  (that can mean the VM finished executing or was migrated); the placement of a VM fails due to lack of a suitable host.
- Examples showing how to reuse the same listener objects to several VMs. 
- Example showing how to dynamically create a VM when another one finishes executing, simulating dynamic VM arrival.
  It allows the sequential execution of VMs into a host that just have resources for a VM a time. 
  The use of listeners simplify the dynamic creation of VMs and allows the code to be clear
  and direct, without recurring to threads and sleep tricks. 
- Converted the relationship between Vm and CloudletScheduler to a bi-directional one (now CloudletScheduler has access to Vm).
- Included new listener `onUpdateCloudletProcessingListener` for Cloudlet, that gets notified when 
  the execution of the Cloudlet inside a Vm is updated. A new example of this feature was introduced in the  
  [listeners](cloudsim-plus-examples/src/main/java/org/cloudbus/cloudsim/examples/listeners/) example package.
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
    presented at the paper ["NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations"](http://dx.doi.org/10.1109/UCC.2011.24). 
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
The `NULL` attributes created are broadly used by classes of the new `org.cloudbus.cloudsim.builders` package in order to avoid `NullPointerException`. 
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
    Despite these changes, it doesn't change how a Vm is used.
    
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
org.cloudbus.cloudsim.util package
    -  They provide standard features for printing CloudSim generated data.
    The set of classes use the Builder Design Pattern in order to create a table in different formats such as Text, CSV and HTML.
    The class hierarchy was designed to enable the easy creation of new table formats (such as Latex and others).
    Any kind of data can be printed into the tables, enabling the specification of a format for each column (if desired).
    
    - Updated all examples in order to remove the duplicated code to print simulation results (the list of executed cloudlets)
    and start using the new set of classes for generating tabular data.
    Simulation results were presented in a not completely organized tabular format, that caused some data to
    appear misplaced. A new ResultsHelper class was created into the package org.cloudbus.cloudsim.examples.util
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
    - Functional/Integration Tests have to be included in the package `org.cloudbus.cloudsim.IntegrationTests`
    (as configured in maven profiles inside the pom.xml).
    - To run all tests at NetBeans, including the Functional/Integration ones, you can right click on the project root,
    select "Set Configurtion >> integration-test", right click again and select "Custom >> integration-tests" 
    (that is configured in the nbactions.xml). To run in other IDE's, you can see the maven parameters at the nbactions.xml file.
    - Included the CheckHostAvailableMips Integration Test. The IT checks if the Host CPU utilization is as expected along the simulation run. 


- Inclusion of the interface `EventListener` in order to provide event notification features for user applications running CloudSim simulations.
These notifications can be about the change in state of CloudSim entities.
	- The package `org.cloudbus.cloudsim.listeners` was introduced to place the classes and interfaces related to event listeners.

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


- Inclusion of the package `org.cloudbus.cloudsim.builders` with classes and interfaces that implement the [Builder Design Pattern](https://en.wikipedia.org/wiki/Builder_pattern) 
	- These classes and interfaces were introduced as an alternative way to help creating CloudSim objects, such as 
    `Host`, `Datacenter`, `DatacenterBroker`, `Vm` and `Cloudlet`.

	- The creation of these objects requires a lot of parameters and some of them have to be created before other ones
	(e.g: Hosts have to be created before a `Datacenter`). The constructor of these objects are complex and the creation of multiple objects
    of the same class is a repetitive task that can lead to code redundancy.

	- The builder classes were introduced to reduce complexity when creating these objects.
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
    