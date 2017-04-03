/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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

/**
 * A solution for a complex problem found using a {@link Heuristic} implementation.
 *
 * @author Manoel Campos da Silva Filho
 * @param <T> the type used to store the result of the solution.
 * For instance, if a implementation of this interface aims to provide
 * a mapping between Cloudlets and Vm's, this type would be
 * a {@code  Map<Cloudet, Vm>}, that will indicate which Vm will
 * run each Cloudlet. Such result can be obtained by calling the {@link #getResult()} method.
 * @since CloudSim Plus 1.0
 */
public interface HeuristicSolution<T> extends Comparable<HeuristicSolution<T>> {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link HeuristicSolution}
     * objects.
     */
    HeuristicSolution NULL = new HeuristicSolutionNull();

	/**
	 * @return the heuristic associated to this solution.
	 */
	Heuristic<HeuristicSolution<T>> getHeuristic();

    /**
     * Defines how good the solution is and it the inverse of the {@link #getCost()}.
     * As higher is the fitness,
     * better is a solution. How a solution fitness is computed is totally
     * dependent of the heuristic implementation being used
     * to find a solution.
     *
     * @return the solution fitness
     * @see #getCost()
     */
    default double getFitness() {
        return 1.0/getCost();
    }

    /**
     * Defines the cost of using this solution.
     * As higher is the cost, worse is a solution. How a solution cost is computed is totally
     * dependent of the heuristic implementation being used to find a solution.
     *
     * @return the solution cost
     * @see #getFitness()
     */
    double getCost();

    /**
     * @return the object containing the result of the generated solution.
     */
    T getResult();

}
