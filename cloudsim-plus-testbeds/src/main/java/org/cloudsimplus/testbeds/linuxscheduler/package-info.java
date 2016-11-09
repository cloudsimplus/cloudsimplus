/**
 * The package contains a set of experiments to compare the {@link org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared},
 * that has an oversimplified implementation of a time-shared scheduler, and the new CloudSim Plus
 * {@link org.cloudbus.cloudsim.schedulers.CloudletSchedulerCompletelyFair} class that provides
 * a basic implementation of the
 * <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a> used by Linux Kernel.
 *
 * <p>The package provides two {@link org.cloudsimplus.testbeds.ExperimentRunner},
 * one for each of the experiments. Each runner has a main method
 * that allows to start a specific testbed. A testbed is a set of experiments executed
 * a given number of times defined by the runner class.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.testbeds.linuxscheduler;
