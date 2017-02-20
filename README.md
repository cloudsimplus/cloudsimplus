<a id="top"></a>

[![Build Status](https://img.shields.io/travis/manoelcampos/cloudsim-plus/master.svg)](https://travis-ci.org/manoelcampos/cloudsim-plus) [![Dependency Status](https://www.versioneye.com/user/projects/587a137d2ef9ab000eff9d41/badge.svg?style=rounded-square)](https://www.versioneye.com/user/projects/587a137d2ef9ab000eff9d41) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/app/manoelcampos/cloudsim-plus?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=manoelcampos/cloudsim-plus&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/app/manoelcampos/cloudsim-plus?utm_source=github.com&utm_medium=referral&utm_content=manoelcampos/cloudsim-plus&utm_campaign=Badge_Coverage) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cloudsimplus/cloudsim-plus/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.cloudsimplus/cloudsim-plus) [![Documentation Status](https://readthedocs.org/projects/cloudsimplus/badge/?version=latest)](http://cloudsimplus.rtfd.io/en/latest/?badge=latest) [![GPL licensed](https://img.shields.io/badge/license-GPL-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

<p align="center">
<b><a href="#overview">Overview</a></b>
|
<b><a href="#exclusive-features">Exclusive Features</a></b>
|
<b><a href="#projects-modules">Modules</a></b>
|
<b><a href="#how-to-use-cloudsim-plus">How to use</a></b>
|
<b><a href="#a-minimal-and-complete-simulation-example">Examples</a></b>
|
<b><a href="#documentation-and-help">Docs and Help</a></b>
|
<b><a href="#why-care">Why should I care?</a></b>
|
<b><a href="#why-another-fork">Why another fork?</a></b>
|
<b><a href="#differences">Differences from CloudSim</a></b>
|
<b><a href="#general-features-of-the-simulator">General Features</a></b>
|
<b><a href="#publications">Publications</a></b>
|
<b><a href="#license">License</a></b>
|
<b><a href="#contributing">Contribution Guide</a></b>
</p>

# Overview

CloudSim Plus is a full-featured, highly extensible simulation framework that enables modeling, simulation, and experimentation of emerging Cloud computing infrastructures and application services. It allows its users to focus on specific system design issues that they want to investigate, without getting concerned about the low level details related to Cloud-based infrastructures and services.
 
Cloud computing is the leading technology for delivery of reliable, secure, fault-tolerant, sustainable, and scalable computational services. For assurance of such characteristics in cloud systems under development, it is required timely, repeatable, and controllable methodologies for evaluation of new cloud applications and policies, before actual development of cloud products. Because utilization of real testbeds limits the experiments to the scale of the testbed and makes the reproduction of results cumbersome, computer-base simulation may constitute an interesting tool. This project is a suitable tool to rapidly develop such simulation scenarios and run them quickly, in a typical PC. 

CloudSim Plus is a fork of [CloudSim 3](https://github.com/Cloudslab/cloudsim/tree/20a7a55e537181489fcb9a2d42a3e539db6c0318) that was re-engineered primarily to avoid code duplication. It provides [code reusability](https://en.wikipedia.org/wiki/Code_reuse) and ensures compliance with software engineering principles and recommendations for extensibility improvements. It focuses on usage of software engineering standards and recommendations such as [Design Patterns](https://en.wikipedia.org/wiki/Software_design_pattern), [SOLID principles](https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)) and other ones such as [KISS](https://en.wikipedia.org/wiki/KISS_principle) and [DRY](https://pt.wikipedia.org/wiki/Don't_repeat_yourself).

CloudSim Plus is developed through a partnership among the Systems, Security and Image Communication Lab of [Instituto de Telecomunicações (IT, Portugal)](http://www.it.pt), the [Universidade da Beira Interior (UBI, Portugal)](http://www.ubi.pt) and the [Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil)](http://www.ifto.edu.br). It is supported by the Portuguese [Fundação para a Ciência e a Tecnologia (FCT)](https://www.fct.pt) and by the [Brazilian foundation Coordenação de Aperfeiçoamento de Pessoal de Nível Superior (CAPES)](http://www.capes.gov.br).

The original [CloudSim](http://github.com/Cloudslab/cloudsim) project is developed in [the Cloud Computing and Distributed Systems (CLOUDS) Laboratory](http://cloudbus.org/), at [the Computer Science and Software Engineering Department](http://www.csse.unimelb.edu.au/) of [the University of Melbourne](http://www.unimelb.edu.au/).

<p align="right"><a href="#top">:arrow_up:</a></p>

# Exclusive Features

CloudSim Plus provides a lot of exclusive features, ranging from the most basic ones that enable building simple simulations, to advanced features for implementing more realistic simulation scenarios: 

1. It is easier to use. A complete and easy-to-understand simulation scenario can be built in some few lines. Check the [Examples Section](#a-minimal-and-complete-simulation-example);
1. [Vertical VM Scaling](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/VerticalVmScalingExample.java) 
  that performs on-demand up and down allocation of VM resources such as Ram, Bandwidth and PEs (CPUs);
1. [Horizontal VM scaling](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/LoadBalancerByHorizontalVmScalingExample.java), allowing dynamic creation of VMs according to an overload condition. Such a condition is defined by a predicate that can check different VM resources usage such as CPU, RAM or BW;
1. [Parallel execution of simulations](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/ParallelSimulationsExample.java), allowing several simulations to be run simultaneously, in a isolated way, inside a multi-core computer;
1. A [Functional](https://en.wikipedia.org/wiki/Functional_programming) `DatacenterBrokerSimple` class that enables changing, at runtime, the policies to select: a Datacenter to place waiting VMs; a fallback Datacenter when a previous selected one fails in finding a suitable Host for a VM; and a VM to run each Cloudlet. This dynamic behavior allows implementing specific policies, without requiring the creation of new `DatacenterBroker` classes;
1. [Delay creation of submitted Cloudlets](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/DynamicCloudletsArrival1.java), enabling simulation of dynamic arrival of tasks (see issue [#11](https://github.com/manoelcampos/cloudsim-plus/issues/11));
1. [Allow dynamic creation of VMs and Cloudlets without requiring creation of Datacenter Brokers at runtime](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/DynamicCreationOfVmsAndCloudletsExample.java), enabling VMs to be created on-demand according to arrived cloudlets (see issue [#43](https://github.com/manoelcampos/cloudsim-plus/issues/43));
1. [Listeners](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/listeners/) to enable simulation monitoring and creation of VMs and Cloudlets at runtime;
1. [Builders](/cloudsim-plus/src/main/java/org/cloudsimplus/builders/) to enable creating multiple simulation objects with same configuration;
1. It is a strongly object-oriented framework that creates relationships among classes and allows chained calls such as `cloudlet.getVm().getHost().getDatacenter()`. And guess what? You don't even have to worry about `NullPointerException` when making such a chained call because CloudSim Plus uses the [Null Object Design Pattern](https://en.wikipedia.org/wiki/Null_Object_pattern) to avoid that;
1. Classes and interfaces to allow implementation of [heuristics](http://en.wikipedia.org/wiki/Heuristic) such as [Tabu Search](http://en.wikipedia.org/wiki/Tabu_search), [Simulated Annealing](http://en.wikipedia.org/wiki/Simulated_annealing), [Ant Colony Systems](http://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms) and so on. See an [example using Simulated Annealing here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/DatacenterBrokerHeuristicExample.java).
1. [Implementation of the Completely Fair Scheduler](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/LinuxCompletelyFairSchedulerExample.java) used in recent versions of the Linux Kernel;
1. Completely re-designed and reusable Network module. Totally refactored network examples to make them clear and easy to change (see issue [#49](https://github.com/manoelcampos/cloudsim-plus/issues/49));
1. Simpler constructors to instantiate simulation objects, making it less confusing to use the framework. It applies the Convention over Configuration principle (CoC) to ask just mandatory parameters when instantiating objects (see issue [#30](https://github.com/manoelcampos/cloudsim-plus/issues/30));
1. TableBuilder objects that are used in all examples and enable printing simulation results in different formats such as ASCII Table, CSV or HTML. It shows simulation results in perfectly aligned tables, including data units and additional data. See the last line of the [BasicFirstExample](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/BasicFirstExample.java) constructor to see how it is easy to print results;
1. Throughout documentation update, improvement and extension;
1. Improved class hierarchy, modules and package structure that is easier to understand and follows the Separation of Concerns principle (SoC);
1. As it is usual to extend framework classes to provide some specific behaviors for your simulations, you will find a totally refactored code that follows clean code programming, [SOLID](https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)), [Design Patterns](https://en.wikipedia.org/wiki/Software_design_pattern) and several other software engineering principles and practices. By this way, it will be easier to understand the code and implement the feature you want;
1. Integration Tests to increase framework accuracy by testing entire simulation scenarios;
1. Updated to Java 8, making extensive use of [Lambda Expressions](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html) and [Streams API](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html) to improve efficiency and provide a cleaner and easier-to-maintain code.

# Project's Modules

CloudSim Plus has a simple structure to ease usage and comprehension. It consists of 4 modules, 2 of which are new, as presented below.

- [cloudsim-plus](/cloudsim-plus): the CloudSim Plus cloud simulation framework API that is used by all other modules. 
  It is the main module that contains the simulation framework implementation and is the only
  one you need to write your cloud simulations. 
- [cloudsim-plus-examples](/cloudsim-plus-examples): includes a series of different examples, since minimal simulation scenarios using basic 
  CloudSim Plus features, to complex scenarios using workloads from trace files or Vm migration examples. This is an excellent starting point for learning how to build cloud simulations using CloudSim Plus.
- [cloudsim-plus-testbeds](/cloudsim-plus-testbeds): a new module that implements some simulation testbeds in a repeatable manner, 
  allowing a researcher to execute several simulation runs for a given experiment and collect statistical data using a scientific approach. 
  It represents real testbeds implemented to assess CloudSim Plus features, providing relevant results. The module provides a set of class which 
  can be used by other researchers to implement their own comprehensive testbeds. Different from the examples module that aims just
  to show how to use CloudSim Plus features, this module includes more complex simulation scenarios concerned in providing
  scientifically valid results. 
- [cloudsim-plus-benchmarks](/cloudsim-plus-benchmarks): a new module used just internally to implement micro benchmarks using the 
  [Java Microbenchmark Harness framework (JMH)](http://openjdk.java.net/projects/code-tools/jmh/) to enable measuring critical methods of the 
  CloudSim Plus API that have a high impact in the simulation framework performance.

<p align="right"><a href="#top">:arrow_up:</a></p>


# How to Use CloudSim Plus 
There are 3 ways to use CloudSim Plus. It can be downloaded and executed directly from some IDE or from the command line. Since it is a Maven project available at [Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cloudsimplus/cloudsim-plus), you can also include it as a dependency inside your own project.

You can watch the video below ([high quality version here](https://youtu.be/hvFJtvrkCNI)) or follow the instructions in one of the next subsections.

![Downloading CloudSim Plus and running Examples using NetBeans](https://github.com/manoelcampos/cloudsim-plus/raw/master/cloudsim-plus-netbeans.gif)

## Via Command Line
Considering that you have [git](https://git-scm.com) and [maven](http://maven.apache.org) installed on your operating system, 
download the project source by cloning the repository issuing the command `git clone https://github.com/manoelcampos/cloudsim-plus.git` 
at a terminal. 

The project has a [bash script](script/bootstrap.sh) that you can use to build and run CloudSim Plus examples. 
This is a script for Unix-like system such as Linux, FreeBDS and Mac OSX.

To run some example type the command: `sh script/bootstrap.sh package.ExampleClassName`.
For instance, to run the CloudSimExample0 you can type: `sh script/bootstrap.sh org.cloudbus.cloudsim.examples.CloudSimExample0`. 

The script checks if it is required to build the project, using maven in this case, making sure to download all dependencies. 
To see what examples are available, just navigate through the [examples directory](/cloudsim-plus-examples/src/main/java/).
To see more script options, run it without any parameter.  
 
## By Means of an IDE
The easiest way to use the project is relying on some IDE such as [NetBeans](http://netbeans.org), [Eclipse](http://eclipse.org) 
or [IntelliJ IDEA](http://jetbrains.com/idea/).
Below are the steps to start using the project:

- Download the project sources using the download button on top of this page or clone it using `git clone https://github.com/manoelcampos/cloudsim-plus.git` 
at a terminal.
- Open/import the project in your IDE:
    - For NetBeans, just use the "Open project" menu and select the directory where the project was downloaded/cloned.
    - For Eclipse or IntelliJ IDEA, 
      you have to import the project selecting the folder where the project was cloned. 
- Inside the opened/imported project you will have the cloudsim-plus and cloudsim-plus-examples modules. 
  The cloudsim-plus module is where the simulator source code is, that usually you don't have to change, unless you want to contribute to the project. 
  The cloudsim-plus-examples is where you can start.
- Open the cloudsim-plus-examples module. The most basic examples are in the root of the org.cloudbus.cloudsim.examples package. 
  You can run any one of the classes in this package to get a specific example. 
- If you want to build your own simulations, the easiest way is to create another class inside this module.

<a id="maven"></a>

## Adding it as a Maven Dependency into Your Own Project

You can add CloudSim Plus API module, that is the only one required to build simulations, as a dependency inside the pom.xml file or your own maven project,
as presened below (check if the informed version is the latest one). By this way you can start building your simulations from scratch.

```xml
<dependency>
    <groupId>org.cloudsimplus</groupId>
    <artifactId>cloudsim-plus</artifactId>
    <version>1.1.0</version>
</dependency>
```

<p align="right"><a href="#top">:arrow_up:</a></p>

# A Minimal and Complete Simulation Example

In order to build a simulation scenario you have to create, at least: 
- a datacenter with a list of physical machines (Hosts); 
- a broker that allows submission of VMs and Cloudlets to be executed, on behalf of a given customer, into the cloud infrastructure; 
- a list of customer's virtual machines (VMs); 
- and a list of customer's cloudlets (objects that model resource requirements of different applications).

Due to the simplicity provided by CloudSim Plus, all the code to create a minimal simulation scenario can be as simple as presented below.
A more adequate and reusable example is available
[here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/BasicFirstExample.java),
together with [other examples](/cloudsim-plus-examples). Specific examples of CloudSim Plus, showing several
new exclusive features and advanced scenarios, can be found [here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/). 

```java
//Creates a CloudSim object to initialize the simulation.
CloudSim cloudsim = new CloudSim();

/*Creates a Broker that will act on behalf of a cloud user (customer).*/
DatacenterBroker broker0 = new DatacenterBrokerSimple(cloudsim);

//Creates a list of Hosts, each host with a specific list of CPU cores (PEs).
List<Host> hostList = new ArrayList<>(1);
List<Pe> hostPes = new ArrayList<>(1);
hostPes.add(new PeSimple(20000, new PeProvisionerSimple()));
Host host0 = new HostSimple(0, 100000, hostPes);
host0.setRamProvisioner(new ResourceProvisionerSimple(new Ram(10000)))
     .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(100000)))
     .setVmScheduler(new VmSchedulerSpaceShared());
hostList.add(host0);

//Creates a Datacenter with a list of Hosts 
DatacenterCharacteristics characts = new DatacenterCharacteristicsSimple(hostList);
VmAllocationPolicy vmAllocationPolicy = new VmAllocationPolicySimple();
Datacenter dc0 = new DatacenterSimple(cloudsim, characts, vmAllocationPolicy);

//Creates VMs to run applications.
List<Vm> vmList = new ArrayList<>(1);
Vm vm0 = new VmSimple(0, 1000, 1);
vm0.setRam(1000).setBw(1000).setSize(1000)
   .setBroker(broker0)
   .setCloudletScheduler(new CloudletSchedulerSpaceShared());
vmList.add(vm0);

//Creates Cloudlets that represent applications to be run inside a VM.
List<Cloudlet> cloudlets = new ArrayList<>(1);
Cloudlet cloudlet0 = new CloudletSimple(0, 10000, 1);
cloudlet0.setBroker(broker0).setUtilizationModel(new UtilizationModelFull());
cloudlets.add(cloudlet0);
Cloudlet cloudlet1 = new CloudletSimple(1, 10000, 1);
cloudlet1.setBroker(broker0).setUtilizationModel(new UtilizationModelFull());
cloudlets.add(cloudlet1);

broker0.submitVmList(vmList);
broker0.submitCloudletList(cloudlets);

/*Starts the simulation and waits all cloudlets to be executed, automatically
stopping when there is no more events to process.*/
cloudsim.start();

/*Prints results when the simulation is over
(you can use your own code here to print what you want from this cloudlet list)*/
new CloudletsTableBuilderHelper(broker0.getCloudletsFinishedList()).build();
```

The presented results are structured and clear to allow better understanding. For example, the image below shows the output for a simulation with two cloudlets (applications).
![Simulation Results](https://github.com/manoelcampos/cloudsim-plus/raw/master/simulation-results.png)

<p align="right"><a href="#top">:arrow_up:</a></p>

# Documentation and Help
The project documentation originated from CloudSim was entirely updated and extended. 
You can see the javadoc documentation for classes and their elements directly on your IDE.

The documentation is available online at [ReadTheDocs](http://cloudsimplus.rtfd.io/en/latest/?badge=latest), that includes a FAQ and guides.
CloudSim Plus has extended documentation of classes and interfaces and also includes extremely helpful
package documentation that can be viewed directly on your IDE or at the link provided above.
Such a package documentation gives a general overview of the classes used to build a cloud simulation.

A Google Group forum is also available at <https://groups.google.com/group/cloudsim-plus>

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="why-care"></a>

# Why should I care about this CloudSim fork? I just want to build my simulations. :neutral_face:
Well, the design of the tool has a direct impact when you need to extend it to include some feature required for your simulations. 
The simulator provides a set of classes such as `VmSchedulers`s, `CloudletScheduler`s, `VmAllocationPolicy`s, `ResourceProvisioner`s, 
`UtilizationModel`s, `PowerModel`s and `DatacenterBroker`s that implement basic algorithms for different goals. 
For instance, the `VmAllocationPolicySimple` class implements a Worst Fit
policy that selects the PM which less processor cores in use to host a VM and, in fact, it is the only policy available. 

Usually you have to write your own implementations of these classes, such as a Best Fit `VmAllocationPolicy`, 
a resource `UtilizationModel` with an upper threshold or a `DatacenterBroker` that selects the best `Datacenter` to submit a VM.

Considering that, several software engineering principles aim to ease the task of creating new classes to implement those features. 
They also try to avoid forcing you to change core classes of the simulator in order to introduce a feature you need to implement.
Changing these core classes is a bad practice, once you will not be able to automatically update your project to new versions 
of the simulator, without losing your changes or struggling to fix merge conflicts.  

And as we have seen in forums that we've attended, many times users have to perform these changes in core classes 
just to implement some specific features they need. We think those problems are enough reasons that show the need of a new re-engineered version of the simulator.  

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="why-another-fork"></a>

# But why another CloudSim fork? :unamused:
The original CloudSim moved on to a new major release, introducing a completely new set of classes to provide Container as a Service (CaaS) simulations, 
before the changes proposed here being merged to the official repository. This way, all the work performed here was not incorporated to allow this new CaaS module to be developed using this redesigned version.
And unfortunately, there are several months of hard work that would need to be replicated to merge both projects.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="differences"></a>

# What are the practical differences of using CloudSim Plus instead of CloudSim? How can I update my simulations in order to use CloudSim Plus?

To update your simulations to use the CloudSim Plus you have to change the way that some objects are instantiated, because some new interfaces were introduced to follow the "program to an interface, not an implementation" recommendation and also to increase [abstraction](https://en.wikipedia.org/wiki/Abstraction_(software_engineering)). 
These new interfaces were also crucial to implement the [Null Object Pattern](https://en.wikipedia.org/wiki/Null_Object_pattern) to try avoiding `NullPointerException`s.

The classes `Datacenter`, `DatacenterCharacteristics`, `Host`, `Pe`, `Vm` and `Cloudlet` were renamed due to 
the introduction of interfaces with these same names. Now all these classes have a suffix *Simple* 
(as already defined for some previous classes such as `PeProvisionerSimple` and `VmAllocationPolicySimple`). 
For instance, to instantiate a `Cloudlet` you have to execute a code such as:

 ```java
CloudletSimple cloudlet = new CloudletSimple(required, parameters, here);
```   

However, once these interfaces were introduced in order to also enable the creation of different cloudlet classes, 
the recommendation is to declare your object using the interface, not the class: 
 
 ```java
Cloudlet cloudlet = new CloudletSimple(required, parameters, here);
```   

Once the packages were reorganized, you have to adjust them. However, use your IDE to correct the imports for you.

Additionally, the interface `Storage` was renamed to `FileStorage` and its implementations are `SanStorage` and `HarddriveStorage`, that can be used as before. Finally, the way you instantiate a host has changed too. The classes `RamProvisionerSimple` and `BwProvisionerSimple` don't exist anymore. Now you just have the generic class `ResourceProvisionerSimple`. And this class doesn't require a primitive value to define the resource capacity. Instead, it requires an object that implements the new `Resource` interface (such as the `Ram` and `Bandwidth` classes). Instantiating a host should be now similar to:

```java
long ram = 20480; //in MB
long bw = 1000000; //in Megabits/s
long storage = 1000000; //in MB
Host host = new HostSimple(id, storage, pesList);
host.setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
    .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
    .setVmScheduler(new VmSchedulerTimeShared());
``` 

<p align="right"><a href="#top">:arrow_up:</a></p>

# General Features of the Simulator

  * Support for modeling and simulation of large scale Cloud computing data centers.
  * Support for modeling and simulation of virtualized server hosts, with customizable policies for provisioning host resources to virtual machines.
  * Support for modeling and simulation of energy-aware computational resources.
  * Support for modeling and simulation of data center network topologies and message-passing applications.
  * Support for modeling and simulation of federated clouds.
  * Support for dynamic insertion of simulation elements, stop and resume of simulation.
  * Support for user-defined policies for allocation of hosts to virtual machines and policies for allocation of host resources to virtual machines.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="publications"></a>

# CloudSim Plus Publications

  * A paper was approved in a top conference and we are just waiting publication to provide a link to it.
  * Other papers are in writing process.
  
<p align="right"><a href="#top">:arrow_up:</a></p>
  
# License

This project is licensed under [GNU GPLv3](http://www.gnu.org/licenses/gpl-3.0), as defined inside CloudSim 3 source files.

<p align="right"><a href="#top">:arrow_up:</a></p>

# Contributing

You are welcome to contribute to the project. However, make sure to read the [contribution guide](CONTRIBUTING.md) before you start.
If you just want to request a feature or report an issue, feel free to [create a ticket here](https://github.com/manoelcampos/cloudsim-plus/issues). You should just look if the issue/feature you want to report/request hasn't been reported/requested yet. Try checking the existing issues/features and search using some keywords before creating a new ticket. 

<p align="right"><a href="#top">:arrow_up:</a></p>
