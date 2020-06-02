# Datacenter Trace Files in the PlanetLab Format

This directory contains some workload files that can be used to create Cloudlets to simulate execution of jobs from a PlanetLab real Datacenter trace file.
These files are originated from [CloudSim](http://cloudbus.org/cloudsim).

Each sub-directory contains traces from a specific date. 
Each file contains CPU utilization percentage in scale from 0 to 100, 
measured at every 5 minutes (300 seconds) inside PlanetLab's VMs.
The [UtilizationModelPlanetLab](cloudsim-plus/src/main/java/org/cloudbus/cloudsim/utilizationmodels/UtilizationModelPlanetLab.java) class can be used to read these values and define a CPU Utilization Model for Cloudlets. When the class loads a trace file, it converts the percentage values to a scale between 0 to 1.

In order to reduce build time and to keep the project size small, just one directory of PlanetLab traces was kept. If you want to build simulations using other traces, a fork of the original files is available 
[here](https://github.com/manoelcampos/planetlab-workload-traces).