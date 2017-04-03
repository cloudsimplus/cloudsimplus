/**
 * Implements a set of testbeds in a repeatable manner, allowing a researcher to execute several simulation runs
 * for a given experiment and collect statistical data using a scientific approach.
 * It represents real testbeds implemented to assess CloudSim Plus features, providing relevant results.
 *
 * <p>Each package contains the classes for a specific testbed that is composed of:
 * <ul>
 *     <li>a {@link org.cloudsimplus.testbeds.SimulationExperiment} that implements a single run
 *     of a specific simulation scenario and usually has a main method just to
 *     check the execution of the experiment isolatedly</li>
 *     <li>a {@link org.cloudsimplus.testbeds.ExperimentRunner} that is accountable for
 *     running a specific SimulationExperiment different times with;
 *     different configurations (such as seeds, number of VMs, Cloudlets, etc),
 *     showing scientific results at the end of the execution.</li>
 * </ul>
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.testbeds;
