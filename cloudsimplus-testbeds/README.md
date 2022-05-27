# CloudSim Plus Testbeds

This module contains a set of classes that enable you to implement cloud simulations in a repeated manner.
It provide base abstract classes that can be extended to implement a simulation scenario
that is executed multiple times and uses Pseudo Random Number Generators (PRNGs) to
set simulation parameters according to a behavior you want to investigate in your research.

It provides the basic mechanisms to start and manage multiple execution of the 
simulation scenario, controlling the generation of pseudo random numbers
and allowing collection of scientifically valid results to be assessed.

For instance, it allows the application of the [antithetic variates technique](https://en.wikipedia.org/wiki/Antithetic_variates)
and Batch Means Method to reduce variance of obtained results. These classes take care of all the details 
to apply such methods and you just have to set the required parameters.
It also computes confidence intervals for the final results and present
them in a organized way.

Different from the examples module that aims just to show how to use CloudSim Plus features, this module includes more complex simulation scenarios concerned in providing scientifically valid results.