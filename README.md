# CloudSim++ A CloudSim fork for Modeling and Simulation of Cloud Computing Infrastructures and Services that focuses on highly cohesive and low coupled components
[![Build Status](https://img.shields.io/travis/manoelcampos/cloudsim/master.svg)](https://travis-ci.org/manoelcampos/cloudsim) [![Coverage Status](https://coveralls.io/repos/github/manoelcampos/cloudsim/badge.svg?branch=master)](https://coveralls.io/github/manoelcampos/cloudsim?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/3f132b184d5e475dbbcd356ee84499fc)](https://www.codacy.com/app/manoelcampos/cloudsim?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=manoelcampos/cloudsim&amp;utm_campaign=Badge_Grade) [![LGPL licensed](https://img.shields.io/badge/license-LGPL-blue.svg)](LICENSE)

# Introduction

CloudSim++ is a fork of [CloudSim 3](https://github.com/Cloudslab/cloudsim/tree/20a7a55e537181489fcb9a2d42a3e539db6c0318) that was reengineered primarily to avoid code duplication for improved [code reuse](https://en.wikipedia.org/wiki/Code_reuse), to clean code and ensure compliance with software engineering principles and recomendations, aiming to provide a more extensible, less [coupled](https://en.wikipedia.org/wiki/Coupling_(computer_programming)), more [cohesive](https://en.wikipedia.org/wiki/Cohesion_(computer_science)) cloud simulation tool.  

It focuses on refactorings to reduce code duplication, increase usage of software engeneering standards and recommendations such as [Design Patterns](https://en.wikipedia.org/wiki/Software_design_pattern), [SOLID principles](https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)) and other ones such as [KISS](https://en.wikipedia.org/wiki/KISS_principle) and [DRY](https://pt.wikipedia.org/wiki/Don't_repeat_yourself).


# Why should I care? I just want to use the simulator. :neutral_face:

Well, the design of the tool has a direct impact when you need to extend it to include some feature for your simulations. The simulator provides a set of classes such as Vm Schedulers, Cloudlet Schedulers, Vm Allocation Policies, Resource Provisioners, Utilization Models, Power Models and Datacenter Brokers that implement basic algorithms for every one of these  features. For instance, the `VmAllocationPolicySimple` class implements a Worst Fit
policy that selects the PM wich less processor cores in use to host the VM, and in fact it is the only policy available. 

Usually you have to perform your own implementations of these features, such as a Best Fit VM allocation policy, a resource Utilization Model with an upper threshold or a Datacenter Broker that selects the best Datacenter to submit a VM.

Considering that, several software engineering principles aim to ease the task of creating  new classes to implement those features. They also try to avoid forcing you to change  core classes of the simulator in order to introduce a feature you need to implement.
Changing these core classes is a bad practice, once you will not be able to automatically update your  project to new versions of the simulator without losing your changes or struggle to fix merge conficts.  

And as I have seen in the forums that I've attended, many times users have to perform these changes in core classes just to implement some specific features they need. By this way, I think those problems are enough reasons that show the need of a new reengineered version of the simulator.  

# OK, but I'm just wondering what are the real contributions of CloudSim++ :blush:

Firstly, there is a huge amount of changes that makes CloudSim++ **NOT BACKWARD COMPATIBLE** with original CloudSim. However, to port your CloudSim simulations to CloudSim++ can be relatively easy, as it will be presented further. 

Accordingly, the main contributions of CloudSim++ are as follows.

## Improved class hierarchy and code that is easier to understand

- **Classes were moved to new meangnifull packages in order to ease the process of finding a class that represents a given behaviour that you want to use or extend**. Some new packages are:
  	- [org.cloudbus.cloudsim.allocationpolicies](modules/cloudsim/src/main/java/org/cloudbus/cloudsim/allocationpolicies) for VmAllocationPolicy classes that define how a PM is selected to host a VM.
  	- [org.cloudbus.cloudsim.brokers](modules/cloudsim/src/main/java/org/cloudbus/cloudsim/brokers) for DatacenterBroker classes that defines the policies for submission of customer VMs and cloudlets.
  	- [org.cloudbus.cloudsim.resources]((modules/cloudsim/src/main/java/org/cloudbus/cloudsim/resources) for resources such as CPU and cores (Processor Elements - PEs), RAM, Hard Drive Storage, SAN Storage, etc.
  	- [org.cloudbus.cloudsim.schedulers](modules/cloudsim/src/main/java/org/cloudbus/cloudsim/schedulers) for VmScheduler and CloudletScheduler classes that defines how the execution of cloudlets and VMs are scheduled in the processor.
  	- [org.cloudbus.cloudsim.utilizationmodels](modules/cloudsim/src/main/java/org/cloudbus/cloudsim/utilizationmodels) for UtilizationModel classes that define how a cloudlet uses physical resources.
- **More meaningful class and method names to provide clear understanding of the responsiblity of each one**: usually you have to look at classes and their methods to figure out if there is a feature you want to use. Not rarely you have to go through the documentation to understand what is the responsibility of a given class or method. Using more specific and clear names frequently makes you to instantaneously find out what is the method or class you need. It thus relieves you to read the documentation to start coding (*despite it is really important to read the documentation,
sometimes you don't have time to go through the details*).

## Reusable and standards conforming code

- **Cleaner code that is easier to understand, maintain and test**: a cleaner, clear, well organized code that always follows the same code standards and formatting is easier to understand. And talking about an open source project, the best way to understand how the software works and how you can extend it is reading the source code. If is follows the [clean code](http://cleancoder.com) practice, [Design Patterns](https://en.wikipedia.org/wiki/Software_design_pattern) and [SOLID principles](https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)), it will be easier to understand.
- **Division of very long and confuse methods into small, clean and very meaningful ones that usually makes just one thing**: when using the approach of reading the code to understand how it works, it is very difficult to figure out what is happening into a method with dozens or hundreds lines. And I'm not even talking about the problems to maintain such methods. Thus, little methods with just one responsibility and with meangniful names are easier to maintain. Further, they improve reuse and extension, the most important parts for you that are creating cloud simulations.
- **Improved class hierarchy in order to follow the [Interface Segregation Principle (ISP)](https://en.wikipedia.org/wiki/Interface_segregation_principle) and increased reausability and facility for extension, following the [Open/Closed Principle (OCP)](https://en.wikipedia.org/wiki/Open/closed_principle)****: the ISP states that ["no client should be forced to depend on methods it does not use"](https://en.wikipedia.org/wiki/Interface_segregation_principle#cite_note-ASD-1) and the OCP states that ["software entities (classes, modules, functions, etc.) should be open for extension, but closed for modification"](https://en.wikipedia.org/wiki/Open/closed_principle#cite_note-1). These principles together impacts directly developers that need to extend some simulator class to implement a desired behaviour (such as a specific CloudletScheduler). The ISP preach that you should have a great number of small classes following the [Single Responsibility Principle (SRP)](https://en.wikipedia.org/wiki/Single_responsibility_principle) instead of a small set of big classes. By this way, if you need to extend a given behaviour, usually you will have to extend a specific and little class, that has less code for you to worry about. By the other hand, the OCP relieves you of changing simulator classes to include a desired behaviour. Thus, it focuses on trying to give you the serenity to change just your code. 
- **Both use of OCP and ISP to try to avoid changing base classes to include some new behaviour
  and the bad [copy-and-paste anti-pattern](https://sourcemaking.com/antipatterns/cut-and-paste-programming)**: copy-and-paste is a bad practice that degrades the source code along the time. It makes difficulty to correct bugs when there may be several copies of the problematic code. The duplicated code has to be tested several times, what usually leads to neglecting them. Further, along the time, the number of duplicated just increases. 

## Improved documentation

- **Entirely documentation review and update**, improving explanation of classes and method responsibilities, fixing issues, correcting broken links, updating documentation that didn't reflect methods and class's responsibility anymore.
- **Documentation extension** to make clear key points of classes or methods.
- **Removal of duplicated documentation** that just gets out-of-date along the time. Now, subclasses that had documentation copied from the super class, just inherit the documentation of extends it using the javadoc [@inheritdoc](http://docs.oracle.com/javase/6/docs/technotes/tools/solaris/javadoc.html#inheritingcomments) tag.
- **Inclusion of some missing documentation** or documentation that just repeated the name of a method or attribute. For instance "This is the id", doesn't help in anything.

## Completely new Test Suites

The original CloudSim already has some unit test suites, however, there isn't coverage reports enabled in the project. After enabling these reports, it was realized that just 20% of the code was being covered by unit tests. Tests are really important to try ensuring that the software works as expected and gives more security and peace of mind to refactor the code, perform changes and fix bugs :pray:. 
After all, a broad code coverage also tries to ensure that you are getting accurate results from your simulations.

Finally, an entire simulation is very complex, integrating several classes that communicate among themselves. Once, unit tests just check
isolated method calls, integration/functional tests are required to validate entire simulation scenarios.
This project introduces a set of such tests into the simulator in order to increase simulation accuracy.
Thus, the contributions in this area are as follows:

- Functional/integration tests using JUnit to test how a set of classes works together and even overall simulation scenarios.
- Continuous integration using [Travis](http://travis-ci.org) services (see the badge at the top of this file).
- Inclusion of Maven Coverage Plugin to starting tracking the percentage of code that is being coverage by unit tests.
- Inclusion of public code coverage report using [Coveralls](http://coveralls.io) services (see the badge at the top of this file). The code coverage raised from 20% to [![Coverage Status](https://coveralls.io/repos/github/manoelcampos/cloudsim/badge.svg?branch=master)](https://coveralls.io/github/manoelcampos/cloudsim?branch=master).
- Bug fixes.

## New examples

New concise and easy to understand examples of features that have lots of questions at forums were included, such as:
- [Dynamic creation of cloudlets based on workload traces](modules/cloudsim-examples/src/main/java/org/cloudbus/cloudsim/examples/workload/) - the example dynamically create cloudlets according to the data of a workload trace file. VMs are created to fulfil cloudlets' requirements and, in its turn, Hosts are created to fulfil VMs' requirements. It may not represent a real world use case, but the example can be adapted to developer needs.  
- [VM migration](/modules/cloudsim-examples/src/main/java/org/cloudbus/cloudsim/examples/migration/) - the example performs VM migrations based on Host utilization threshold. 
- Implementation of custom DatacenterBroker and VmAllocationPolicy for the given examples.

## Updated to Java 8

The [cloudsim module](modules/cloudsim), that represents the Cloud Simulation API, now requires the JDK 8 and makes intensive use of [Lambda Expressions, Streams and Functional Programming](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html) of the Java 8. However, [cloudsim-examples](modules/cloudsim-examples) project was updated just from Java 6 to 7 aiming to provide yet simple examples for beginner Java programmers, but less verbose code than Java 6.

The amazing Java 8 features allow developers to drastically reduce the number of lines to implement basic features such as iteration, filtering,
sorting and processing lists, as well as reducing the number of [boilerplate code](https://en.wikipedia.org/wiki/Boilerplate_code) required 
just to use classes having a single method.

For instance, see the code below to create a Thread in Java 7:

```java
Runnable task1 = 
    new Runnable(){
        @Override
        public void run() {
            //Your code to be run by the thread
        }
    };     
        
new Thread(task1).start();
```

In Java 8 you can just write:

```java
new Thread(() -> {
    //Your code to be run by the thread
}).start();
```

This makes the code clearer to understand and less verbose. Further, the new Stream API provides a complete set of features that even increase performance, such as allowing the [parallel processing of lists](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html#section5).


For more information about the changes and features included in this release, please read the [CHANGELOG](CHANGELOG.md) file and the [cloudsim-examples](modules/cloudsim-examples) project.

# But why another CloudSim fork? :unamused:

I know what you are thinking: it would be better to pull a request to the original CloudSim repository in order to really contribute to the project, benefiting everybody.

Well, I strongly agree with you and in fact I tried that. However, the original CloudSim moved on to a new major release, including a completely new set of classes to provide Container as a Service simulations, before the changes being merged to the official repository. 
By this way, all the work performed here was not incorporated to allow the new features to be developed using this redesigned version.
And unfortunately, there are several months of hard work that would need to be replicated in order to merge both projects.

# Cloud Computing Simulations

Cloud computing is the leading technology for delivery of reliable, secure, fault-tolerant, sustainable, and scalable computational services.

For assurance of such characteristics in cloud systems under development, it is required timely, repeatable, and controllable methodologies for evaluation of new cloud applications and policies before actual development of cloud products. Because utilization of real testbeds limits the experiments to the scale of the testbed and makes the reproduction of results an extremely difficult undertaking, simulation may be used.

CloudSim goal is to provide a generalized and extensible simulation framework that enables modeling, simulation, and experimentation of emerging Cloud computing infrastructures and application services, allowing its users to focus on specific system design issues that they want to investigate, without getting concerned about the low level details related to Cloud-based infrastructures and services.

The original CloudSim version is developed in [the Cloud Computing and Distributed Systems (CLOUDS) Laboratory](http://cloudbus.org/), at [the Computer Science and Software Engineering Department](http://www.csse.unimelb.edu.au/) of [the University of Melbourne](http://www.unimelb.edu.au/).

# Main features of the simulator

  * support for modeling and simulation of large scale Cloud computing data centers
  * support for modeling and simulation of virtualized server hosts, with customizable policies for provisioning host resources to virtual machines
  * support for modeling and simulation of energy-aware computational resources
  * support for modeling and simulation of data center network topologies and message-passing applications
  * support for modeling and simulation of federated clouds
  * support for dynamic insertion of simulation elements, stop and resume of simulation
  * support for user-defined policies for allocation of hosts to virtual machines and policies for allocation of host resources to virtual machines

# Download

The download package contains all the source code, examples, jars, and API html files.

# Publications about the original CloudSim version

  * Anton Beloglazov, and Rajkumar Buyya, [Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers](http://beloglazov.info/papers/2012-optimal-algorithms-ccpe.pdf), Concurrency and Computation: Practice and Experience, Volume 24, Number 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012.
  * Saurabh Kumar Garg and Rajkumar Buyya, [NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations](http://www.cloudbus.org/papers/NetworkCloudSim2011.pdf), Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011.
  * **Rodrigo N. Calheiros, Rajiv Ranjan, Anton Beloglazov, Cesar A. F. De Rose, and Rajkumar Buyya, [CloudSim: A Toolkit for Modeling and Simulation of Cloud Computing Environments and Evaluation of Resource Provisioning Algorithms](http://www.buyya.com/papers/CloudSim2010.pdf), Software: Practice and Experience (SPE), Volume 41, Number 1, Pages: 23-50, ISSN: 0038-0644, Wiley Press, New York, USA, January, 2011. (Preferred reference for CloudSim)**
  * Bhathiya Wickremasinghe, Rodrigo N. Calheiros, Rajkumar Buyya, [CloudAnalyst: A CloudSim-based Visual Modeller for Analysing Cloud Computing Environments and Applications](http://www.cloudbus.org/papers/CloudAnalyst-AINA2010.pdf), Proceedings of the 24th International Conference on Advanced Information Networking and Applications (AINA 2010), Perth, Australia, April 20-23, 2010.
  * Rajkumar Buyya, Rajiv Ranjan and Rodrigo N. Calheiros, [Modeling and Simulation of Scalable Cloud Computing Environments and the CloudSim Toolkit: Challenges and Opportunities](http://www.cloudbus.org/papers/CloudSim-HPCS2009.pdf), Proceedings of the 7th High Performance Computing and Simulation Conference (HPCS 2009, ISBN: 978-1-4244-4907-1, IEEE Press, New York, USA), Leipzig, Germany, June 21-24, 2009.