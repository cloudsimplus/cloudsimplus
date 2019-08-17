# Performance Results

This document provides some data about the performance of different simulation
scenarios in CloudSim and CloudSim Plus. 
Each row in the tables below is the results from a single simulation.
This way, they aren't scentifically valid and are only intended to provide a 
glimpse of how the latest versions of CloudSim Plus outperform CloudSim.

The results were generated using [CloudSim Plus Automation](http://github.com/manoelcampos/cloudsim-plus-automation) tool 
in a computer powered by a dual-core 2.8 GHz Hyper-Threading Intel i7-4558U processor.
Each results was generated from a simulation scenario defined in a YML file.


## Cloudlet's UtilizationModel

- Cloudlet's UtilizationModel for CPU: UtilizationModelStochastic (1 instance for each Cloudlet, history enabled)
- Scenario File: [CloudEnvironment6.yml](https://github.com/manoelcampos/cloudsim-plus-automation/blob/master/CloudEnvironment6.yml)

| Framework          |Simulation Time (min)|VmAllocationPolicy|DCs|Hosts|VMs   |Cloudlets|
|--------------------|---------------------|------------------|---|-----|------|---------|
| CloudSim 4.0.0     |21.7                 |Simple (WorstFit) |1  |20000|40000 |50000    |
| CloudSim 4.0.0     |18.6                 |BestFit**         |1  |20000|40000 |50000    |
| CloudSim 4.0.0     |13.3                 |FirstFit**        |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|14.8                 |Simple (WorstFit) |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|15.2                 |BestFit**         |1  |20000|40000 |50000    |
| CloudSim Plus 4.3.5|&nbsp;&nbsp;2.4      |FirstFit**        |1  |20000|40000 |50000    |

\** *Only officially available in CloudSim Plus.*
