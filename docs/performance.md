# Performance Results

This document provides some data about the performance of different simulation
scenarios in CloudSim and CloudSim Plus. 
Each row in the tables below is the results from a single simulation.
This way, they aren't scentifically valid and are used only to provide a 
glimpse of how the latest versions of CloudSim Plus outperforms CloudSim.

The results were generated using [CloudSim Plus Automation](http://github.com/manoelcampos/cloudsim-plus-automation) tool 
in a computer powered by a dual-core 2.8 GHz Hyper-Threading Intel i7-4558U processor.
Each results was generated from a simulation scenario defined in a YML file.


## Cloudlet's UtilizationModel

- Cloudlet's UtilizationModel for CPU: UtilizationModelStochastic (1 instance for each Cloudlet)
- Scenario File: [CloudEnvironment6.yml](https://github.com/manoelcampos/cloudsim-plus-automation/blob/master/CloudEnvironment6.yml)

| Framework          |Simulation Time (min)|VmAllocationPolicy|DCs|Hosts|VMs   |Cloudlets|
|--------------------|---------------------|------------------|---|-----|------|---------|
| CloudSim 4.0.0     |21.74                |Simple (WorstFit) |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.2|38.83                |Simple (WorstFit) |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|14.88                |Simple (WorstFit) |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|15.25                |BestFit**         |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|&nbsp;&nbsp;2.47     |FirstFit**        |1  |20000|40000 |50000    |

\** *Only available in CloudSim Plus.*
