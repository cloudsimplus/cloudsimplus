/**
 * Provides a set of interfaces and classes to develop heuristics to
 * find suboptimal solutions for problems, considering some
 * utility function that has to be minimized or maximized.
 * Such a function is also called a fitness function, and as the higher the fitness,
 * the better the found solution is.
 *
 * <p>
 * Different heuristics include
 * <a href="http://en.wikipedia.org/wiki/Simulated_annealing">Simulated Annealing</a>,
 * <a href="http://en.wikipedia.org/wiki/Tabu_search">Tabu Search</a> and
 * <a href="http://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms">Ant Colony Optimization</a>.
 * </p>
 *
 * <p>The first introduced heuristic is the {@link org.cloudsimplus.heuristics.CloudletToVmMappingSimulatedAnnealing}
 * that is used by a {@link org.cloudsimplus.brokers.DatacenterBrokerHeuristic}
 * to map Cloudlets to VMs.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.heuristics;
