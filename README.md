<a id="top"></a>

<p align="center">
<b><a href="#overview">Overview</a></b>
|
<b><a href="#exclusive-features">Exclusive Features</a></b>
|
<b><a href="#structure">Structure</a></b>
|
<b><a href="#usage">How to use</a></b>
|
<b><a href="#example">Example</a></b>
|
<b><a href="#docs-help">Docs and Help</a></b>
|
<b><a href="#consulting">Consulting</a></b>
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

# 1. Overview [![Open in Gitpod (You need to log in at gitpod.io first)](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/manoelcampos/cloudsimplus) 

[![Consulting](https://img.shields.io/badge/Consulting-Click%20here-brightgreen)](#consulting) [![Build Status](https://github.com/manoelcampos/cloudsimplus/actions/workflows/maven.yml/badge.svg)](https://github.com/manoelcampos/cloudsimplus/actions/workflows/maven.yml) [![Codacy Code Quality](https://app.codacy.com/project/badge/Grade/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/gh/manoelcampos/cloudsimplus/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=manoelcampos/cloudsimplus&amp;utm_campaign=Badge_Grade) [![Codacy Code Coverage](https://app.codacy.com/project/badge/Coverage/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/gh/manoelcampos/cloudsimplus/dashboard?utm_source=github.com&utm_medium=referral&utm_content=manoelcampos/cloudsimplus&utm_campaign=Badge_Coverage) [![Maven Central](https://img.shields.io/maven-central/v/org.cloudsimplus/cloudsim-plus.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.cloudsimplus%22%20AND%20a:%22cloudsim-plus%22) [![Documentation Status](https://readthedocs.org/projects/cloudsimplus/badge/?version=latest)](http://cloudsimplus.rtfd.io/en/latest/?badge=latest) [![GPL licensed](https://img.shields.io/badge/license-GPL-blue.svg)](http://www.gnu.org/licenses/gpl-3.0) [![GitHub Repo stars](https://img.shields.io/github/stars/manoelcampos/cloudsim-plus?label=Contribute.%20Star%20it.)](https://github.com/manoelcampos/cloudsimplus) [![Twitter Follow](https://img.shields.io/twitter/follow/cloudsimplus?style=social)](http://twitter.com/cloudsimplus)

CloudSim Plus is a modern, up-to-date, full-featured and fully documented **Java 17** simulation framework. It's easy to use and extend, enabling modeling, simulation, and experimentation of Cloud computing infrastructures and application services. 
It allows developers to focus on specific system design issues to be investigated, without concerning the low-level details related to Cloud-based infrastructures and services.
 
CloudSim Plus is a fork of CloudSim 3, re-engineered primarily to avoid code duplication, provide [code reuse](https://en.wikipedia.org/wiki/Code_reuse) and ensure compliance with software engineering principles and recommendations for extensibility improvements and accuracy. It's currently the state-of-the-art in cloud computing simulation framework. 

The efforts dedicated to this project have been recognized by the [EU/Brasil Cloud FORUM](https://eubrasilcloudforum.eu). 
A post about CloudSim Plus is available at 
[this page of the Forum](https://eubrasilcloudforum.eu/en/instituto-federal-de-educação-do-tocantins-brazil-instituto-de-telecomunicações-portugal-and), including a White Paper available in the [Publications Section](#publications).

CloudSim Plus started through a partnership between the [Instituto de Telecomunicações (IT, Portugal)](http://www.it.pt), 
the [Universidade da Beira Interior (UBI, Portugal)](http://www.ubi.pt) 
and the [Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil)](http://www.ifto.edu.br). 
It was partially supported by the Portuguese [Fundação para a Ciência e a Tecnologia (FCT)](https://www.fct.pt) 
and by the [Brazilian foundation Coordenação de Aperfeiçoamento de Pessoal de Nível Superior (CAPES)](http://www.capes.gov.br).

**NOTE**
---
> If you are using CloudSim Plus in your research, please make sure you cite this paper: M. C. Silva Filho, R. L. Oliveira, C. C. Monteiro, P. R. M. Inácio, and M. M. Freire. [CloudSim Plus: a Cloud Computing Simulation Framework Pursuing Software Engineering Principles for Improved Modularity, Extensibility and Correctness,](https://doi.org/10.23919/INM.2017.7987304) in IFIP/IEEE International Symposium on Integrated Network Management, 2017, p. 7.

**IMPORTANT**
---
- Developing and maintaining this project takes a huge effort. This way, any kind of [contribution](#contributing) is encouraged. Show your support by giving it a star :star: using the button at the top of the GitHub page. It takes no time, helps promoting the project and keeps it evolving. Thanks in advance :clap:.
- If you aren't intending to make changes and contribute back to the project, you shouldn't fork it. Your fork tends to become obsolete as the project is updated. 
- If you're willing to use the framework to develop your own project on top of it, creating a fork is the worst way. You aren't supposed to change the framework code to implement your project, but to extend it by creating some subclasses. Unless you are planning to contribute your changes back, you'll end up with an incompatible and obsolete version of the framework. The project is constantly evolving and bugfixes are a priority. Your fork with personal changes will miss those updates and high performance improvements.
- If you've just came across this project and wants to check it later, star it :star:, we appreciate :clap:.
---

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="exclusive-features"></a>

# 2. 🧰 Main Exclusive Features

CloudSim Plus provides lots of exclusive features, from the most basic ones to build simple simulations, 
to advanced features for simulating more realistic cloud scenarios: 

1. It is easier to use. [A complete and easy-to-understand simulation scenario can be built in few lines of code.](#example)
2. Multi-cloud simulations with inter-datacenter VM migrations ([#361](https://github.com/manoelcampos/cloudsimplus/issues/361)).
3. Creation of joint power- and network-aware simulations ([#45](https://github.com/manoelcampos/cloudsim-plus/issues/45)).
4. Vertical ([#7](https://github.com/manoelcampos/cloudsim-plus/issues/7)) and Horizontal VM scaling ([#41](https://github.com/manoelcampos/cloudsim-plus/issues/41)).
5. [Highly accurate power usage computation](https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/power/PowerExampleSchedulingInterval.java) ([#153](https://github.com/manoelcampos/cloudsim-plus/issues/153)).
6. [Built-in computation of CPU utilization history and energy consumption for VMs (and Hosts)](https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/power/PowerExample.java) ([#168](https://github.com/manoelcampos/cloudsim-plus/issues/168)).
7. [Virtual Memory and Reduced bandwidth allocation](https://github.com/manoelcampos/cloudsim-plus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/resourceusage/VirtualMemoryForRequestedRamHigherThanAvailableExample.java) when RAM and BW are oversubscribed. ([#170](https://github.com/manoelcampos/cloudsim-plus/issues/170)). 
8. [Automatically power Hosts on and off according to demand](https://github.com/manoelcampos/cloudsimplus/blob/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/power/HostActivationExample.java) ([#128](https://github.com/manoelcampos/cloudsim-plus/issues/128)) and support defining a startup and shutdown delay/power consumption ([#238](https://github.com/manoelcampos/cloudsim-plus/issues/238)).
9. [Parallel execution of simulations in multi-core computers](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/ParallelSimulationsExample.java), allowing multiple simulations to be run simultaneously in an isolated way ([#38](https://github.com/manoelcampos/cloudsim-plus/issues/38)).
10. Delay creation of submitted VMs and [Cloudlets](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/DynamicCloudletsArrival1.java), enabling simulation of dynamic arrival of tasks ([#11](https://github.com/manoelcampos/cloudsim-plus/issues/11), [#23](https://github.com/manoelcampos/cloudsim-plus/issues/23)). 
11. [Allow dynamic creation of VMs and Cloudlets in runtime](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/DynamicCreationOfVmsAndCloudletsExample.java), enabling VMs to be created on-demand ([#43](https://github.com/manoelcampos/cloudsim-plus/issues/43)).
12. [Listeners](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/listeners) to enable simulation configuration, monitoring and data collection.
13. Create [simulations](https://github.com/manoelcampos/cloudsim-plus/tree/master/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/traces/google) from [Google Cluster Data](https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md) 
    trace files. ([#149](https://github.com/manoelcampos/cloudsim-plus/issues/149)).
14. Strongly object-oriented, allowing chained calls such as `cloudlet.getVm().getHost().getDatacenter()` without even worrying about `NullPointerException` ([#10](https://github.com/manoelcampos/cloudsim-plus/issues/10)).
15. Classes and interfaces for implementing [heuristics](http://en.wikipedia.org/wiki/Heuristic) such as [Tabu Search](http://en.wikipedia.org/wiki/Tabu_search), [Simulated Annealing](http://en.wikipedia.org/wiki/Simulated_annealing), [Ant Colony Systems](http://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms) and so on ([example here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/brokers/DatacenterBrokerHeuristicExample.java)).
16. [Implementation of the Completely Fair Scheduler](https://en.wikipedia.org/wiki/Completely_Fair_Scheduler) used in recent versions of the Linux Kernel ([example here](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/schedulers/LinuxCompletelyFairSchedulerExample.java)) ([#58](https://github.com/manoelcampos/cloudsim-plus/issues/58)).
17. [Host Fault Injection and Recovery Mechanism](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/HostFaultInjectionExample1.java) to enable injection of random failures into Hosts CPU cores and replication of failed VMs ([#81](https://github.com/manoelcampos/cloudsim-plus/issues/81)).
18. [Creation of Hosts at Simulation Runtime](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/DynamicHostCreation.java) to enable physical expansion of Datacenter capacity ([#124](https://github.com/manoelcampos/cloudsim-plus/issues/124)).
19. [Enables the simulation to keep running, waiting for dynamic and even random events such as the arrival of Cloudlets and VMs](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/dynamic/KeepSimulationRunningExample.java) ([#130](https://github.com/manoelcampos/cloudsim-plus/issues/130)).
20. TableBuilder objects that are used in all examples and enable printing simulation results in different formats such as Markdown Table, CSV or HTML.
21. Colors log messages and enables filtering the level of messages to print ([#24](https://github.com/manoelcampos/cloudsim-plus/issues/24)). ![](docs/images/log-messages-by-type.png) If you want to just see messages from warning level, call `Log.setLevel(ch.qos.logback.classic.Level.WARN);`
22. [Enables running the simulation synchronously, making it easier to interact with it and collect data inside a loop, as the simulation goes on](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/synchronous/SynchronousSimulationExample1.java). This brings freedom to implement your simulations ([#205](https://github.com/manoelcampos/cloudsim-plus/issues/205)).
23. [Allows placing a group of VMs into the same Host.](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/VmGroupPlacementExample1.java) ([#90](https://github.com/manoelcampos/cloudsim-plus/issues/90)).
24. [Enables Broker to try selecting the closest Datacenter to place VMs, according to their time zone.](/cloudsim-plus-examples/src/main/java/org/cloudsimplus/examples/brokers/DatacenterSelectionByTimeZoneExample.java) ([#212](https://github.com/manoelcampos/cloudsim-plus/issues/212)).
25. It outperforms CloudSim 4, as can be seen in the table below*.

VmAllocationPolicy|CloudSim 4.0.0 Simulation Time (min) |CloudSim Plus 4.3.4 Simulation Time (min) |DCs|Hosts|VMs  |Cloudlets
------------------|-------------------------------------|------------------------------------------|---|-----|-----|---------
Simple (WorstFit) |23.9                                 |15.7                                      |1  |20000|40000|50000
BestFit**         |19.6                                 |15.7                                      |1  |20000|40000|50000
FirstFit**        |13.6                                 |&nbsp;&nbsp;1.3                           |1  |20000|40000|50000

\* *More details and results [here](docs/performance.md).*   \** *Only officially available in CloudSim Plus.*

<a id="structure"></a>

# 3. 🏗 Project's Structure

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

<a id="usage"></a>

# 4. 👩🏽‍💻 How to Use CloudSim Plus 

**:warning: IMPORTANT**
---
- Before trying to use this project, make sure you have JDK 17 installed.


There are 4 ways to use CloudSim Plus. It can be downloaded and executed: 
(i) from [Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cloudsimplus/cloudsim-plus) since you include it as a dependency inside your own project.
(ii) downloading a zip file using the button at the top of this page;
(iii) from the command line; 
(iv) directly from some IDE; 

You can check [this video](https://youtu.be/k2enNoxTYVw) or follow the instructions in one of the next subsections.

<a id="maven"></a>

## 4.1 Adding it as a Dependency into Your Own Project

You can add CloudSim Plus API module (which is the only one required to build simulations) as a dependency inside your own Maven or Gradle project.
This way you can start building your simulations from scratch.

### 4.1.1 Maven

Add the following dependency into the pom.xml file of your own Maven project. 

```xml
<dependency>
    <groupId>org.cloudsimplus</groupId>
    <artifactId>cloudsim-plus</artifactId>
    <!-- Set a specific version or use the latest one -->
    <version>LATEST</version>
</dependency>
```

### 4.1.2 Gradle

Add the following dependency into the build.gradle file of your own Gradle project. 

```groovy
dependencies {
    //Set a specific version or use the latest one
    implementation 'org.cloudsimplus:cloudsim-plus:LATEST'
}
```

## 4.2 Downloading sources via Command Line

Considering that you have [git](https://git-scm.com) and [maven](http://maven.apache.org) installed on your operating system, 
download the project source by cloning the repository issuing the command `git clone https://github.com/manoelcampos/cloudsim-plus.git` 
at a terminal. 

The project has a [bash script](script/bootstrap.sh) you can use to build and run CloudSim Plus examples. 
This is a script for Unix-like systems such as Linux, FreeBSD and macOS.

To run some example, type the following command at a terminal inside the project's root directory: `bash script/bootstrap.sh package.ExampleClassName`.
For instance, to run the `BasicFirstExample` you can type: `bash script/bootstrap.sh org.cloudsimplus.examples.BasicFirstExample`. 

The script checks if it is required to build the project, using maven in this case, making sure to download all dependencies. 
To see which examples are available, just navigate through the [examples directory](/cloudsim-plus-examples/src/main/java/).
To check more script options, run it without any parameter.  
 
## 4.3 By Means of an IDE

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

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="example"></a>

# 5. ⚙️ A Minimal but Complete Simulation Example

In order to build a simulation scenario, you have to create at least:
 
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
List<Cloudlet> cloudletList = new ArrayList<>();
//UtilizationModel defining the Cloudlets use only 50% of any resource all the time
UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
Cloudlet cloudlet0 = new CloudletSimple(10000, 1, utilizationModel);
Cloudlet cloudlet1 = new CloudletSimple(10000, 1, utilizationModel);
cloudletList.add(cloudlet0);
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

The presented results are structured and clear to allow better understanding. 
For example, the image below shows the output for a simulation with two cloudlets (applications).

![Simulation Results](https://github.com/manoelcampos/cloudsim-plus/raw/master/docs/images/simulation-results.png)

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="docs-help"></a>

# 6. 📘❓ Documentation and Help

The project documentation originated from CloudSim was entirely updated and extended. 
You can see the javadoc documentation for classes and their elements directly on your IDE.

The documentation is available online at [ReadTheDocs](http://cloudsimplus.rtfd.io/en/latest/?badge=latest), 
which includes a FAQ and guides.
CloudSim Plus has extended documentation of classes and interfaces and also includes extremely helpful
package documentation that can be viewed directly on your IDE or at the link provided above.
Such a package documentation gives a general overview of the classes used to build a cloud simulation. Also, check the [publications](#publications) section to access published CloudSim Plus papers.

A Google Group forum is available at <https://groups.google.com/group/cloudsim-plus> and you can also use the [Discussions page here](https://github.com/manoelcampos/cloudsimplus/discussions). Do not open an issue to post a question, they will be just deleted.

<p align="right"><a href="#top">:arrow_up:</a></p>

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="consulting"></a>

# 7. 👨🏽‍🏫 Consulting

If you are doing research on cloud computing simulation and facing challenging issues, I've started to offer my consulting services.

I can help you with different kinds of issues and provide specific features for your simulations, including resource allocation, task scheduling, VM placement and migration, metrics computation, process automation, debugging, results analysis, validation and more.

If you have a CloudSim project and want to migrate to CloudSim Plus to benefit from its extensive documentation, active development and support, exclusive features, great accuracy and performance, the consulting can be fit for you too.

**Get the [contact e-mail here](https://github.com/manoelcampos).**

<a id="general-features"></a>

# 8. 🛠 General Features of the Framework

CloudSim Plus supports modeling and simulation of:

* large scale Cloud computing data centers;
* virtualized server hosts, with customizable policies for provisioning host resources to virtual machines;
* data center network topologies and message-passing applications;
* federated clouds;
* user-defined policies for allocation of hosts to virtual machines and policies for allocation of host resources to virtual machines.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="publications"></a>

# 8. 📝 CloudSim Plus Publications

1. M. C. Silva Filho, R. L. Oliveira, C. C. Monteiro, P. R. M. Inácio, and M. M. Freire. [CloudSim Plus: a Cloud Computing Simulation Framework Pursuing Software Engineering Principles for Improved Modularity, Extensibility and Correctness,](https://doi.org/10.23919/INM.2017.7987304) in IFIP/IEEE International Symposium on Integrated Network Management, 2017, p. 7. If you are using CloudSim Plus in your research, please make sure you cite that paper. You can check the paper presentation [here](http://cloudsimplus.org/docs/presentation/).
2. White Paper. [CloudSim Plus: A Modern Java 8 Framework for Modeling and Simulation of Cloud Computing Infrastructures and Services](https://github.com/manoelcampos/cloudsim-plus/blob/master/docs/cloudsim-plus-white-paper.pdf). 2016.
3. R. L. Oliveira. [Virtual Machine Allocation in Cloud Computing Environments based on Service Level Agreements](docs/MScDissertation-RaysaOliveira.pdf) (only in Portuguese). Master's Dissertation. University of Beira Interior, 2017 (Supervisor: M. M. Freire).
  
<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="projects"></a>

# 9. 🧩 Related Projects

Here, it's presented a list of some projects based on CloudSim Plus, which trust in its accuracy, performance, maintainability and extensibility.
If you want your project to be listed here, send us a Pull Request. Make sure your project has a descriptive README.

1. [PureEdgeSim: A simulation toolkit for performance evaluation of Fog and pure Edge computing environments.](https://github.com/CharafeddineMechalikh/PureEdgeSim)
1. [CloudSim Plus Py4j gateway: building CloudSim Plus simulations in Python](https://github.com/pkoperek/cloudsimplus-gateway)
1. [RECAP Discrete Event Simulation Framework an extension for CloudSimPlus](https://bitbucket.org/RECAP-DES/recap-des/)
1. [CloudSim Plus Automation: defining CloudSim Plus simulation scenarios into a YAML file.](https://github.com/manoelcampos/cloudsimplus-automation)
1. [LEAF: Simulator for modeling Large Energy-Aware Fog computing environments](https://github.com/dos-group/leaf-java)
1. [EPCSAC: Extensible Platform for Cloud Scheduling Algorithm Comparison.](https://github.com/TNanukem/EPCSAC)
1. [SatEdgeSim: A Toolkit for Modeling and Simulation of Performance Evaluation in Satellite Edge Computing Environments.](https://github.com/wjy491156866/SatEdgeSim)

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="license"></a>

# 10. ⚖️ License

This project is licensed under [GNU GPLv3](http://www.gnu.org/licenses/gpl-3.0), as defined inside CloudSim 3 source files.

<p align="right"><a href="#top">:arrow_up:</a></p>

<a id="contributing"></a>

# 11. 🤝 Contributing

You are welcome to contribute to the project. 
However, make sure you read the [contribution guide](CONTRIBUTING.md) before starting. 
The guide provides information on the different ways you can contribute, 
such as by requesting a feature, reporting an issue, fixing a bug or providing some new feature.

<p align="right"><a href="#top">:arrow_up:</a></p>
