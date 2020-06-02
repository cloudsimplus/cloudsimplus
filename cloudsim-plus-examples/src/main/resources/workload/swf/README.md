# Datacenter Trace Files in the Standard Workload Format (swf)

This directory contains some workload files that can be used to create Cloudlets to simulate execution of jobs from a real datacenter trace files.
Such files follow the [Standard Workload Format (swf)](http://www.cs.huji.ac.il/labs/parallel/workload/)
from [The Hebrew University of Jerusalem](http://new.huji.ac.il/en).

The swf format includes detailed information about job execution in real datacenters, such as:

- submit time;
- wait time;
- run time;
- number of allocated processors;
- used memory.

These files are read using the [WorkloadFileReader](cloudsim-plus/src/main/java/org/cloudbus/cloudsim/util/WorkloadFileReader.java) class. However, just some of these file fields are read by the class. 
