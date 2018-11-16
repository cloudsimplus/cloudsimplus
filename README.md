<a id="top"></a>

[![Build Status](https://img.shields.io/travis/manoelcampos/cloudsim-plus/master.svg)](https://travis-ci.org/manoelcampos/cloudsim-plus) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/app/manoelcampos/cloudsim-plus?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=manoelcampos/cloudsim-plus&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/app/manoelcampos/cloudsim-plus?utm_source=github.com&utm_medium=referral&utm_content=manoelcampos/cloudsim-plus&utm_campaign=Badge_Coverage) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cloudsimplus/cloudsim-plus/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.cloudsimplus/cloudsim-plus) [![Documentation Status](https://readthedocs.org/projects/cloudsimplus/badge/?version=latest)](http://cloudsimplus.rtfd.io/en/latest/?badge=latest) 
[![GitHub Closed Issues](https://img.shields.io/github/issues-closed-raw/manoelcampos/cloudsim-plus.svg?style=rounded-square)](http://github.com/manoelcampos/cloudsim-plus/issues) 
[![GPL licensed](https://img.shields.io/badge/license-GPL-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

<p align="center">
<b><a href="#overview">Overview</a></b>
|
<b><a href="#main-exclusive-features">Exclusive Features</a></b>
|
<b><a href="#projects-structure">Structure</a></b>
|
<b><a href="#how-to-use-cloudsim-plus">How to use</a></b>
|
<b><a href="#a-minimal-and-complete-simulation-example">Examples</a></b>
|
<b><a href="#documentation-and-help">Docs and Help</a></b>
|
<b><a href="#why-care">Why should I care?</a></b>
|
<b><a href="#why-another-fork">Why an independent fork?</a></b>
|
<b><a href="#differences">Differences from CloudSim</a></b>
|
<b><a href="#general-features-of-the-simulator">General Features</a></b>
|
<b><a href="#publications">Publications</a></b>
|
<b><a href="#license">License</a></b>
|
<b><a href="#contributing">Contributing</a></b>
</p>

# Overview

CloudSim Plus is a full-featured, highly extensible simulation framework enabling modeling, simulation, and experimentation of Cloud computing infrastructures and application services. It allows users to focus on specific system design issues to be investigated, without concerning the low-level details related to Cloud-based infrastructures and services.
 
Cloud computing is the leading technology for delivery of reliable, secure, fault-tolerant, sustainable, and scalable computational services. For assurance of such characteristics in cloud systems under development, it is required timely, repeatable, and controllable methodologies for evaluation of new cloud applications and policies, before actual development of cloud products. Because utilization of real testbeds limits the experiments to the scale of the testbed and makes the reproduction of results cumbersome, computer-base simulation may constitute an interesting tool. This project is suitable to quickly develop such simulation scenarios and run them quickly, in a typical PC. 

CloudSim Plus is a fork of [CloudSim 3](https://github.com/Cloudslab/cloudsim/tree/20a7a55e537181489fcb9a2d42a3e539db6c0318), re-engineered primarily to avoid code duplication. It provides [code reusability](https://en.wikipedia.org/wiki/Code_reuse) and ensures compliance with software engineering principles and recommendations for extensibility improvements. It focuses on usage of software engineering standards and recommendations such as [Design Patterns](https://en.wikipedia.org/wiki/Software_design_pattern), [SOLID principles](https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)) and other ones such as [KISS](https://en.wikipedia.org/wiki/KISS_principle) and [DRY](https://pt.wikipedia.org/wiki/Don't_repeat_yourself).

The efforts dedicated to this project have been recognized by the [EU/Brasil Cloud FORUM](https://eubrasilcloudforum.eu). A post about CloudSim Plus is available at [this page of the Forum](https://eubrasilcloudforum.eu/en/instituto-federal-de-educação-do-tocantins-brazil-instituto-de-telecomunicações-portugal-and), including a White Paper available in the [Publications Section](#publications).

CloudSim Plus is developed through a partnership among the Systems, Security and Image Communication Lab of [Instituto de Telecomunicações (IT, Portugal)](http://www.it.pt), the [Universidade da Beira Interior (UBI, Portugal)](http://www.ubi.pt) and the [Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil)](http://www.ifto.edu.br). It is supported by the Portuguese [Fundação para a Ciência e a Tecnologia (FCT)](https://www.fct.pt) and by the [Brazilian foundation Coordenação de Aperfeiçoamento de Pessoal de Nível Superior (CAPES)](http://www.capes.gov.br).

**There are different ways you can contribute to CloudSim Plus, as it is shown in the [contribution guide](CONTRIBUTING.md). One easy way is to click on the "Star" button at the top of the project's GitHub page. It cost nothing to you and helps promote the project.**

The original [CloudSim](http://github.com/Cloudslab/cloudsim) project is developed in the [Cloud Computing and Distributed Systems (CLOUDS) Laboratory](http://cloudbus.org/), at the [Computer Science and Software Engineering Department](http://www.csse.unimelb.edu.au/) of the [University of Melbourne](http://www.unimelb.edu.au/).

<p align="right"><a href="#top">:arrow_up:</a></p>

# Main Exclusive Features

CloudSim Plus provides a lot of exclusive features, from the most basic ones to build simple simulations, to advanced features for simulating more realistic cloud scenarios: 

1. It is easier to use. A complete and easy-to-understand simulation scenario can be built in few lines of code. Check the [Examples Section](#a-minimal-and-complete-simulation-example); 
1. Process trace files from [Google Cluster Data](https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md) 
   creating Hosts and Cloudlets (tasks). A script to download the trace files is available at [download-google-cluster-data.sh](script/download-google-cluster-data.sh). Examples are available [here](cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/googletraces) ([#149](https://github.com/manoelcampos/cloudsim-plus/issues/149)).
1. [Vertical VM Scaling](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/autoscaling/VerticalVmCpuScalingExample.java) that performs on-demand up and down allocation of VM resources such as Ram, Bandwidth and PEs (CPUs) ([#7](https://github.com/manoelcampos/cloudsim-plus/issues/7));
1. [Horizontal VM scaling](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/autoscaling/LoadBalancerByHorizontalVmScalingExample.java), allowing dynamic creation of VMs according to an overload condition. Such a condition is defined by a predicate that can check different VM resources usage such as CPU, RAM or BW ([#41](https://github.com/manoelcampos/cloudsim-plus/issues/41));
1. Creation of joint power- and network-aware simulations. Regular classes such as `DatacenterSimple`, `HostSimple` and `VmSimple` now allow power-aware simulations. By using the network version of such classes, simulations that are both power- and network-aware can be created. ([#45](https://github.com/manoelcampos/cloudsim-plus/issues/45));
1. [Highly accurate power usage computation for different Datacenter's scheduling intervals](https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/power/PowerExampleSchedulingInterval.java) ([#153](https://github.com/manoelcampos/cloudsim-plus/issues/153)).
1. [Parallel execution of simulations in multi-core computers](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/ParallelSimulationsExample.java), allowing multiple simulations to be run simultaneously in an isolated way ([#38](https://github.com/manoelcampos/cloudsim-plus/issues/38));
1. Delay creation of submitted VMs and [Cloudlets](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/DynamicCloudletsArrival1.java), enabling simulation of dynamic arrival of tasks ([#11](https://github.com/manoelcampos/cloudsim-plus/issues/11), [#23](https://github.com/manoelcampos/cloudsim-plus/issues/23)); 
1. [Allow dynamic creation of VMs and Cloudlets without requiring creation of Datacenter Brokers at runtime](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/DynamicCreationOfVmsAndCloudletsExample.java), enabling VMs to be created on-demand according to arrived cloudlets ([#43](https://github.com/manoelcampos/cloudsim-plus/issues/43));
1. [Listeners](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/listeners) to enable simulation monitoring and creation of VMs and Cloudlets at runtime;
1. It is a strongly object-oriented framework that creates relationships among classes and allows chained calls such as `cloudlet.getVm().getHost().getDatacenter()`. And guess what? You don't even have to worry about `NullPointerException` when making such a chained call because CloudSim Plus uses the [Null Object Design Pattern](https://en.wikipedia.org/wiki/Null_Object_pattern) to avoid that ([#10](https://github.com/manoelcampos/cloudsim-plus/issues/10));
1. Classes and interfaces to allow implementation of [heuristics](http://en.wikipedia.org/wiki/Heuristic) such as [Tabu Search](http://en.wikipedia.org/wiki/Tabu_search), [Simulated Annealing](http://en.wikipedia.org/wiki/Simulated_annealing), [Ant Colony Systems](http://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms) and so on. See an [example using Simulated Annealing here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/brokers/DatacenterBrokerHeuristicExample.java);
1. [Implementation of the Completely Fair Scheduler](https://en.wikipedia.org/wiki/Completely_Fair_Scheduler) used in recent versions of the Linux Kernel. See an example [here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/LinuxCompletelyFairSchedulerExample.java) ([#58](https://github.com/manoelcampos/cloudsim-plus/issues/58));
1. Completely re-designed and reusable Network module. Totally refactored network examples to make them clear and easy to change ([#13](https://github.com/manoelcampos/cloudsim-plus/issues/13), [#49](https://github.com/manoelcampos/cloudsim-plus/issues/49), [#57](https://github.com/manoelcampos/cloudsim-plus/issues/57), [#85](https://github.com/manoelcampos/cloudsim-plus/issues/85), [#132](https://github.com/manoelcampos/cloudsim-plus/issues/132), [#137](https://github.com/manoelcampos/cloudsim-plus/issues/137), [#138](https://github.com/manoelcampos/cloudsim-plus/issues/138));
1. Enables the use of any regular `CloudletScheduler` with a `NetworkVm`, such as the `CloudletSchedulerTimeShared`, `CloudletSchedulerSpaceShared` or the new `CloudletSchedulerCompletelyFair`. The introduced `CloudletTaskScheduler` is used inside all `CloudletSchedulers` to schedule the execution of `NetworkCloudletTasks`, such as sending and receiving network packets;
1. Updated to Java 8 to provide some [Functional Programming](https://en.wikipedia.org/wiki/Functional_programming) features (such as the next one), using [Lambda Expressions](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html) and [Streams API](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html) to make the code easier to understand and maintain;
1. <a id="exclusive-features-broker"></a> A [Functional](https://en.wikipedia.org/wiki/Functional_programming) `DatacenterBrokerSimple` class that enables changing, at runtime, policies for different goals. This dynamic behavior allows implementing specific policies without requiring the creation of new `DatacenterBroker` classes (check [this example](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/brokers/CloudletToVmMappingBestFit.java)). Consider *P* the number of policies and *I* the number of implementations for each policy. For *P = 3* and *I = 2*, if you want to try all possible combinations of policies and implementations, without CloudSim Plus, it would be required to create 12 `DatacenterBroker` classes (*P* * *I^(P-1)*), instead of just using the existing one. Some of these behaviors which can be changed are:
    - selection of a Datacenter to place waiting VMs and a fallback Datacenter when a previous selected one doesn't have a suitable Host for a VM ([#28](https://github.com/manoelcampos/cloudsim-plus/issues/28)); 
    - selection of a VM to run each Cloudlet ([#25](https://github.com/manoelcampos/cloudsim-plus/issues/25));
    - definition of the time when an idle VM should be destroyed ([#99](https://github.com/manoelcampos/cloudsim-plus/issues/99));
    - sorting of requests to create submitted VMs and Cloudlets, defining priorities to create such objects ([#102](https://github.com/manoelcampos/cloudsim-plus/issues/102)). 
1. [Host Fault Injection Mechanism](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/HostFaultInjectionExample1.java) to enable injection of random failures into Hosts CPU cores: it injects failures and reallocates working cores to running VMs. When all cores from a Host fail, it starts clones of failed VMs to recovery from failure. This way, it is simulated the instantiation of VM snapshots into different Hosts ([#81](https://github.com/manoelcampos/cloudsim-plus/issues/81)).
1. [Creation of Hosts at Simulation Runtime](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/DynamicHostCreation.java) to enable physical expansion of Datacenter capacity ([#124](https://github.com/manoelcampos/cloudsim-plus/issues/124)).
1. [Enables the simulation to keep running, waiting for dynamic and even random events such as the arrival of Cloudlets and VMs](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/KeepSimulationRunningExample.java) ([#130](https://github.com/manoelcampos/cloudsim-plus/issues/130)).
1. [Builders](/cloudsim-plus/src/main/java/org/cloudsimplus/builders/SimulationScenarioBuilder.java) to enable creating multiple simulation objects with the same configuration;
1. TableBuilder objects that are used in all examples and enable printing simulation results in different formats such as ASCII Table, CSV or HTML. It shows results in perfectly aligned tables, including data units. Check the last line of the [BasicFirstExample](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/BasicFirstExample.java) constructor to see how it is easy to print results;
1. Integration Tests to increase framework accuracy by testing entire simulation scenarios;
1. Throughout documentation update, improvement and extension;
1. Improved class hierarchy, modules and package structure that are easier to understand, following the [Separation of Concerns principle (SoC)](https://en.wikipedia.org/wiki/Separation_of_concerns);
1. As it is usual to extend framework classes to provide some specific behaviors for your simulations, you will find a totally refactored code following clean code programming, [SOLID](https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)), [Design Patterns](https://en.wikipedia.org/wiki/Software_design_pattern) and several other software engineering principles and practices. This way, it will be easier to understand the code and implement the feature you want;
1. Simpler constructors to instantiate simulation objects, making it less confusing to use the framework. It applies the [Convention over Configuration principle (CoC)](https://en.wikipedia.org/wiki/Convention_over_configuration) to ask just mandatory parameters when instantiating objects ([#30](https://github.com/manoelcampos/cloudsim-plus/issues/30));
1. Defines types and colors for log messages and enables filtering the level of messages to print. The image below shows how easy is to check things that may be wrong in your simulation ([#24](https://github.com/manoelcampos/cloudsim-plus/issues/24)). ![](docs/images/log-messages-by-type.png) And for instance, if you want to see just messages from warning level, it's as simple as calling `Log.setLevel(ch.qos.logback.classic.Level.WARN);` 

# Project's Structure

CloudSim Plus has a simpler structure to make it ease to use and understand. It consists of 4 modules, 2 of which are new, as presented below.

![CloudSim Plus Modules](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/modules.png)

- [cloudsim-plus](/cloudsim-plus): the CloudSim Plus cloud simulation framework API, which is used by all other modules. 
  It is the main and only required module you need to write cloud simulations. 
- [cloudsim-plus-examples](/cloudsim-plus-examples): includes a series of different examples, since minimal simulation scenarios using basic 
  CloudSim Plus features, to complex scenarios using workloads from trace files or Vm migration examples. This is an excellent starting point for learning how to build cloud simulations using CloudSim Plus.
- [cloudsim-plus-testbeds](/cloudsim-plus-testbeds): enables implementation of simulation testbeds in a repeatable manner, 
  allowing a researcher to execute several simulation runs for a given experiment and collect statistical data using a scientific approach. 
- [cloudsim-plus-benchmarks](/cloudsim-plus-benchmarks): a new module used just internally to implement micro benchmarks to assess framework performance.

It also has a better package organization, improving [Separation of Concerns (SoC)](https://en.wikipedia.org/wiki/Separation_of_concerns) and making it easy to know where a desired class is and what is inside each package. The figure below presents the new package organization. The dark yellow packages are new in CloudSim Plus and include its exclusive interfaces and classes. The light yellow ones were introduced just to better organize existing CloudSim classes and interfaces. 

![CloudSim Plus Packages](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/package-structure-reduced.png)


<p align="right"><a href="#top">:arrow_up:</a></p>

# How to Use CloudSim Plus 
There are 3 ways to use CloudSim Plus. It can be downloaded and executed: (i) directly from some IDE; (ii) from the command line; or (iii) from [Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cloudsimplus/cloudsim-plus) once you include it as a dependency inside your own project.

You can watch the video below ([high quality version here](https://youtu.be/k2enNoxTYVw)) or follow the instructions in one of the next subsections.

![Downloading CloudSim Plus and running Examples using NetBeans](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/cloudsim-plus-netbeans.gif)

## Via Command Line
Considering that you have [git](https://git-scm.com) and [maven](http://maven.apache.org) installed on your operating system, 
download the project source by cloning the repository issuing the command `git clone https://github.com/manoelcampos/cloudsim-plus.git` 
at a terminal. 

The project has a [bash script](script/bootstrap.sh) you can use to build and run CloudSim Plus examples. 
This is a script for Unix-like systems such as Linux, FreeBSD and macOS.

To run some example type the command: `sh script/bootstrap.sh package.ExampleClassName`.
For instance, to run the `CloudSimExample0` you can type: `sh script/bootstrap.sh org.cloudbus.cloudsim.examples.CloudSimExample0`. 

The script checks if it is required to build the project, using maven in this case, making sure to download all dependencies. 
To see which examples are available, just navigate through the [examples directory](/cloudsim-plus-examples/src/main/java/).
To check more script options, run it without any parameter.  
 
## By Means of an IDE
The easiest way to use the project is relying on some IDE such as [NetBeans](http://netbeans.org), [Eclipse](http://eclipse.org) 
or [IntelliJ IDEA](http://jetbrains.com/idea/).
Below are the steps to start using the project:

- Download the project sources by using: the download button on top of this page; your own IDE for it; or the command line as described above.
- Open/import the project in your IDE:
    - For NetBeans, just use the "Open project" menu and select the directory where the project was downloaded/cloned.
    - For Eclipse or IntelliJ IDEA, 
      you have to import the project selecting the folder where the project was cloned. 
      **Check an Eclipse tutorial [here](https://youtu.be/oO-a5-cZBps)**.
- Inside the opened/imported project you will have the cloudsim-plus and cloudsim-plus-examples modules. 
  The cloudsim-plus module is where the simulator source code is, that usually you don't have to change, unless you want to contribute to the project. 
  The cloudsim-plus-examples is where you can start.
- Open the cloudsim-plus-examples module. The most basic examples are in the root of the org.cloudbus.cloudsim.examples package. 
  You can run any one of the classes in this package to get a specific example. 
- If you want to build your own simulations, the easiest way is to create another class inside this module.

<a id="maven"></a>

## Adding it as a Maven Dependency into Your Own Project

You can add CloudSim Plus API module, that is the only one required to build simulations, as a dependency inside the pom.xml file or your own maven project,
as presened below (check if the informed version is the latest one). This way you can start building your simulations from scratch.

```xml
<dependency>
    <groupId>org.cloudsimplus</groupId>
    <artifactId>cloudsim-plus</artifactId>
    <!-- Set a specific version or use the latest one -->
    <version>LATEST</version>
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
//Enables just some level of logging.
//Make sure to import org.cloudsimplus.util.Log;
//Log.setLevel(ch.qos.logback.classic.Level.WARN);

//Creates a CloudSim object to initialize the simulation.
CloudSim cloudsim = new CloudSim();

/*Creates a Broker that will act on behalf of a cloud user (customer).*/
DatacenterBroker broker0 = new DatacenterBrokerSimple(cloudsim);

//Creates a list of Hosts, each host with a specific list of CPU cores (PEs).
List<Host> hostList = new ArrayList<>(1);
List<Pe> hostPes = new ArrayList<>(1);
hostPes.add(new PeSimple(20000, new PeProvisionerSimple()));
long ram = 10000; //in Megabytes
long storage = 100000; //in Megabytes
long bw = 100000; //in Megabits/s
Host host0 = new HostSimple(ram, bw, storage, hostPes);
host0.setRamProvisioner(new ResourceProvisionerSimple())
      .setBwProvisioner(new ResourceProvisionerSimple())
      .setVmScheduler(new VmSchedulerSpaceShared());
hostList.add(host0);

//Creates a Datacenter with a list of Hosts.
Datacenter dc0 = new DatacenterSimple(cloudsim, hostList, new VmAllocationPolicySimple());

//Creates VMs to run applications.
List<Vm> vmList = new ArrayList<>(1);
Vm vm0 = new VmSimple(0, 1000, 1);
vm0.setRam(1000).setBw(1000).setSize(1000)
   .setCloudletScheduler(new CloudletSchedulerSpaceShared());
vmList.add(vm0);

//Creates Cloudlets that represent applications to be run inside a VM.
List<Cloudlet> cloudletList = new ArrayList<>(1);
Cloudlet cloudlet0 = new CloudletSimple(0, 10000, 1);
cloudlet0.setUtilizationModel(new UtilizationModelFull());
cloudletList.add(cloudlet0);
Cloudlet cloudlet1 = new CloudletSimple(1, 10000, 1);
cloudlet1.setUtilizationModel(new UtilizationModelFull());
cloudletList.add(cloudlet1);

broker0.submitVmList(vmList);
broker0.submitCloudletList(cloudletList);

/*Starts the simulation and waits all cloudlets to be executed, automatically
stopping when there is no more events to process.*/
cloudsim.start();

/*Prints results when the simulation is over
(you can use your own code here to print what you want from this cloudlet list).*/
new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
```

The presented results are structured and clear to allow better understanding. For example, the image below shows the output for a simulation with two cloudlets (applications).
![Simulation Results](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/simulation-results.png)

<p align="right"><a href="#top">:arrow_up:</a></p>

# Documentation and Help

The project documentation originated from CloudSim was entirely updated and extended. 
You can see the javadoc documentation for classes and their elements directly on your IDE.

The documentation is available online at [ReadTheDocs](http://cloudsimplus.rtfd.io/en/latest/?badge=latest), which includes a FAQ and guides.
CloudSim Plus has extended documentation of classes and interfaces and also includes extremely helpful
package documentation that can be viewed directly on your IDE or at the link provided above.
Such a package documentation gives a general overview of the classes used to build a cloud simulation.

A Google Group forum is also available at <https://groups.google.com/group/cloudsim-plus>.
See the [publications](#publications) section to access published CloudSim Plus papers.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="why-care"></a>

# Why should I care about this CloudSim fork? I just want to build my simulations. :neutral_face:
Well, the design of the tool has a direct impact when you need to extend it to include some feature required for your simulations. 
The simulator has a set of classes that implement interfaces such as `VmScheduler`, `CloudletScheduler`, `VmAllocationPolicy`, `ResourceProvisioner`, 
`UtilizationModel`, `PowerModel` and `DatacenterBroker` and provide basic algorithms for different goals. 
For instance, the `VmAllocationPolicySimple` class implements a Worst Fit
policy that selects the PM which less processor cores in use to host a VM and, in fact, it is the only policy available. 

Usually you have to write your own implementations of these classes, such as a Best Fit `VmAllocationPolicy`, 
a resource `UtilizationModel` with an upper threshold or a `DatacenterBroker` that selects the best `Datacenter` to submit a VM.

Several software engineering principles aim to ease the task of creating new classes to implement those features. 
They also try to avoid forcing you to change core classes of the simulator in order to introduce a feature you need to implement.
**Changing these core classes just to implement a particular feature which will be used only in your simulations is a bad practice, since you will not be able to automatically update your project to new versions of the simulator, without losing your changes or struggling to fix merge conflicts.**

As we have seen in forums that we've attended, many times users have to perform these changes in core classes 
just to implement some specific features they need. We think those problems are enough reasons that show the need of a new re-engineered version of the simulator.  

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="why-another-fork"></a>

# But why an independent CloudSim fork? :unamused:
The original CloudSim moved on to a new major release, introducing a completely new set of classes to provide Container as a Service (CaaS) simulations, 
before the changes proposed here being merged to the official repository. This way, all the work performed here was not incorporated to allow this new CaaS module to be developed using this redesigned version. Unfortunately, there are several months of hard work that would need to be replicated to merge both projects. In reason of that, CloudSim Plus was born as an independent fork, following its own way and philosophies.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="differences"></a>

# What are the practical differences of using CloudSim Plus instead of CloudSim? How can I update my simulations to use CloudSim Plus?

It's much easier to use CloudSim Plus. A complete, side-by-side [comparison between CloudSim and CloudSim Plus Java simulation scenarios is available here](http://cloudsimplus.org/docs/CloudSim-and-CloudSimPlus-Comparison.html).

To update your simulations to use the CloudSim Plus you have to change the way that some objects are instantiated, because some new interfaces were introduced to follow the "program to an interface, not an implementation" recommendation and also to increase [abstraction](https://en.wikipedia.org/wiki/Abstraction_(software_engineering)). 
These new interfaces were also crucial to implement the [Null Object Pattern](https://en.wikipedia.org/wiki/Null_Object_pattern) to try avoiding `NullPointerException`s.

The initialization of the simulation is not performed by the static `CloudSim.startSimulation` method anymore, which required a lot of parameters.
Now you have just to instantiate a `CloudSim` object using the default, no-arguments constructor, as shown below. This instance is used in the constructor of `DatacenterBroker` and `Datacenter` objects: 

```java
CloudSim cloudsim = new CloudSim();
```

The classes `Datacenter`, `DatacenterCharacteristics`, `Host`, `Pe`, `Vm` and `Cloudlet` were renamed due to 
the introduction of interfaces with these same names. Now all these classes have a suffix *Simple* 
(as already defined for some previous classes such as `PeProvisionerSimple` and `VmAllocationPolicySimple`). 
For instance, to instantiate a `Cloudlet` you have to execute a code such as:

 ```java
CloudletSimple cloudlet = new CloudletSimple(required, parameters, here);
```   

However, since these interfaces were introduced in order to also enable the creation of different cloudlet classes, 
the recommendation is to declare your object using the interface, not the class: 
 
 ```java
Cloudlet cloudlet = new CloudletSimple(required, parameters, here);
```   

The method `setBrokerId(int userId)` from `Vm` and `Cloudlet` were refactored to `setBroker(DatacenterBroker broker)`,
now requiring a `DatacenterBroker` instead of just an int ID which may be even nonexistent.

You don't need to explicitly create a `DatacenterCharacteristics` anymore. Such object is created internally when a `Datacenter` is created.
A `VmAllocationPolicy` doesn't require any parameter at all. A `Datacenter` doesn't require a name, storage list and scheduling interval too.
The name will be automatically defined. It and all the other parameter can be set further using the respective setter methods.
Now it is just required a `CloudSim`, a `Host` list and a `VmAllocationPolicy` instance.

```java
Datacenter dc0 = new DatacenterSimple(cloudsim, hostList, new VmAllocationPolicySimple());
```

The way you instantiate a host has changed too. The classes `RamProvisionerSimple` and `BwProvisionerSimple` don't exist anymore. Now you just have the generic class `ResourceProvisionerSimple` and you can just use its default no-args constructor. RAM and bandwidth capacity of the host now are given in the constructor, as it already was for storage. A `VmScheduler` constructor doesn't require any parameter. You don't need to set an ID for each Host, since
if one is not given, when the List of hosts is attached to a Datacenter, it will generate an ID for those hosts. Instantiating a host should be now similar to:

```java
long ram = 20480; //in MB
long bw = 1000000; //in Megabits/s
long storage = 1000000; //in MB
Host host = new HostSimple(ram, bw, storage, pesList);
host.setRamProvisioner(new ResourceProvisionerSimple())
    .setBwProvisioner(new ResourceProvisionerSimple())
    .setVmScheduler(new VmSchedulerTimeShared());
``` 

Additionally, the interface `Storage` was renamed to `FileStorage` and its implementations are `SanStorage` and `HarddriveStorage`, that can be used as before. Finally, since the packages were reorganized, you have to adjust them. However, use your IDE to correct the imports for you. A complete and clear example was presented in the <a href="#a-minimal-and-complete-simulation-example">Examples</a> section above.

<p align="right"><a href="#top">:arrow_up:</a></p>

# General Features of the Framework

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

1. M. C. Silva Filho, R. L. Oliveira, C. C. Monteiro, P. R. M. Inácio, and M. M. Freire. [CloudSim Plus: a Cloud Computing Simulation Framework Pursuing Software Engineering Principles for Improved Modularity, Extensibility and Correctness,](https://doi.org/10.23919/INM.2017.7987304) in IFIP/IEEE International Symposium on Integrated Network Management, 2017, p. 7. If you are using CloudSim Plus in your research, please make sure you cite that paper. You can check the paper presentation [here](http://cloudsimplus.org/docs/presentation/).
2. White Paper. [CloudSim Plus: A Modern Java 8 Framework for Modeling and Simulation of Cloud Computing Infrastructures and Services](https://github.com/manoelcampos/cloudsim-plus/blob/master/docs/cloudsim-plus-white-paper.pdf). 2016.
3. R. L. Oliveira. [Virtual Machine Allocation in Cloud Computing Environments based on Service Level Agreements](docs/MScDissertation-RaysaOliveira.pdf) (only in Portuguese). Master's Dissertation. University of Beira Interior, 2017 (Supervisor: M. M. Freire).
  
<p align="right"><a href="#top">:arrow_up:</a></p>
  
# License

This project is licensed under [GNU GPLv3](http://www.gnu.org/licenses/gpl-3.0), as defined inside CloudSim 3 source files.

<p align="right"><a href="#top">:arrow_up:</a></p>

# Contributing

You are welcome to contribute to the project. However, make sure you read the [contribution guide](CONTRIBUTING.md) before starting. The guide provides information on the different ways you can contribute, such as by requesting a feature, reporting an issue, fixing a bug or providing some new feature.

<p align="right"><a href="#top">:arrow_up:</a></p>
