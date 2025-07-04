/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.heuristics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Provides the methods to be used for implementation of
/// [heuristics](https://en.wikipedia.org/wiki/Heuristic_(computer_science))
/// to find a solution for complex problems where the solution space
/// to search is large. These problems are usually [NP-Hard](https://en.wikipedia.org/wiki/NP-hardness) ones
/// that the time to find a solution increases, for instance, in exponential time.
/// An example of such a problem can be
/// the mapping a set of VMs to existing Hosts or mapping a set of Cloudlets to VMs.
///
/// A heuristic implementation thus provides an approximation of
/// an optimal solution (a suboptimal solution).
///
/// Different heuristics can be implemented, such as
/// [Tabu search](https://en.wikipedia.org/wiki/Tabu_search),
/// [Simulated annealing](https://en.wikipedia.org/wiki/Simulated_annealing),
/// [Hill climbing](https://en.wikipedia.org/wiki/Hill_climbing) or
/// [Ant colony optimization](https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms),
/// to name a few.
///
/// @param <S> the [class of solutions][HeuristicSolution] the heuristic will deal with
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public interface Heuristic<S extends HeuristicSolution<?>> {
    Logger LOGGER = LoggerFactory.getLogger(Heuristic.class.getSimpleName());

    /**
     * A property that implements the Null Object Design Pattern for {@link Heuristic} objects.
     */
    Heuristic NULL = new HeuristicNull();

    /**
     * Computes the acceptance probability to define if a neighbor solution
     * has to be accepted or not, compared to the {@link #getBestSolutionSoFar()}.
     *
     * @return the acceptance probability, in scale from [0 to 1] where:
     * 0 is to maintain the {@link #getBestSolutionSoFar() current solution};
     * 1 is to accept the neighbor solution;
     * intermediate values define the probability that the neighbor solution
     * will be randomly accepted.
     */
    double getAcceptanceProbability();

	/**
	 * Gets a random number between 0 (inclusive) and maxValue (exclusive).
	 *
	 * @param maxValue the max value to get a random number (exclusive)
	 * @return the random number
	 */
    int getRandomValue(int maxValue);

    /**
     * Checks if the solution search can be stopped,
     * since a suitable solution was found or the number of iterations desired was reached.
     *
     * @return true if the solution search can be stopped, false otherwise.
     */
    boolean isToStopSearch();

    /**
     * Gets the initial solution that the heuristic will start from to try to improve it.
     * If no initial solution was generated yet, one should be randomly generated.
     * @return the initial randomly generated solution
     */
    S getInitialSolution();

    /**
     * @return the latest neighbor solution created
     * @see #createNeighbor(HeuristicSolution)
     */
    S getNeighborSolution();

    /**
     * Creates a neighbor-solution cloning a source one
     * and randomly changing some of its values.
     * A neighbor solution is one that is close to the current solution
     * and has just small changes.
     *
     * @param source the source to create a neighbor solution
     * @return the cloned and randomly changed solution that represents a neighbor solution
     */
    S createNeighbor(S source);

    /**
     * @return the best solution found out up to now.
     */
    S getBestSolutionSoFar();

    /**
     * @return the number of times a neighbor solution will be searched
     * at each iteration of the {@link #solve() solution finding}.
     */
    int getSearchesByIteration();

    /**
     * Sets the number of times a neighbor solution will be searched
     * at each iteration of the {@link #solve() solution finding}.
     *
     * @param numberOfNeighborhoodSearches number of neighbor searches to perform at each iteration
     */
    Heuristic<S> setSearchesByIteration(int numberOfNeighborhoodSearches);

	/**
	 * Starts the heuristic to find a suboptimal solution.
	 * After the method finishes, you can call the {@link #getBestSolutionSoFar()}
	 * to get the final solution.
	 *
	 * @return the final solution
	 * @see #getBestSolutionSoFar()
     *
	 * TODO Try to parallelize the solution finding to reduce search time using Parallel Streams.
	 */
	S solve();

	/**
	 * @return the time taken to finish the solution search (in seconds).
	 * @see #solve()
	 */
	double getSolveTime();
}
