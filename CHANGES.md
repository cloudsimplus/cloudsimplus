# CloudSim 4.0 Change Log 

**Listing just the main features. For a complete overview of the changes, the the commit logs**

- Throughout documentation update and improvement  

- Created an enum Cloudlet.Status and changed the cloudlet status to that type. A new test case was accordingly created.
The use of an integer attribute to define the status of the cloudlet inccur more code to validate the attribute.
It is not transparent to the user what are the acceptable values. Further, passing a integer value out of the
acceptable range will only raise exception in runtime.
With the enum these problems were solved. 155fb79124d5d289fe37c97ed61bb2833d557e80

- The method getCloudletFinishedSoFar of CloudletSimple class now returns 0 when the cloudlet hasn't started executing yet,
instead of returning the cloudlet length. If it hasn't started, the executed length is thus 0. @bfc15b66e565d0c1c0e38dde394a981aee4ec962

- Created an enum Pe.Status to define the status of a Pe, avoiding the use of int constants (that require extra validation and may be prone to store values outside the acceptable range). @5d4b622b24ac2ca2bdfdecb88839718867d0f936

- Several refactorings to: reduce code duplication; improve class hierarchy and project’s extensibility; reduce bug probability; clean the code and unit tests in order to improve code readability; correct minor bugs; reduce code duplication of unit tests; include extensive set of unit tests to validate performed changes. @1798447db592c447afbcf6b830a9eeec94f9ce86    
    - Several refactorings in classes such as `Host`, `Vm` and provisioners, changing the way that the capacity and allocated resources are stored.
    	- It has been noticed a repetitive set of attributes and methods over several classes. For instance: attributes to store resource capacity and allocation; and methods to get the amount of free available resource and amount of allocated resource. These repetitive codes were found in classes such as Vm and provisioners, what made clear the need of a new class with these behaviours and data. Accordingly, a new set of classes was created inside the new package `org.cloudbus.cloudsim.resources`.
    
    	- An abstract `Resource` interface was created, that is used to define resource capacity and allocation for Hosts and VMs. It uses generics to identify the type associated with the resource (if `Double`, `Long`, `Integer`, etc). It defines the signature of methods to manage a given resource such as CPU, RAM or BW of VMs and Hosts.
    
    	- The new `ResourceAbstract` class implements basic behaviours of a `Resource`, managing its capacity, allocated and available amount of resources. New validations were introduced to avoid setting invalid values to the resource, such as negative values or a zero capacity. The class also allows changing the capacity, provided that the new one is greater or equals to the amount of allocated resource. It is also avoided to set an available resource amount greater than the capacity. All methods that try to change the resource capacity or allocated size now returns a boolean to indicate if the operation succeeded or not.
    
    	- The method `isSuitablForVm` of provisioner classes (that is now inside the new `Resource` interface and is called just `isSuitable`) previously changed the current allocated VM resource just to check if it was possible to change the total allocated resource to another value. The method was changed to perform the verification, without actually changing the current allocated resource. It only performs the necessary calculations to find out that.
    
    	- Concrete classes were created for specific resources, namely `Ram` and `Bandwidth` classes (the already existing `HarddriveStorage` and `SanStorage` classes now implement `FileStorage` interface that in turn extends the new `ResourceInformation` interface). They hide the abstract type of the `Resource` (if `Double`, `Integer`, etc), facilitating the class use and ensuring that the same type is always used for a given resource (`Bandwidth` always use `Long`, `Ram` always use `Integer`, `FileStorage` always use `Long`). It also standardize the type of each resource, avoiding some inconsistencies found throughout the code.
    
    	- Now that each kind of resource uses a specific generic class, the `Vm` class had to conform with these new changes, in order to get the object that represents a specific resource of the VM (such as `Ram` or `Bandwidth`) from the class of such a resource. To implement that, a new `Map` was introduced in the `Vm` class, where: each key is a resource class; each value is the object that represents such a VM resource. By this way, the new method `Vm.getResource` is used by classes such as the `ResourceProvisionerSimple` to get information of a given resource from the `Vm`. For instance, if a `ResourceProvisioner` manages allocation of `Ram` resource, the class uses the getResource method on the `Vm` class to get the information about the `Ram` object assigned to the VM. Despite these changes, the public interface of the Vm class was not changed.
    
    	- **CHANGES IN PUBLIC INTERFACE OF A CLASS**: Now, when instantiating a `Host`, it doesn’t have to be used a different `ResourceProvisioner` class for each resource (at least if you don’t want to). It only has to be passed a different instance of a `ResourceRrovisioner` for each `Host` resource. Instead of each provisioner instance receiving the resource capacity, now each one has to receive an instance of a `Resource`, that in turn receives the resource capacity. So, the `Provisioner` interface changed to receive a `Resource` object, that will store all information about itself (such as its capacity, the amount of free resource, etc). Below it is shown a example of how to instantiate a `Host`, before and after the performed changes:
    		- **Before changes** : `new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), new VmSchedulerTimeShared(peList));`
    		- **After changes**: `new Host(hostId, new ResourceProvisionerSimple<>(new Ram(ram)), new ResourceProvisionerSimple<>(new Bandwidth(bw)), storage, peList, new VmSchedulerTimeShared(peList);`
    
    	- Finally, the classes `RamProvisioner`, `RamProvisionerSimple`, `BwProvisioner` and `BwProvisionerSimple` were deleted and the new previous mentioned classes and interfaces were introduced (PeProvisioner and PeProvisionerSimple are working in progress yet) . Now, the `ResourceProvisionerSimple` is the only concrete class that you have to use in order to instantiate a provisioner for `Ram`, `Bandwidth` or a `FileStorage` (such as a `HarddriveStorage`) resource object.
        
    - Several refactoring on classes that controls storage space, in order to remove code duplication, improve class hierarchy and reduce bugs probabilities (once that duplicated code doesn't have to be tested several times in different classes). The major part of duplicated code was related to dealing with storage capacity, used space and available space. As the introduced `Resource` interface and related classes implement these features, there isn't duplicated code for that anymore.
    	- The interface `Storage` was splitted into two interfaces: `FileStorage` and `ResourceInformation`. They were created in order to improve class hierarchy and reduce code duplication (that sometimes occurred even between methods of different interfaces, causing the same method to be implemented several times).
    	- The interface `Storage` is now called `FileStorage` in order to differentiate it from the new class `RawStorage`. The first one has methods to perform file system operations (such as add and delete a file). The last one is a raw storage that doesn't keep track of files, but just manages the  capacity, used space and free space. For instance, the `Host` and `Vm` classes just control their storage capacity and available space. Accordingly, they just use a storage attribute (an instance of the previously mentioned `Resource` interface) to manage this storage data.
    	- The `HarddriveStorage` class now implements the `FileStorage` interface.
    	- **CHANGES IN PUBLIC INTERFACE OF A CLASS**: With the new `FileStorage` interface, the public interface of the `Datacenter` class changed. Now, to instantiate a `Datacenter` it has to be passed a `List<FileStorage>` instead of `List<Storage>`.
    	- The classes `HarddriveStorage` and `SanStorage` were moved to the new package `org.cloudbus.cloudsim.resources`, including the `FileStorage` interface.
    	- **CHANGES IN PUBLIC INTERFACE OF A CLASS**: The public interface of the `Datacenter` class and all its subclasses was changed due to renaming the `Storage` interface to FileStorage. Now the constructor of these classes has to receive a `List<FileStorage>` instead of `List<Storage>`.
    	- The "double storage" attribute of the `Host` class originally was being decreased every time a new `Vm` was placed at the host, instead of managing the current allocated storage (as it is being made for other resources such as `Ram` and `Bandwidth`). Now, the `Host` uses an internal `RawStorage` object to manage the capacity and allocated space. The method `getStorage` now always return the actual host storage capacity, not the remaining space. The data returned by the method changed, however, it was verified that just the `Host` itself was calling this public method. Thus, the method was renamed to `getStorageCapacity` (and the setter to `setStorageCapacity`) to avoid confusion. A new protected method `getStorage` was introduced to return the `RawStorage` object.
    	- The `HarddriveStorage` class had an issue when calling the method `addReservedFile` without priorly reserving space for the file by calling the `reserveSpace` method. Consider the situation where the allocated space is 0. When adding a reserved file of size 50 without reserving the space, the method `addReservedFile` reduced the allocated space to -50. After adding the file it increased the allocated space to 0 again. However, the allocated space couldn't be zero once a file (reserved or not) was added. Other issue occurred when reserving a spacing and adding a file of size different from the reserved. These issues were corrected by introducing the `reservedStorage` attribute to control the reserved space. Several unit tests were included to test the class.
    	- The parameters of the method `File.deleteFile(final String fileName, File file)` were not being used as defined in the method documentation. It was supposed to pass the name of the file to be removed and the `file` parameter should return the removed `File` object. By this way, it was supposed to pass even null to the `file` parameter and get a `File` instance after the method call. However, Java passes object references by value (not by reference), what means that if the reference to the `file` parameter is changed inside the method, the reference of the original object outside the method is not updated. Such a Java behaviour makes impossible to the method work as proposed. By this way, the method was removed.
    	- Removal of duplicated `fileSize` and `fileName` attributes at `FileAttribute` class. Now `FileAttribute` class has a relationship with `File`.
    



- Added the interface TableBuilder and classes AbstractTableBuilder, TextTableBuilder, CsvTableBuilder and HtmlTableBuilder the org.cloudbus.cloudsim.util package @2782a408a2f35d8c167feab8096b5921e41308ee
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



#Integration Tests @4bc726a7bcbe01a727459737b018fce76e4df89f
- Configuration of the pom.xml file of the cloudsim project to perform execution of Functional/Integration Tests
    in order to test entire simulation scenarios instead of just isolated units.
    With the integration tests, it is safer to perform changes in the code and make sure that
    the changes don't brake anything. For instance, the created Integration Test called `VmCreationFailureIntegrationTest`,
    verifies if:
    - a given VM failed to be created due to lack of host resources;
    - the time a host is allocated or deallocated to a given VM; the start;
    - finish and execution time of different cloudlets using a specific `CloudletScheduler`.
- It was used the already included maven-surefire-plugin instead of the maven-failsafe-plugin to run
    these new tests. By this way, it is possible to see the Integration/Functional tests results directly at the NetBeans JUnit
    graphical test results interface.
- Functional/Integration Tests have to be included in the package `org.cloudbus.cloudsim.IntegrationTests`
    (as configured in the maven profiles).
- To run all tests at NetBeans, including the Functional/Integration ones, you can right click on the project root,
    select Custom >> integration-tests (that is configured in the nbactions.xml).
    To run in other IDE's, you can see the maven parameters at the nbactions.xml file.

New Features
------------
- Inclusion of the interface `EventListener` in order to provide event notification features applications running CloudSim simulations.
    These notifications can be about the change in state of CloudSim entities.
- The package `org.cloudbus.cloudsim.listeners` was introduced to place the classes and interfaces related to event listeners.
- First listeners were included in the `Vm` class. The following listeners attributes were introduced to allow CloudSim users
    to set listeners to receive notifications about Vm state changes:
    - `onHostAllocationListener`: gets notified when a Host is allocated to a Vm
    - `onHostDeallocationListener`: gets notified when a Host is deallocated to a Vm
    - `setOnVmCreationFailureListener:` gets notified when a Vm fail being placed at a Host due to lack of resources
- The inclusion of the Vm listeners doesn't change the way VMs are instantiated.
- The `EventListener` interface implements the Null Object Design Pattern in order to avoid `NullPointerException` when a
    EventListener is not set.
    In order to ensure that, listener properties are being initialized with the `Listener.NULL` mock object.
- These listeners allow CloudSim users to perform specific tasks when different events happen
    and enable implementation of comprehensive functional and integration tests.
    Examples of these new features can be seen in the `VmCreationFailureIntegrationTest` Integration Test class.
- Inclusion of the package `org.cloudbus.cloudsim.builders` time classes and interfaces that implement the Builder Design Pattern
    in order to help creating CloudSim objects, such as `Host`, `Datacenter`, `DatacenterBroker`, `Vm` and `Cloudlet`.
    The creation of these objects requires a lot of parameters and some of them have to be created before other ones
    (e.g: Hosts have to be created before a `Datacenter`).
    The constructor of these objects are complex and the creation of multiple objects
    of the same class is a repetitive task that can lead to code redundancy.
- The builder classes were introduced to reduce complexity when creating these objects.
    They help setting the values to be used when create such objects, by providing default values for each object property.
    Once these values are set, just one method needs to be called to create as many copies of the object as desired.
    For instance, if you want to create 2 hosts with the same configuration,
    you just have to set the host attributes first and then, call the `createHosts(int amount)` method of the `HostBuilder` class
    in order to create the amount of Hosts you want.
- The new builders use the Decorator Design Pattern in order to ensure a given object creation flow.
    For instance, VMs and Cloudlets have to be submitted to a given `DatacenterBroker`.
    By this way, just after calling the `createBroker()` method at the `BrokerBuilder` class that is possible to make a chained call to the
    `getVmBuilderForTheCreatedBroker().createAndSubmitVms(int amount)`.
    This method allows the creation of VM and submission to the broker just created.
    For instance, you can't make the call `brokerBuilder.getVmBuilderForTheCreatedBroker().createAndSubmitVms(2)`.
    You have to call `brokerBuilder.createBroker().getVmBuilderForTheCreatedBroker().createAndSubmitVms(2)`
    that ensure that VMs are created only after creating a broker.
- An entire example of the use of these builder can be seen at the `VmCreationFailureIntegrationTest` Integration Test class.
- The builders can be used by Unit and Integration Tests and by users of CloudSim that want to creation cloud simulations.

commit 6b177ab10b518f6aaf4c29190c7696b4130ade92
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Mar 11 09:56:04 2016 +0000

    Added new TODOs.

commit 873ba73b105830db570f029ad8b1b576a6a638d1
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Mar 16 13:12:15 2016 +0000

    Corrected the value of the const org.cloudbus.cloudsim.Consts.WEEK that the values was duplicated from the DAY constant.

commit 40c7b81f0439460e66694271467a108568ab630b
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Mar 16 13:51:46 2016 +0000

    - Update CloudSimExample7 moving the anonymous Thread class to a specific class named MonitorThread,  outside the CloudSimExample7 class, but in the same file.
    - Included the attribute DatacenterBroker broker inside the  MonitorThread class in order to get the broker dynamically created inside the Thread.
    -Printed the list of cloudlets submitted to the broker created by the MonitorThread.

commit 0f7a50f9d64b56b3909e431efa84609348166f43
Merge: 873ba73 40c7b81
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Mar 16 15:39:08 2016 +0000

    Merge master into reduce_code_duplication
    
    Conflicts:
    	.gitignore
    	modules/cloudsim-examples/src/main/java/org/cloudbus/cloudsim/examples/CloudSimExample7.java
    	modules/cloudsim-examples/src/main/java/org/cloudbus/cloudsim/examples/CloudSimExample8.java

commit 7e20838c2a323a5fd01e222dd9a8831830083d8e
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Mar 16 20:14:54 2016 +0000

    - Update the CloudSim class to include the EventListener<CloudSim, SimEvent> eventProcessingListener attribute. It uses the new EventListener interface to notify observer objects when any event of any kind is processed by CloudSim simulator. See the setEventProcessingListener method to define a observer object to get nofied when any event is processed. The inclusion of the listener doesn't break applications using previous CloudSim versions.
    - Due to the use of the new eventProcessingListener at the CloudSim class, it was introduced the method getInstance() that implements the Singleton Design Pattern in order to avoid multiple instances of the CloudSim package. However, CloudSim continues working through its static method calls.
    - Overall refactoring of the VmCreationFailureIntegrationTest class that implements the first CloudSim Integration Test (or maybe Functional Test?).
    - Inclusion of new verifications at the VmCreationFailureIntegrationTest after the introduction of the CloudSim eventProcessingListener. Verifications were moved to specific methods with meaningful names in order to give a comprehensive notion of which verifications are being performed by each method.

commit 824b9196423b12b4d8728cea3359cce02ee7edbc
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Mar 17 04:30:36 2016 +0000

    - Implementation of the Null Object Design Pattern in order to start avoiding null checks and `NullPointerException` when using classes such as `Datacenter`, `Host`, `Vm`, `Cloudlet`, `CloudletScheduler`, `Resource`, `ResourceProvisioner`, `VmAllocationPolicy`, `UtilizationModel`, `VmScheduler`, `Pe`, etc.
    
    - Each interface that implements the Null Object Design Pattern has a public static final attribute named `NULL`, that implements the interface itself, returning: zero for all numeric methods; false for all boolean methods; an empty string for all String methods; an empty list for all List methods; and an empty Map for all Map methods. Taking the `Vm` interface as example, using the new `NULL` attribute defined on it, instead of returning `null` when a method doesn't find a `Vm`, such a method can return `Vm.NULL`. To verify if a valid `Vm` was returned, you have to use `if(!vm.equals(Vm.NULL))` instead of `if(vm == null)`.
    
    - The `NULL` attributes created are broadly used by classes of the `org.cloudbus.cloudsim.builders` package in order to avoid `NullPointerException`.
    
    - A complete new set of interfaces was introduced in order to starting providing a more structured class hierarchy and to reduce code duplication. The classes `Datacenter`, `DatacenterBroker`, `Host`, `PowerHost`, `Pe`, `Vm`, `VmAllocationPolicy`, `VmScheduler`, `Cloudlet` and `CloudletScheduler` (and maybe others) had their names suffixed with the word "Simple", as already has been used in other classes such as `VmAllocationPolicySimple`. Further, they were introduced new interfaces with the same name of the original classes (without the suffix "Simple"), defining the common public methods to be present in each class that implements one of these interfaces. By this way, it was paved the way to start applying the "Liskov Substitution Principle", one of the SOLID principles that say to "program to an interface, not to an implementation".
    
    - With the new set of interfaces, it was started reducing the amount of unchecked `(List<T>)` casts (that raises an warning).
    
    - WARNING: This release breaks compatibility with previous CloudSim versions due to inclusion of the suffix "Simple" in the name of mentioned classes.

commit 7d3b462465b1affefd03839a43014133de927feb
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Mar 19 16:48:01 2016 +0000

    - Changed the uni-directional relationship between Datacenter and DatacenterCharacteristics into a bi-directional one in order to be able to get the Datacenter that owns a given DatacenterCharacteristics object.
    This is being used by the onVmCreationFailureListener attribute of Vm in order to pass the failed Vm and the Datacenter where it was tried to place it.

commit bda537266764d4d0c24ade9ab1fa13c87c959d39
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Mar 19 17:37:54 2016 +0000

    - Updated the VmCreationFailureIntegrationTest Integation Test (IT) to use the new onCloudletFinishEventListener attribute of the Cloudlet to be notified every time when a Cloudlet finishes executing into a given VM. By this way, the  IT verifies if the created cloudlets are finishing at the expected time.

commit 62ef8264316f302f127e617e3532cf6d9d9c5d34
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Mar 19 17:41:52 2016 +0000

    - Updated the CloudletBuilder class to able to set the new onCloudletFinishEventListener attribute of the Cloudlet

commit 4779e5dfe1e0ed87ce35cb4435a61665adbbcd9e
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Mar 19 17:52:23 2016 +0000

    - Updated the CloudletBuilder class to able to set the new onCloudletFinishEventListener attribute of the Cloudlet.
    - Updated the VmCreationFailureIntegrationTest IT to verify if the number of finished cloudlets is as expected.

commit 7349d46ba699844e08aeb93a949cf693337026a3
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Tue Mar 22 10:57:05 2016 +0000

    Just code formatting in some examples

commit 8fcd4de08e5037a408503d759b22dd978cf24f5f
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Mon Mar 28 23:26:22 2016 +0100

    Inclusion of MigrationExample1 class, a simple simulation example that performs VM migration due to under and over host utilization.
    
    - The original examples inside packages org.cloudbus.cloudsim.examples.power.planetlab and
    org.cloudbus.cloudsim.examples.power.random are over complex to allow a novice CloudSim user to understand how to perform VM migration simulations.
    These examples create too much Hosts, VMs and Cloudlets to enable any reasoning about the obtained results. They don't make clear why, how and when the migrations were performed.
    - The include example implements a Worst Fit VmAllocationPolicy that select the first host with the most available CPU capacity to host a VM.
    - It was also included a custom UtilizationModel to implement progressive cloudlet CPU usage along the simulation time. The cloudlet CPU usage changes by Arithmetic Progression, allowing to know exactly how it will be along the time. By this way, it is easy to understand simulation results.

commit c88d60c506f45f1dc1f954f4ad278006ca7695c8
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Mon Mar 28 23:32:31 2016 +0100

    Inclusion of MigrationExample1 class, a simple simulation example that performs VM migration due to under and over host utilization.
    - The original examples inside packages org.cloudbus.cloudsim.examples.power.planetlab and# org.cloudbus.cloudsim.examples.power.random are over complex to
    allow a novice CloudSim user to understand how to perform VM migration simulations. # Changes to be committed: These examples create too much Hosts, VMs and
    Cloudlets to enable any reasoning about the obtained results. They don't make clear why, how and when the migrations were performed.# new file:
    modules/cloudsim-examples/src/main/java/org/cloudbus/cloudsim/examples/migration/MigrationExample1.java - The include example implements a Worst Fit
    VmAllocationPolicy that select the first host with the most available CPU capacity to host a VM. # new file:
    modules/cloudsim-examples/src/main/java/org/cloudbus/cloudsim/examples/migration/NonPowerVmAllocationPolicyMigrationWorstFitStaticThreshold.java
    - It was also included a custom UtilizationModel to implement progressive cloudlet CPU usage along the simulation time. The cloudlet CPU usage changes by Arithmetic Progression, allowing to know exactly how it will be along the time. By this way, it is easy to understand simulation results.

commit 95117f8a07e8318f9220a99b4c586b34f85e82e8
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Tue Mar 29 00:29:38 2016 +0100

    Correction of compilation errors

commit 033c37c1181900dcaba2bf15af16f71cd1a663d2
Merge: 95117f8 c88d60c
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Tue Mar 29 00:52:02 2016 +0100

    Merge origin/master into reduce_code_duplication
    
    Conflicts:
    	modules/cloudsim/src/main/java/org/cloudbus/cloudsim/HostDynamicWorkload.java
    	modules/cloudsim/src/main/java/org/cloudbus/cloudsim/power/PowerVmAllocationPolicyMigrationAbstract.java

commit bc49925e06da65ea9ea3f5ad01a3bdd6453c2122
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Tue Mar 29 01:11:50 2016 +0100

    - Reduction of unnecessary messages when optimizing VM placement. For instance, when there isn't overloaded hosts, the message "Reallocation of VMs from the over-utilized hosts" will not be shown.

commit 18d7794132e497a807a15614ac8f1ca09d8760e9
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Tue Mar 29 01:23:34 2016 +0100

    Just documentation update of MigrationExample1

commit 3fcc1366b3fda1c653a3b2e7455d94b09c90af88
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Tue Mar 29 01:25:15 2016 +0100

    Just documentation update of MigrationExample1

commit 9e789b63d7c551926d00b4597f60702d3bb25e81
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Tue Mar 29 01:45:50 2016 +0100

    Updated MigrationExample1 to use the TableBuilderHelper class to print the cloudlet list

commit a43316e413a998316fbfd7dc9a3c6a31ca156d23
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Mar 31 12:13:27 2016 +0100

    - Changed requirements of the cloudsim project to Java 8 (the examples remain in Java 7)
    - Deleted the ParameterException class and changed its use to the unchecked native InvalidArgumentException
    - All classes that usually have to be extended by CloudSim users in order to provide some specific features for their simulations were moved to new appropriated packages in order to increase class's organization:
    	- Moved all Utilization Model classes and interfaces to new package org.cloudbus.cloudsim.utilizationmodels
    	- Moved all Datacenter Broker classes and interfaces to new package org.cloudbus.cloudsim.brokers
    	- Moved all Cloudlet and Vm Scheduler classes and interfaces to new package org.cloudbus.cloudsim.schedulers
    	- Moved all Vm Allocation Policy classes and interfaces to new package org.cloudbus.cloudsim.allocationpolicies
    	- Moved classes Packet, InfoPacket and NetworkTopology to the network package

commit 7a99278f98c13b6c2e464846033122f03d3f2359
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Mar 31 18:50:52 2016 +0100

    - Usage of Java 8 lambda expressions in VmCreationFailureIntegrationTest class.
    - Simplification of builder's attributes names, removing the word "default" from them

commit cd74743724f3e43765263efc2b5233edb2309621
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Mar 31 23:27:40 2016 +0100

    - Usage of Java 8 lambda expressions in VmCreationFailureIntegrationTest class.
    - Simplification of builder's attributes names, removing the word "default" from them
    - Updated the DatacenterSimple class to use the scheduleInterval to define the interval to update cloudlets processing, in the same way as in PowerDatacenter class

commit e75d1c6bcc9401293b3962f0ecb9ee2b594c4e7f
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Mar 31 23:48:11 2016 +0100

    Included the Integration Test CheckHostAvailableMips. The IT checks if the Host CPU utilization is as expected along the simulation run. However, the test is failing and the involved classes have to be verified further.

commit 8448073525995232c4ed24974938ff564cb59b5a
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 1 01:10:49 2016 +0100

    Fixed several TODOs

commit defe3b727d1e445dc35172b774b031fb3a6a0887
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 1 14:15:34 2016 +0100

    - Introduced a onUpdateVmsProcessingListener on the HostSimple class in order to notify listeners when a host updates its VMs processing
    - Included the new CheckHostAvailableMips Integration Test to check the amount of available CPU during the simulation time for cloudlets of different brokers using an UtilizationModelFull for CPU.

commit 7649adcb95760f9de210c9c0fdb814acab08fa77
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 1 19:59:04 2016 +0100

    - Renamed the new Integration Tests, including the suffix "Test" to allow their automatic execution.

commit 2c1cd09fc18e51d30bbb0b0c5658da499853b882
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 1 21:40:57 2016 +0100

    Changed the return value of the VmAllocationPolicy.optimizeAllocation method from List<Map<String, Object>> that was completely strange and doesn't correctly uses generics. The method return was a List of Maps where the keys were either the strings "vm" or "host". If the key was the string "vm", it meant the value was a Vm instance. If the key was the string "host", it meant the value was a Host instance. Thus, each Map represented the relation between a Vm and the Host where it was to be placed.
    
    This design was very confusing, requiring a lot of explanation and several unsafe typecasts. Now the method (and all related ones that were returning a List<Map<String, Object>>) is returning a Map<Vm, Host>.
    
    The method DatacenterSimple.processVmMigrate that expected the ev.getData() was a Map<String, Object>, now expects a Map.Entry<Vm, Host>, making to code clearer and reducing typecasts.

commit e9fc2bcdb94ea988497c883f8bd547aff4cf4d30
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 1 21:58:06 2016 +0100

    Removação de type casts desnecessários.

commit 0da6a654584a8fd62fae1620b7ecb2f233f22c76
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 1 22:11:50 2016 +0100

    Documentation improvement

commit 1cfe2da0bebe66ac686f8dc9a85550e790607dc6
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 2 10:28:34 2016 +0100

    Updated all sub-projects version numbers to 4.0

commit bd349f595a21afb34c4f755019c90996c6d41d3c
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 2 13:01:56 2016 +0100

    Updated Network classes's documentation and code formatting. Improved name of constants at NetworkConstants class and included documentation.

commit 2f51594e714caf11b25bd3ebb88b8de6c8f4c636
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 2 13:04:29 2016 +0100

    Updated Network classes's documentation and code formatting. Improved name of constants at NetworkConstants class and included documentation.

commit da3272ea36b6af749e1fd544a1d9eeded327c37d
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 2 13:41:24 2016 +0100

    Updated Network classes's documentation and code formatting. Improved name of constants at NetworkConstants class and included documentation.

commit f978a7ead3a1417de71d827c29085585d668f381
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 2 16:04:47 2016 +0100

    - Reduction of documentation duplication
    - All the implementations of the method VmAllocationPolicy.optimizeAllocation are now returning an empty map instead of null when the method in fact doesn't perform any VM placement optimization. This change was performed to reduce null checks and avoid NullPointerException's.

commit 9f1e26bdd6392fae50b35e79c8223f0d10d85857
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 2 16:53:20 2016 +0100

    Refactoring on VmScheduler classes, applying Java 8 streams in order to increase code readability, reduce verbosity and increase performance.

commit 124276f6e273f0093ba221084d2d707a8e62ad6d
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 2 17:41:12 2016 +0100

    Refactoring on CloudletScheduler classes, applying Java 8 streams in order to increase code readability, reduce verbosity and increase performance.

commit d7043e521dd3f8faa9246d4232621645e822ae84
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sun Apr 3 12:25:34 2016 +0100

    Moved the duplicated methods updateVmProcessing, getNextFinishedCloudlet, areThereFinishedCloudlets, getTotalUtilizationOfCpu, cloudletCancel, cloudletStatus, cloudletPause, cloudletSubmit(Cloudlet cloudlet) and other ones from
    CloudletSchedulerSpaceShared and CloudletSchedulerTimeShared to CloudletSchedulerAbstract class.
    
    Some of these methods were exactly equal. Other ones such as the updateVmProcessing, cloudletCancel and cloudletPause at the CloudletSchedulerSpaceShared class were just looking at the list of waiting cloudlets.
    However, this list can be just empty in the CloudletSchedulerTimeShared class,
    since all cloudlets will share the processor time (not waiting to a previous cloudlet completely finishes executing after it can start running).
    By this way, moving the methods to the super class doesn't change the behaviour of these different schedulers. Other subclasses in fact completely override methods such as updateVmProcessing.
    
    By this way, now it is easier to extend and test one of these classes, since a bunch of duplicated code was removed, increasing code reuse.

commit d3306585d50a11156f61828ac7f0d44bf982ab7a
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sun Apr 3 15:34:18 2016 +0100

    Added .travis.yml file to enable project's continuous integration, building and testing it at the Travis Service after every commit

commit 9177f65fd59356464137991fe0cb22abfc2f7714
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sun Apr 3 15:48:01 2016 +0100

    Ignored the CheckHostAvailableMipsDynamicUtilizationTest Integration Test because
    it has to be assessed only schedulers such as CloudletSchedulerDynamicWorkload
    are able to dynamically update VM CPU usage accordingly to the actual
    cloudlets usage. The test is using such scheduler but with the regular Vm, Host and Datacenter, the results are not as expected.
    Even the VM is not using all its reserved CPU capacity, all the required capacity
    is taken from the Host as if they are really being used all the time.

commit d610075e680ff96baecbe145dc5f79fd13055900
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Mon Apr 4 00:19:57 2016 +0100

    Addition of new test cases

commit 5d3f300bab607a9903460e1f020e438684230ad4
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Mon Apr 4 00:31:23 2016 +0100

    Refactoring in the VmScheduler classes

commit 004bbf9e0e7164d05ef05083ef1e4740c67203e5
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 6 11:22:32 2016 +0100

    New unit tests for CloudletList

commit 08b8aed68cf430d47b8c639a47113d317c053698
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 6 13:21:45 2016 +0100

    New unit tests for HostList

commit 80c7b27e505eeec4046105697d6ca33fed18da81
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 6 17:41:34 2016 +0100

    New unit tests for PowerVmList

commit 27793552bb0a06e1a010895099b4406ef6c82371
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 6 22:26:24 2016 +0100

    Addition of several unit tests

commit 93d5b0e56739c17ef68d28bf9b410f744c20c80a
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Apr 7 08:57:20 2016 +0100

    - Created unit tests for PowerVmAllocationPolicySimple class.
    - Included NULL object for PowerHost interface.
    - Validated hostList for VmAllocationPolicyAbstract constructor

commit 4b025ad10e9f8170cff4bab830ca23180a1d0eb3
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Apr 7 09:21:47 2016 +0100

    Created HostTest test class.

commit b536c14e0ebcd84982d74e543d5e4ed97dbef1df
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Apr 7 10:17:07 2016 +0100

    Added VmTest test class

commit 0bd86c61b46b8ab3864740cd58d0ac74e5b10483
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Thu Apr 7 19:10:51 2016 +0100

    New unit test classes. Refactoring of several unit tests.

commit 76fc2ce7cde536b49af63d2021ccf6e4223e6f7a
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 8 11:24:37 2016 +0100

    Corrections at the new Processor class

commit 50db4938b9ead244d05641e0d73a3816ad53dffa
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 8 13:33:46 2016 +0100

    Refactored the Resource related interfaces to apply the Interface Segregation Principle (ISP) to avoid classes to have methods that they in fact don't need.

commit 35f02b0a21a71d5d14ae1111da6c80f98e8af3fa
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 8 19:43:14 2016 +0100

    Minor tests refactoring

commit 1b84eb6b5e6490f54da6bf53b385fde5bf385cd6
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 8 20:51:19 2016 +0100

    - Minor tests refactoring
    - New minor test cases
    - Changed the Vm.addStateHistoryEntry method to receive a VmStateHistoryEntry object instead of a
      separated value for each of its attributes, providing a more OO solution

commit 4a19e109e7156002ea51de691e75e801c04e5e00
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 8 23:06:05 2016 +0100

    - Added new test cases for HostSimpleTest class
    - Renamed the methods getBw and getRam to getBwCapacity and getRamCapacity

commit fc4d1e5da9b631f4aab7ab54b518b02d8f95803c
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 9 00:59:10 2016 +0100

    Code organization and refactoring of network packet classes

commit 3e422f8171f0a5e3e63a8af077dc4dea9ac4d48c
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 9 03:07:57 2016 +0100

    Several refactorings in network related classes

commit d9128e3f5f779f6013b7c1bafefebdb129092a59
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Sat Apr 9 03:30:37 2016 +0100

    Refactoring of Switch classes in order to move constants to the Switch class an remove unnecessary parameters from constructors

commit 54c3da6fab0fcaf7e32a619b862863c2a03b5be6
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 13 09:45:33 2016 +0100

    THE FIRST CRITICAL CODE REMAKE: UPDATE OF NETWORK RELATED CLASSES
    -----------------------------------------------------------------
    
    The classes related to the examples at the org.cloudbus.cloudsim.network.datacenter package of the
    cloudsim-examples project were almost totally remade.
    
    The network related classes such as AppCloudlet, NetworkCloudlet, NetDatacenterBroker had an extremely amount of duplicated code.
    The NetDatacenterBroker was a copy of the original DatacenterBroker class, instead of extending it and overriding
    the necessary methods. It was noticed that a lot of code in the created class was exactly equals to the DatacenterBroker,
    what shows that such methods should be inherited from the already existing class.
    
    The code that in fact belonged only to the NetDatacenterBroker class was completely repetitive and confuse.
    There were very long methods that should be divided into smaller and specific ones in order to avoid
    code redundancy and make clear what the code does.
    
    The class used a lot of hard-coded values for creating objects such as Cloudlets and VMs,
    that doesn't make sense once these values have to be defined by the developer creating
    the simulation. It was clear that the values were defined just to perform the simulations
    presented at the paper "NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations",
    <http://dx.doi.org/10.1109/UCC.2011.24>. By this way, they aren't useful for other users
    that desire to create their own simulations. Further, the creation of hard-coded Cloudlets and VMs inside
    the NetDatacenterBroker doesn't make it to be reusable and even violates the **Open/Closed Principle (OCP)**.
    
    Once the mentioned paper doesn't provide low level details about how every class's method works (and it is not supposed that
    a paper will present such in deep code details) and that there weren't any unit test for the network related classes,
    it was not even feasible to know if the classes and examples were working correctly.
    The examples didn't show any useful information, just some vague messages such as "App1, App2, ... AppN" to inform the creation of AppCloudlets,
    not providing actual information. In face of that, the best to do was to perform a in-depth refactoring of the classes and examples
    in order to provide a actual class hierarchy, eliminate code redundancy and provide short and meaningful methods
    that makes clear to understand the code and to just further (unfortunately) introduce test units.
    
    The classes WorkflowApp and BagOfTasksAppCloudlet were deleted because they in fact were just creating hard-coded VMs and Cloudlets
    and they weren't providing any specific code. In replacement, they were created the NetworkVmsExampleWorkflowAppCloudlet and NetworkVmsExampleBagOfTasksAppCloudlet classes that just instantiate regular AppCloudlet and NetworkCloudlet classes with specific values in order to provide the desired kind of tasks to be simulated.
    
    The class NetworkConstants was deleted because all of its values were just values used by the examples, and were accordingly moved to the
    classes that they belong. Some constants included in this class were just values to be used by Switch related classes. Placing these constants outside the classes that in fact  use them makes the classes design low cohesive. Accordingly, to increase the cohesion, making easy to understand how the elements are related, the constants were moved to the appropriated classes.

commit de4963c6b0c44ad9015c5bda9a68863826317ce4
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 13 13:59:39 2016 +0100

    Added the SwfWorkloadFormatExample1 example that show how to use the WorkloadFileReader to created cloudlets from a trace in the
    Standard Workload Format from the Hebrew University of Jerusalem.
    The example uses a new DatacenterBroker class created to allow submittion of VMs to a datacenter, ordering VMs descendingly, according
    to their number of PEs. By this way, VMs requiring more PEs will be submitted first for creation inside a datacenter's host.

commit 13d3c2f2d301cdd596475c475fe64f3d4e4d4e7f
Merge: 54c3da6 6b8db25
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 13 19:33:20 2016 +0100

    Closes pull request #2

commit 98b166de9b5eae151d56a7c5ba8c9f1490de6915
Merge: 13d3c2f de4963c
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 13 19:35:47 2016 +0100

    Merge master into reduce_code_duplication

commit e4c75bcd068c616aff057802495a6f4f908ad0c3
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 13 20:10:55 2016 +0100

    Inclusion and update of the new SwfWorkloadFormatExample1 example from master branch

commit 47ba8edd68dc6c8d57491967ee42a636fe5eb5cc
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 13 21:47:24 2016 +0100

    Refactored the WorkloadFileReader to remove code duplication. Included new test cases for gz, zip and swf workload files.

commit a8f985fdc90e5785980648d7d6fe2dce4dceb789
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Wed Apr 13 22:27:15 2016 +0100

    Refactoring in the WorkloadFileReaderTest

commit 938de9c67f7818da2dec444dcbbf5736782edeab
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 15 11:32:10 2016 +0100

    Updated README.md

commit 96083523ab26322ccc4b7749307a2656313f2b84
Author: Manoel Campos <manoelcampos@gmail.com>
Date:   Fri Apr 15 11:38:41 2016 +0100

    Updated README.md
