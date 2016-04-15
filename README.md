# CloudSim: A Framework For Modeling And Simulation Of Cloud Computing Infrastructures And Services [![Build Status](https://travis-ci.org/manoelcampos/cloudsim.png?branch=reduce_code_duplication)](https://travis-ci.org/manoelcampos/cloudsim) [![Coverage Status](https://coveralls.io/repos/github/manoelcampos/cloudsim/badge.svg?branch=cloudsim-4)](https://coveralls.io/github/manoelcampos/cloudsim?branch=cloudsim-4)

# NOTICE

This is a **non-official proposed CloudSim 4.0** version thas is based on CloudSim 3.0.3 and **IS NOT BACKWARD COMPATIBLE** with such main release (despite simulations using the official version can relatively easy be ported to this new version using some IDE such as NetBeans). CloudSim is divided in some maven projects that are all provided when you download the source code. The [cloudsim-examples](modules/cloudsim) project, that represents the Cloud Simulation API, now requires the JDK 8 and makes intensive use of new Java 8 features such as [Lambda Expressions, Stream and Functional Programming](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html). However, [cloudsim-examples](modules/cloudsim-examples) project were updated just from Java 6 to 7 in order to provide yet simple examples for beginner Java programmers, but less verbose code than Java 6. 

This proposed 4.0 version is in beta stage yet and is mainly focused on refactorings to reduce code duplication, increase usage of 
software engeneering standards and recommendations such as [Design Patterns](https://en.wikipedia.org/wiki/Software_design_pattern), [SOLID principles](https://en.wikipedia.com/wiki/SOLID %28object-oriented_design%29) and other ones such as [KISS](https://en.wikipedia.org/wiki/KISS_principle) and [DRY](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself) in order to provide:
- cleaner code that is easier to understand and maintain
- more meaningful classes and methods names
- division of very long and confuse methods into small, clean and very meaningful ones that usually makes just one thing
- increased reausability and facility for extension
- improved class hiearchy 

It also includes:

- functional/integration tests using JUnit in order to test how a set of classes works together and even overall simulation scenarios
- continuous integration using [Travis](http://travis-ci.org) services (see the badge at the top of this file)
- inclusion of Maven Coverage Plugin to starting tracking the percentage of code that is being coverage by unit tests
- inclusion of public code coverage report using [Coveralls](http://coveralls.io) services (see the badge at the top of this file). The code coverage raised from 20% to [![Coverage Status](https://coveralls.io/repos/github/manoelcampos/cloudsim/badge.svg?branch=cloudsim-4)](https://coveralls.io/github/manoelcampos/cloudsim?branch=cloudsim-4)
- new concise and easy to understand examples of features that have lots of questions at the Google Groups forum, such as dynamic creation of cloudlets based on workload traces; VM migration; and definition of new DatacenterBroker's, VmScheduler's and CloudletScheduler's
- creation of new packages and reorganization of classes 
- bug fixes
- improved and completely updated and clearer documentation
- inclusion of new set of features.

As this release gets a stable stage, it will be performed a pull request for the official CloudSim repository in order to try make this official.
Thus, this version is being earlier released to the community in order to get feedback such as suggestions and bug reports.

For more information about the changes and features included in this release, please read the [change log](CHANGELOG.md) file and the [cloudsim-examples](modules/cloudsim-examples) project.

# INTRODUCTION

Cloud computing is the leading technology for delivery of reliable, secure, fault-tolerant, sustainable, and scalable computational services.

For assurance of such characteristics in cloud systems under development, it is required timely, repeatable, and controllable methodologies for evaluation of new cloud applications and policies before actual development of cloud products. Because utilization of real testbeds limits the experiments to the scale of the testbed and makes the reproduction of results an extremely difficult undertaking, simulation may be used.

CloudSim goal is to provide a generalized and extensible simulation framework that enables modeling, simulation, and experimentation of emerging Cloud computing infrastructures and application services, allowing its users to focus on specific system design issues that they want to investigate, without getting concerned about the low level details related to Cloud-based infrastructures and services.

CloudSim is developed in [the Cloud Computing and Distributed Systems (CLOUDS) Laboratory](http://cloudbus.org/), at [the Computer Science and Software Engineering Department](http://www.csse.unimelb.edu.au/) of [the University of Melbourne](http://www.unimelb.edu.au/).

More information can be found on the [CloudSim's web site](http://cloudbus.org/cloudsim/).


CloudSim is powered by [jProfiler](http://www.ej-technologies.com/products/jprofiler/overview.html).

# Main features #

  * support for modeling and simulation of large scale Cloud computing data centers
  * support for modeling and simulation of virtualized server hosts, with customizable policies for provisioning host resources to virtual machines
  * support for modeling and simulation of energy-aware computational resources
  * support for modeling and simulation of data center network topologies and message-passing applications
  * support for modeling and simulation of federated clouds
  * support for dynamic insertion of simulation elements, stop and resume of simulation
  * support for user-defined policies for allocation of hosts to virtual machines and policies for allocation of host resources to virtual machines


# Download #

The downloaded package contains all the source code, examples, jars, and API html files.

# Publications #

  * Anton Beloglazov, and Rajkumar Buyya, [Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers](http://beloglazov.info/papers/2012-optimal-algorithms-ccpe.pdf), Concurrency and Computation: Practice and Experience, Volume 24, Number 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012.
  * Saurabh Kumar Garg and Rajkumar Buyya, [NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations](http://www.cloudbus.org/papers/NetworkCloudSim2011.pdf), Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011.
  * **Rodrigo N. Calheiros, Rajiv Ranjan, Anton Beloglazov, Cesar A. F. De Rose, and Rajkumar Buyya, [CloudSim: A Toolkit for Modeling and Simulation of Cloud Computing Environments and Evaluation of Resource Provisioning Algorithms](http://www.buyya.com/papers/CloudSim2010.pdf), Software: Practice and Experience (SPE), Volume 41, Number 1, Pages: 23-50, ISSN: 0038-0644, Wiley Press, New York, USA, January, 2011. (Preferred reference for CloudSim)**
  * Bhathiya Wickremasinghe, Rodrigo N. Calheiros, Rajkumar Buyya, [CloudAnalyst: A CloudSim-based Visual Modeller for Analysing Cloud Computing Environments and Applications](http://www.cloudbus.org/papers/CloudAnalyst-AINA2010.pdf), Proceedings of the 24th International Conference on Advanced Information Networking and Applications (AINA 2010), Perth, Australia, April 20-23, 2010.
  * Rajkumar Buyya, Rajiv Ranjan and Rodrigo N. Calheiros, [Modeling and Simulation of Scalable Cloud Computing Environments and the CloudSim Toolkit: Challenges and Opportunities](http://www.cloudbus.org/papers/CloudSim-HPCS2009.pdf), Proceedings of the 7th High Performance Computing and Simulation Conference (HPCS 2009, ISBN: 978-1-4244-4907-1, IEEE Press, New York, USA), Leipzig, Germany, June 21-24, 2009.




[![](http://www.cloudbus.org/logo/cloudbuslogo-v5a.png)](http://cloudbus.org/)