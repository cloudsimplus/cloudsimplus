# Performance Results

This document provides some data about the performance of different simulation
scenarios in CloudSim and CloudSim Plus. 
Each row in the tables below is the results from a single simulation.
This way, they aren't scientifically valid and are only intended to provide a 
glimpse of how the latest versions of CloudSim Plus outperform CloudSim.

The results were generated using the [Cloud Simulation Comparison project](https://github.com/cloudsimplus/cloud-simulation-comparison),
which relies on [CloudSim Plus Automation](https://github.com/cloudsimplus/cloudsimplus-automation) tool.
The experiments were executed in a personal computer powered by a dual-core 2.8 GHz Hyper-Threading Intel i7-4558U processor with 8GB of RAM.
Each result was generated from a simulation scenario defined in a YML file.

## Datacenter's VmAllocationPolicy

VmAllocationPolicy|CloudSim 4.0.0 Simulation Time (min) |CloudSim Plus 4.3.4 Simulation Time (min) |DCs|Hosts|VMs  |Cloudlets
------------------|-------------------------------------|------------------------------------------|---|-----|-----|---------
Simple (WorstFit) |23.9                                 |15.7                                      |1  |20000|40000|50000
BestFit*          |19.6                                 |15.7                                      |1  |20000|40000|50000
FirstFit*         |13.6                                 |&nbsp;&nbsp;1.3                           |1  |20000|40000|50000

\* *Only officially available in CloudSim Plus.*

## Cloudlet's UtilizationModel

- Cloudlet's UtilizationModel for CPU: UtilizationModelStochastic (1 instance for each Cloudlet, history enabled)
- Scenario File: [scenario6.yml](https://github.com/cloudsimplus/cloud-simulation-comparison/blob/master/scenario6.yml)

| Framework          |Simulation Time (min)| VmAllocationPolicy |DCs|Hosts|VMs   |Cloudlets|
|--------------------|---------------------|--------------------|---|-----|------|---------|
| CloudSim 4.0.0     |21.7                 | Simple (WorstFit)  |1  |20000|40000 |50000    |
| CloudSim 4.0.0     |18.6                 | BestFit*           |1  |20000|40000 |50000    |
| CloudSim 4.0.0     |13.3                 | FirstFit*          |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|14.8                 | Simple (WorstFit)  |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|15.2                 | BestFit*           |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|&nbsp;&nbsp;2.4      | FirstFit*          |1  |20000|40000 |50000    |

\* *Only officially available in CloudSim Plus.*
