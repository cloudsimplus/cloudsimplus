# Datacenter Trace Files in the PlanetLab Format

This directory contains some workload files that can be used to create Cloudlets to simulate execution of jobs from a PlanetLab real Datacenter trace files.
These files are originated from [CloudSim](http://cloudbus.org/cloudsim).

Each sub-directory contains traces from a specific date. Each file contains CPU utilization measured at every 5 minutes in PlanetLab VMs.
The [UtilizationModelPlanetLab](cloudsim-plus/src/main/java/org/cloudbus/cloudsim/utilizationmodels/UtilizationModelPlanetLab.java) class can be used to read these values and define a CPU Utilization Model to be used for Cloudlets.