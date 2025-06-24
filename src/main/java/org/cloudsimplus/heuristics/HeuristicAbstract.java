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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.distributions.ContinuousDistribution;

import java.lang.reflect.InvocationTargetException;

/**
 * An abstract class for {@link Heuristic} implementations.
 *
 * @param <S> The {@link HeuristicSolution class of solutions} the heuristic will deal with.
 *            It starts with an initial solution (usually random, depending on each subclass implementation)
 *            and executes the search to find a satisfying solution (defined by a stop criteria).
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
@Accessors @Getter
public abstract class HeuristicAbstract<S extends HeuristicSolution<?>>  implements Heuristic<S> {
	/**
	 * Reference to the generic class that will be used to instantiate objects.
	 */
    @Getter(AccessLevel.NONE)
    private final Class<S> solutionClass;

    /**
     * A pseudo-random number generator
     */
	private final ContinuousDistribution random;

    /**
     * The number of neighborhood searches by each iteration of the heuristic.
     */
    @Setter
    private int searchesByIteration;

    private S bestSolutionSoFar;

    private S neighborSolution;

	private double solveTime;

	/**
	 * Creates a heuristic.
	 *
	 * @param random a pseudo-random number generator
	 * @param solutionClass reference to the generic class that will be used to instantiate heuristic solutions
	 */
	public HeuristicAbstract(@NonNull final ContinuousDistribution random, @NonNull final Class<S> solutionClass){
        this.random = random;
        this.solutionClass = solutionClass;
		this.searchesByIteration = 1;
		setBestSolutionSoFar(newSolutionInstance());
		setNeighborSolution(bestSolutionSoFar);
	}

	/**
	 * Sets the time taken to solve the heuristic.
	 * @param solveTime the time to set (in seconds)
	 */
	protected void setSolveTime(final double solveTime) {
		this.solveTime = solveTime;
	}

	private S newSolutionInstance() {
	    try {
	        final var constructor = solutionClass.getConstructor(Heuristic.class);
	        return constructor.newInstance(this);
	    } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException ex) {
	        throw new RuntimeException(ex);
	    }
	}

	/**
	 * Updates the state of the system to keep looking for a suboptimal solution.
	 */
	protected abstract void updateSystemState();

	@Override
	public int getRandomValue(final int maxValue){
		final double uniform = getRandom().sample();

        /* Always get an index between [0 and size[,
        regardless if the random number generator returns
        values between [0 and 1[ or >= 1 */
		return (int)(uniform >= 1 ? uniform % maxValue : uniform * maxValue);
	}

	@Override
	public S solve() {
		final long startTime = System.currentTimeMillis();
		setBestSolutionSoFar(getInitialSolution());
		while (!isToStopSearch()) {
            searchSolutionInNeighborhood();
            updateSystemState();
		}
		setSolveTime((System.currentTimeMillis() - startTime)/1000.0);

		return bestSolutionSoFar;
	}

    private void searchSolutionInNeighborhood() {
        for (int i = 0; i < searchesByIteration; i++) {
            setNeighborSolution(createNeighbor(bestSolutionSoFar));
            if (getAcceptanceProbability() > getRandomValue(1)) {
                setBestSolutionSoFar(neighborSolution);
            }
        }
    }

    /**
     * Sets a solution as the current one.
     * @param solution the solution to set as the current one.
     */
    protected final void setBestSolutionSoFar(final S solution) {
        this.bestSolutionSoFar = solution;
    }

	/**
	 * Sets a solution as the neighbor one.
	 * @param solution the solution to set as the neighbor one.
	 */
    protected final void setNeighborSolution(final S solution) {
        this.neighborSolution = solution;
    }

}
