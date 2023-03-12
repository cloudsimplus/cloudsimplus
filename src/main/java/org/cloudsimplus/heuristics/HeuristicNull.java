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

/**
 * A class to allow the implementation of Null Object Design Pattern
 * for the {@link Heuristic} interface and extensions of it.
 *
 * @author Manoel Campos da Silva Filho
 */
class HeuristicNull<S extends HeuristicSolution<?>> implements Heuristic<S> {
    @Override public double getAcceptanceProbability() { return 0.0; }
	@Override public int getRandomValue(int maxValue) { return 0; }
	@Override public boolean isToStopSearch() { return false; }
    @Override public S getInitialSolution() { return (S)HeuristicSolution.NULL; }
    @Override public S getNeighborSolution() { return (S)HeuristicSolution.NULL; }
    @Override public S createNeighbor(S source) { return (S)HeuristicSolution.NULL; }
    @Override public S solve() { return (S)HeuristicSolution.NULL; }
	@Override public S getBestSolutionSoFar() { return (S)HeuristicSolution.NULL; }
	@Override public int getSearchesByIteration() { return 0; }
	@Override public Heuristic<S> setSearchesByIteration(int neighborhoodSearches) { return this; }
	@Override public double getSolveTime() { return 0; }
}
