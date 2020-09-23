<a id="top"></a>

<p align="center">
<b><a href="#overview">Overview</a></b>
|
<b><a href="#2-main-exclusive-features">Exclusive Features</a></b>
|
<b><a href="#3-projects-structure">Structure</a></b>
|
<b><a href="#4-how-to-use-cloudsim-plus">How to use</a></b>
|
<b><a href="#5-a-minimal-and-complete-simulation-example">Examples</a></b>
|
<b><a href="#6-documentation-and-help">Docs and Help</a></b>
|
<b><a href="#why-care">Why should I care?</a></b>
|
<b><a href="#why-another-fork">Why an independent fork?</a></b>
|
<b><a href="#differences">Differences from CloudSim</a></b>
|
<b><a href="#general-features">General Features</a></b>
|
<b><a href="#publications">Publications</a></b>
|
<b><a href="#projects">Related Projects</a></b>
|
<b><a href="#license">License</a></b>
|
<b><a href="#contributing">Contributing</a></b>
</p>


<a id="overview"></a>

# 1. CloudSim Plus Overview [![Open in Gitpod (You need to log in at gitpod.io first)](https://img.shields.io/badge/Gitpod-Open%20in%20Gitpod-%230092CF.svg)](https://gitpod.io/#https://github.com/manoelcampos/cloudsim-plus) [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=Check%20out%20CloudSim%20Plus:%20a%20modern%20and%20full-featured%20framework%20for%20cloud%20computing%20simulation.%20It's%20actively%20maintained%20and%20fully%20documented,%20making%20your%20research%20easier.%20via%20@manoelcampos&url=https://cloudsimplus.org&hashtags=cloudsimplus,cloud,computing,simulation,framework) [![GitHub stars](https://img.shields.io/github/stars/manoelcampos/cloudsim-plus.svg?style=social&label=Contribute.%20Star%20It.%20&#11088;%20&#128077;&maxAge=2592000)](https://github.com/manoelcampos/cloudsim-plus/)

[![Build Status](https://img.shields.io/travis/manoelcampos/cloudsim-plus/master.svg)](https://travis-ci.org/manoelcampos/cloudsim-plus) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/app/manoelcampos/cloudsim-plus?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=manoelcampos/cloudsim-plus&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/app/manoelcampos/cloudsim-plus?utm_source=github.com&utm_medium=referral&utm_content=manoelcampos/cloudsim-plus&utm_campaign=Badge_Coverage) [![Maven Central](https://img.shields.io/maven-central/v/org.cloudsimplus/cloudsim-plus.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.cloudsimplus%22%20AND%20a:%22cloudsim-plus%22) [![Documentation Status](https://readthedocs.org/projects/cloudsimplus/badge/?version=latest)](http://cloudsimplus.rtfd.io/en/latest/?badge=latest) 
[![GitHub Closed Issues](https://img.shields.io/github/issues-closed-raw/manoelcampos/cloudsim-plus.svg?style=rounded-square)](http://github.com/manoelcampos/cloudsim-plus/issues) 
[![GPL licensed](https://img.shields.io/badge/license-GPL-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)


CloudSim Plus is a modern, up-to-date, full-featured and fully documented simulation framework. It's easy to use and extend, enabling modeling, simulation, 
and experimentation of Cloud computing infrastructures and application services. 
It allows developers to focus on specific system design issues to be investigated, 
without concerning the low-level details related to Cloud-based infrastructures and services.
 
CloudSim Plus is a fork of CloudSim 3, 
re-engineered primarily to avoid code duplication,
provide [code reusability](https://en.wikipedia.org/wiki/Code_reuse) and ensure 
compliance with software engineering principles and recommendations for extensibility improvements and accuracy. It's currently the state-of-the-art in cloud computing simulation framework. 

The efforts dedicated to this project have been recognized by the [EU/Brasil Cloud FORUM](https://eubrasilcloudforum.eu). 
A post about CloudSim Plus is available at 
[this page of the Forum](https://eubrasilcloudforum.eu/en/instituto-federal-de-educação-do-tocantins-brazil-instituto-de-telecomunicações-portugal-and), 
including a White Paper available in the [Publications Section](#publications).

CloudSim Plus is developed through a partnership between the Systems, Security and Image Communication Lab 
of [Instituto de Telecomunicações (IT, Portugal)](http://www.it.pt), 
the [Universidade da Beira Interior (UBI, Portugal)](http://www.ubi.pt) 
and the [Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil)](http://www.ifto.edu.br). 
It is supported by the Portuguese [Fundação para a Ciência e a Tecnologia (FCT)](https://www.fct.pt) 
and by the [Brazilian foundation Coordenação de Aperfeiçoamento de Pessoal de Nível Superior (CAPES)](http://www.capes.gov.br).

<p align="right"><a href="#top">:arrow_up:</a></p>

# 2. Main Exclusive Features

CloudSim Plus provides a lot of exclusive features, from the most basic ones to build simple simulations, 
to advanced features for simulating more realistic cloud scenarios: 

1. It is easier to use. [A complete and easy-to-understand simulation scenario can be built in few lines of code.](#5-a-minimal-and-complete-simulation-example) 
1. Process trace files from [Google Cluster Data](https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md) 
   creating Hosts and Cloudlets (tasks). A script to download the trace files is available at [download-google-cluster-data.sh](script/download-google-cluster-data.sh). Examples are available [here](cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/googletraces) ([#149](https://github.com/manoelcampos/cloudsim-plus/issues/149)).
1. [Vertical VM Scaling](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/autoscaling/VerticalVmCpuScalingExample.java) that performs on-demand up and down allocation of VM resources such as Ram, Bandwidth and PEs (CPUs) ([#7](https://github.com/manoelcampos/cloudsim-plus/issues/7));
1. [Horizontal VM scaling](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/autoscaling/LoadBalancerByHorizontalVmScalingExample.java), allowing dynamic creation of VMs according to an overload condition. Such a condition is defined by a predicate that can check different VM resources usage such as CPU, RAM or BW ([#41](https://github.com/manoelcampos/cloudsim-plus/issues/41));
1. Creation of joint power- and network-aware simulations. Regular classes such as `DatacenterSimple`, `HostSimple` and `VmSimple` now allow power-aware simulations. By using the network version of such classes, simulations that are both power- and network-aware can be created. ([#45](https://github.com/manoelcampos/cloudsim-plus/issues/45));
1. [Highly accurate power usage computation for different Datacenter's scheduling intervals](https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/power/PowerExampleSchedulingInterval.java) ([#153](https://github.com/manoelcampos/cloudsim-plus/issues/153)).
1. [Built-in computation of CPU utilization history and energy consumption for VMs (and Hosts)](https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/power/PowerExample.java) ([#168](https://github.com/manoelcampos/cloudsim-plus/issues/168)). 
1. [Automatically power Hosts on and off according to demand](https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/power/HostActivation.java) ([#128](https://github.com/manoelcampos/cloudsim-plus/issues/128)).
1. [Parallel execution of simulations in multi-core computers](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/ParallelSimulationsExample.java), allowing multiple simulations to be run simultaneously in an isolated way ([#38](https://github.com/manoelcampos/cloudsim-plus/issues/38));
1. Delay creation of submitted VMs and [Cloudlets](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/DynamicCloudletsArrival1.java), enabling simulation of dynamic arrival of tasks ([#11](https://github.com/manoelcampos/cloudsim-plus/issues/11), [#23](https://github.com/manoelcampos/cloudsim-plus/issues/23)); 
1. [Allow dynamic creation of VMs and Cloudlets without requiring creation of Datacenter Brokers at runtime](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/DynamicCreationOfVmsAndCloudletsExample.java), enabling VMs to be created on-demand according to arrived cloudlets ([#43](https://github.com/manoelcampos/cloudsim-plus/issues/43));
1. [Listeners](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/listeners) to enable simulation monitoring and creation of VMs and Cloudlets at runtime;
1. It is a strongly object-oriented framework that creates relationships among classes and allows chained calls such as `cloudlet.getVm().getHost().getDatacenter()`. And guess what? You don't even have to worry about `NullPointerException` when making such a chained call because CloudSim Plus uses the [Null Object Design Pattern](https://en.wikipedia.org/wiki/Null_Object_pattern) to avoid that ([#10](https://github.com/manoelcampos/cloudsim-plus/issues/10));
1. Classes and interfaces to allow implementation of [heuristics](http://en.wikipedia.org/wiki/Heuristic) such as [Tabu Search](http://en.wikipedia.org/wiki/Tabu_search), [Simulated Annealing](http://en.wikipedia.org/wiki/Simulated_annealing), [Ant Colony Systems](http://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms) and so on. See an [example using Simulated Annealing here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/brokers/DatacenterBrokerHeuristicExample.java);
1. [Implementation of the Completely Fair Scheduler](https://en.wikipedia.org/wiki/Completely_Fair_Scheduler) used in recent versions of the Linux Kernel. See an example [here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/LinuxCompletelyFairSchedulerExample.java) ([#58](https://github.com/manoelcampos/cloudsim-plus/issues/58));
1. <a id="exclusive-features-broker"></a> A [Functional](https://en.wikipedia.org/wiki/Functional_programming) `DatacenterBrokerSimple` class that enables changing, at runtime, policies for different goals. [This dynamic behavior allows implementing specific policies without requiring the creation of new DatacenterBroker classes](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/brokers/CloudletToVmMappingBestFit.java). The policies that can be dynamically changed are:
    - selection of a Datacenter to place waiting VMs ([#28](https://github.com/manoelcampos/cloudsim-plus/issues/28)); 
    - selection of a VM to run each Cloudlet ([#25](https://github.com/manoelcampos/cloudsim-plus/issues/25));
    - definition of the time when an idle VM should be destroyed ([#99](https://github.com/manoelcampos/cloudsim-plus/issues/99));
    - sorting of requests to create submitted VMs and Cloudlets, defining priorities to create such objects ([#102](https://github.com/manoelcampos/cloudsim-plus/issues/102)). 
1. [Host Fault Injection Mechanism](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/HostFaultInjectionExample1.java) to enable injection of random failures into Hosts CPU cores: it injects failures and reallocates working cores to running VMs. When all cores from a Host fail, it starts clones of failed VMs to recovery from failure. This way, it is simulated the instantiation of VM snapshots into different Hosts ([#81](https://github.com/manoelcampos/cloudsim-plus/issues/81)).
1. [Creation of Hosts at Simulation Runtime](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/DynamicHostCreation.java) to enable physical expansion of Datacenter capacity ([#124](https://github.com/manoelcampos/cloudsim-plus/issues/124)).
1. [Enables the simulation to keep running, waiting for dynamic and even random events such as the arrival of Cloudlets and VMs](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/KeepSimulationRunningExample.java) ([#130](https://github.com/manoelcampos/cloudsim-plus/issues/130)).
1. TableBuilder objects that are used in all examples and enable printing simulation results in different formats such as ASCII Table, CSV or HTML. It shows results in perfectly aligned tables, including data units. Check the last line of the [BasicFirstExample](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/BasicFirstExample.java) constructor to see how it is easy to print results;
1. Defines types and colors for log messages and enables filtering the level of messages to print. The image below shows how easy is to check things that may be wrong in your simulation ([#24](https://github.com/manoelcampos/cloudsim-plus/issues/24)). ![](docs/images/log-messages-by-type.png) And for instance, if you want to see just messages from warning level, it's as simple as calling `Log.setLevel(ch.qos.logback.classic.Level.WARN);`
1. [Enables running the simulation synchronously, making it easier to interact with it and collect data inside a loop, as the simulation progresses](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/synchronous/SynchronousSimulationExample1.java). This brings freedom in the way you can implement your simulations ([#205](https://github.com/manoelcampos/cloudsim-plus/issues/205)).
1. [Enables Broker to destroy a VM and return the list of unfinished Cloudlets, so that they can be resubmitted to another VM.](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/synchronous/SynchronousSimulationDestroyVmExample1.java) ([#209](https://github.com/manoelcampos/cloudsim-plus/issues/209)).
1. [Allows placing a group of VMs into the same Host.](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/VmGroupPlacementExample1.java) ([#90](https://github.com/manoelcampos/cloudsim-plus/issues/90)).
1. [Enables Broker to try selecting the closest Datacenter to place VMs, according to their time zone.](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/brokers/DatacenterSelectionByTimeZoneExample.java) ([#212](https://github.com/manoelcampos/cloudsim-plus/issues/212)).
1. And yeah, it outperforms CloudSim 4, as can be seen in the table below*.

VmAllocationPolicy|CloudSim 4.0.0 Simulation Time (min) |CloudSim Plus 4.3.4 Simulation Time (min) |DCs|Hosts|VMs  |Cloudlets
------------------|-------------------------------------|------------------------------------------|---|-----|-----|---------
Simple (WorstFit) |23.9                                 |15.7                                      |1  |20000|40000|50000
BestFit**         |19.6                                 |15.7                                      |1  |20000|40000|50000
FirstFit**        |13.6                                 |&nbsp;&nbsp;1.3                           |1  |20000|40000|50000

\* *More details and results [here](docs/performance.md).*   \** *Only officially available in CloudSim Plus.*

# 3. Project's Structure

CloudSim Plus has a simpler structure to make it ease to use and understand. It consists of 4 modules, 2 of which are new, as presented below.

![CloudSim Plus Modules](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/modules.png)

- [cloudsim-plus](/cloudsim-plus): the CloudSim Plus cloud simulation framework API, which is used by all other modules. 
  It is the main and only required module you need to write cloud simulations. 
- [cloudsim-plus-examples](/cloudsim-plus-examples): includes a series of different examples, since minimal simulation scenarios using basic 
  CloudSim Plus features, to complex scenarios using workloads from trace files or Vm migration examples. This is an excellent starting point for learning how to build cloud simulations using CloudSim Plus.
- [cloudsim-plus-testbeds](/cloudsim-plus-testbeds): enables implementation of simulation testbeds in a repeatable manner, 
  allowing a researcher to execute several simulation runs for a given experiment and collect statistical data using a scientific approach. 
- [cloudsim-plus-benchmarks](/cloudsim-plus-benchmarks): a new module used just internally to implement micro benchmarks to assess framework performance.

It also has a better package organization, 
improving [Separation of Concerns (SoC)](https://en.wikipedia.org/wiki/Separation_of_concerns) 
and making it easy to know where a desired class is and what is inside each package. 
The figure below presents the new package organization. 
The dark yellow packages are new in CloudSim Plus and include its exclusive interfaces and classes. 
The light yellow ones were introduced just to better organize existing CloudSim classes and interfaces. 

![CloudSim Plus Packages](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/package-structure-reduced.png)


<p align="right"><a href="#top">:arrow_up:</a></p>

# 4. How to Use CloudSim Plus 
There are 4 ways to use CloudSim Plus. It can be downloaded and executed: 
(i) downloading a zip file using the button at the top of this page;
(ii) from the command line; 
(iii) directly from some IDE; 
(iv) from [Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cloudsimplus/cloudsim-plus) 
since you include it as a dependency inside your own project.

You can watch the video below ([high quality version here](https://youtu.be/k2enNoxTYVw)) 
or follow the instructions in one of the next subsections.

![Downloading CloudSim Plus and running Examples using NetBeans](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/cloudsim-plus-netbeans.gif)

## 4.1 Via Command Line
Considering that you have [git](https://git-scm.com) and [maven](http://maven.apache.org) installed on your operating system, 
download the project source by cloning the repository issuing the command `git clone https://github.com/manoelcampos/cloudsim-plus.git` 
at a terminal. 

The project has a [bash script](script/bootstrap.sh) you can use to build and run CloudSim Plus examples. 
This is a script for Unix-like systems such as Linux, FreeBSD and macOS.

To run some example, type the following command at a terminal inside the project's root directory: `sh script/bootstrap.sh package.ExampleClassName`.
For instance, to run the `CloudSimExample0` you can type: `sh script/bootstrap.sh org.cloudbus.cloudsim.examples.CloudSimExample0`. 

The script checks if it is required to build the project, using maven in this case, making sure to download all dependencies. 
To see which examples are available, just navigate through the [examples directory](/cloudsim-plus-examples/src/main/java/).
To check more script options, run it without any parameter.  
 
## 4.2 By Means of an IDE
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

## 4.3 Adding it as a Maven Dependency into Your Own Project

You can add CloudSim Plus API module (which is the only one required to build simulations) as a dependency inside your own Maven or Gradle project.
This way you can start building your simulations from scratch.

### 4.3.1 Maven

Add the following dependency into the pom.xml file of your own Maven project. 

```xml
<dependency>
    <groupId>org.cloudsimplus</groupId>
    <artifactId>cloudsim-plus</artifactId>
    <!-- Set a specific version or use the latest one -->
    <version>LATEST</version>
</dependency>
```

### 4.3.2 Gradle

Add the following dependency into the build.gradle file of your own Gradle project. 

```groovy
dependencies {
    //Set a specific version or use the latest one
    implementation 'org.cloudsimplus:cloudsim-plus:LATEST'
}
```

<p align="right"><a href="#top">:arrow_up:</a></p>

# 5. A Minimal and Complete Simulation Example

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
//Uses a PeProvisionerSimple by default to provision PEs for VMs
hostPes.add(new PeSimple(20000));
long ram = 10000; //in Megabytes
long storage = 100000; //in Megabytes
long bw = 100000; //in Megabits/s

//Uses ResourceProvisionerSimple by default for RAM and BW provisioning
//Uses VmSchedulerSpaceShared by default for VM scheduling
Host host0 = new HostSimple(ram, bw, storage, hostPes);
hostList.add(host0);

//Creates a Datacenter with a list of Hosts.
//Uses a VmAllocationPolicySimple by default to allocate VMs
Datacenter dc0 = new DatacenterSimple(cloudsim, hostList);

//Creates VMs to run applications.
List<Vm> vmList = new ArrayList<>(1);
//Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
Vm vm0 = new VmSimple(1000, 1);
vm0.setRam(1000).setBw(1000).setSize(1000);
vmList.add(vm0);

//Creates Cloudlets that represent applications to be run inside a VM.
List<Cloudlet> cloudletList = new ArrayList<>(1);
//UtilizationModel defining the Cloudlets use only 50% of any resource all the time
UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
Cloudlet cloudlet0 = new CloudletSimple(10000, 1, utilizationModel);
Cloudlet cloudlet1 = new CloudletSimple(10000, 1, utilizationModel);
cloudlets.add(cloudlet0);
cloudlets.add(cloudlet1);

broker0.submitVmList(vmList);
broker0.submitCloudletList(cloudletList);

/*Starts the simulation and waits all cloudlets to be executed, automatically
stopping when there is no more events to process.*/
cloudsim.start();

/*Prints results when the simulation is over
(you can use your own code here to print what you want from this cloudlet list).*/
new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
```

The presented results are structured and clear to allow better understanding. 
For example, the image below shows the output for a simulation with two cloudlets (applications).

![Simulation Results](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/simulation-results.png)

<p align="right"><a href="#top">:arrow_up:</a></p>

# 6. Documentation and Help

The project documentation originated from CloudSim was entirely updated and extended. 
You can see the javadoc documentation for classes and their elements directly on your IDE.

The documentation is available online at [ReadTheDocs](http://cloudsimplus.rtfd.io/en/latest/?badge=latest), 
which includes a FAQ and guides.
CloudSim Plus has extended documentation of classes and interfaces and also includes extremely helpful
package documentation that can be viewed directly on your IDE or at the link provided above.
Such a package documentation gives a general overview of the classes used to build a cloud simulation.

A Google Group forum is also available at <https://groups.google.com/group/cloudsim-plus>.
See the [publications](#publications) section to access published CloudSim Plus papers.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="why-care"></a>

## 6.1. Why should I care about this CloudSim fork? I just want to build my simulations. :neutral_face:
Well, the design of the tool has a direct impact when you need to extend it to include some feature required for your simulations. 
The simulator has a set of classes that implement interfaces such as `VmScheduler`, `CloudletScheduler`, `VmAllocationPolicy`, `ResourceProvisioner`, 
`UtilizationModel`, `PowerModel` and `DatacenterBroker` and provide basic algorithms for different goals. 
For instance, the `VmAllocationPolicySimple` class implements a Worst Fit
policy that selects the PM which less processor cores in use to host a VM and, in fact, it is the only policy available. 

Usually you have to write your own implementations of these classes, such as a Best Fit `VmAllocationPolicy`, 
a resource `UtilizationModel` with an upper threshold or a `DatacenterBroker` that selects the best `Datacenter` to submit a VM.

Several software engineering principles aim to ease the task of creating new classes to implement those features. 
They also try to avoid forcing you to change core classes of the simulator in order to introduce a feature you need to implement.
**Changing these core classes just to implement a particular feature which will be used only in your simulations is a bad practice, 
since you will not be able to automatically update your project to new versions of the simulator, 
without losing your changes or struggling to fix merge conflicts.**

As we have seen in forums that we've attended, many times users have to perform these changes in core classes 
just to implement some specific features they need. We think those problems are enough reasons 
that show the need of a new re-engineered version of the simulator.  

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="why-another-fork"></a>

## 6.2. But why an independent CloudSim fork? :unamused:
The original CloudSim moved on to a new major release, 
introducing a completely new set of classes to provide Container as a Service (CaaS) simulations, 
before the changes proposed here being merged to the official repository. 
This way, all the work performed here was not incorporated to allow this new CaaS module to be developed using this redesigned version. 
Unfortunately, there are several months of hard work that would need to be replicated to merge both projects. 
In reason of that, CloudSim Plus was born as an independent fork, following its own way and philosophies.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="differences"></a>

## 6.3. What are the practical differences of using CloudSim Plus instead of CloudSim? How can I update my simulations to use CloudSim Plus?

It's much easier to use CloudSim Plus. 
A complete, side-by-side [comparison between CloudSim and CloudSim Plus Java simulation scenarios 
is available here](http://cloudsimplus.org/docs/CloudSim-and-CloudSimPlus-Comparison.html).

To update your simulations to use the CloudSim Plus you have to change the way that some objects are instantiated, 
because some new interfaces were introduced to follow the "program to an interface, 
not an implementation" recommendation and also to increase [abstraction](https://en.wikipedia.org/wiki/Abstraction_(software_engineering)). 
These new interfaces were also crucial to implement the [Null Object Pattern](https://en.wikipedia.org/wiki/Null_Object_pattern) 
to try avoiding `NullPointerException`s.

The initialization of the simulation is not performed by the static `CloudSim.startSimulation` method anymore, which required a lot of parameters.
Now you have just to instantiate a `CloudSim` object using the default, no-arguments constructor, as shown below. 
This instance is used in the constructor of `DatacenterBroker` and `Datacenter` objects: 

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

The way you instantiate a host has changed too. The classes `RamProvisionerSimple` and `BwProvisionerSimple` don't exist anymore. 
Now you just have the generic class `ResourceProvisionerSimple` and you can just use its default no-args constructor. 
RAM and bandwidth capacity of the host now are given in the constructor, as it already was for storage. 
A `VmScheduler` constructor doesn't require any parameter. You don't need to set an ID for each Host, since
if one is not given, when the List of hosts is attached to a Datacenter, it will generate an ID for those hosts. 
Instantiating a host should be now similar to:

```java
long ram = 20480; //in MB
long bw = 1000000; //in Megabits/s
long storage = 1000000; //in MB
Host host = new HostSimple(ram, bw, storage, pesList);
host.setRamProvisioner(new ResourceProvisionerSimple())
    .setBwProvisioner(new ResourceProvisionerSimple())
    .setVmScheduler(new VmSchedulerTimeShared());
``` 

Additionally, the interface `Storage` was renamed to `FileStorage` and its implementations are 
`SanStorage` and `HarddriveStorage`, that can be used as before. 
Finally, since the packages were reorganized, you have to adjust them. 
However, use your IDE to correct the imports for you. 
A complete and clear example was presented in the <a href="#a-minimal-and-complete-simulation-example">Examples</a> section above.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="general-features"></a>

# 7. General Features of the Framework

CloudSim Plus supports modeling and simulation of:

* large scale Cloud computing data centers;
* virtualized server hosts, with customizable policies for provisioning host resources to virtual machines;
* data center network topologies and message-passing applications;
* federated clouds;
* user-defined policies for allocation of hosts to virtual machines and policies for allocation of host resources to virtual machines.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="publications"></a>

# 8. CloudSim Plus Publications

1. M. C. Silva Filho, R. L. Oliveira, C. C. Monteiro, P. R. M. Inácio, and M. M. Freire. [CloudSim Plus: a Cloud Computing Simulation Framework Pursuing Software Engineering Principles for Improved Modularity, Extensibility and Correctness,](https://doi.org/10.23919/INM.2017.7987304) in IFIP/IEEE International Symposium on Integrated Network Management, 2017, p. 7. If you are using CloudSim Plus in your research, please make sure you cite that paper. You can check the paper presentation [here](http://cloudsimplus.org/docs/presentation/).
2. White Paper. [CloudSim Plus: A Modern Java 8 Framework for Modeling and Simulation of Cloud Computing Infrastructures and Services](https://github.com/manoelcampos/cloudsim-plus/blob/master/docs/cloudsim-plus-white-paper.pdf). 2016.
3. R. L. Oliveira. [Virtual Machine Allocation in Cloud Computing Environments based on Service Level Agreements](docs/MScDissertation-RaysaOliveira.pdf) (only in Portuguese). Master's Dissertation. University of Beira Interior, 2017 (Supervisor: M. M. Freire).
  
<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="projects"></a>

# 9. Related Projects

Here, it's presented a list of some projects based on CloudSim Plus, which trust in its accuracy, performance, maintainability and extensibility.
If you want your project to be listed here, send us a Pull Request. Make sure your project has a descriptive README.

1. [PureEdgeSim: A simulation toolkit for performance evaluation of Fog and pure Edge computing environments.](https://github.com/CharafeddineMechalikh/PureEdgeSim)
1. [CloudSim Plus Py4j gateway: building CloudSim Plus simulations in Python](https://github.com/pkoperek/cloudsimplus-gateway)
1. [RECAP Discrete Event Simulation Framework an extension for CloudSimPlus](https://bitbucket.org/RECAP-DES/recap-des/src/master/)
1. [CloudSim Plus Automation: defining CloudSim Plus simulation scenarios into a YAML file.](http://manoelcampos.github.io/cloudsim-plus-automation/)
1. [LEAF: Simulator for modeling Large Energy-Aware Fog computing environments](https://github.com/birnbaum/LEAF)
1. [EPCSAC: Extensible Platform for Cloud Scheduling Algorithm Comparison.](https://github.com/TNanukem/EPCSAC)

<p align="right"><a href="#top">:arrow_up:</a></p>

# 10. Contributors

## Code Contributors

This project exists thanks to all the people who contribute. [[Contribute](CONTRIBUTING.md)].
<a href="https://github.com/manoelcampos/cloudsim-plus/graphs/contributors"><img src="https://opencollective.com/cloudsim-plus/contributors.svg?width=890&button=false" /></a>

## Financial Contributors

Become a financial contributor and help us sustain our community. [[Contribute](https://opencollective.com/cloudsim-plus/contribute)]

### Individuals

<a href="https://opencollective.com/cloudsim-plus"><img src="https://opencollective.com/cloudsim-plus/individuals.svg?width=890"></a>

### Organizations

Support this project with your organization. Your logo will show up here with a link to your website. [[Contribute](https://opencollective.com/cloudsim-plus/contribute)]

<a href="https://opencollective.com/cloudsim-plus/organization/0/website"><img src="https://opencollective.com/cloudsim-plus/organization/0/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/1/website"><img src="https://opencollective.com/cloudsim-plus/organization/1/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/2/website"><img src="https://opencollective.com/cloudsim-plus/organization/2/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/3/website"><img src="https://opencollective.com/cloudsim-plus/organization/3/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/4/website"><img src="https://opencollective.com/cloudsim-plus/organization/4/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/5/website"><img src="https://opencollective.com/cloudsim-plus/organization/5/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/6/website"><img src="https://opencollective.com/cloudsim-plus/organization/6/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/7/website"><img src="https://opencollective.com/cloudsim-plus/organization/7/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/8/website"><img src="https://opencollective.com/cloudsim-plus/organization/8/avatar.svg"></a>
<a href="https://opencollective.com/cloudsim-plus/organization/9/website"><img src="https://opencollective.com/cloudsim-plus/organization/9/avatar.svg"></a>

<a id="license"></a>

# 11. License

This project is licensed under [GNU GPLv3](http://www.gnu.org/licenses/gpl-3.0), as defined inside CloudSim 3 source files.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="contributing"></a>

# 12. Contributing

You are welcome to contribute to the project. 
However, make sure you read the [contribution guide](CONTRIBUTING.md) before starting. 
The guide provides information on the different ways you can contribute, 
such as by requesting a feature, reporting an issue, fixing a bug or providing some new feature.

<p align="right"><a href="#top">:arrow_up:</a></p>
