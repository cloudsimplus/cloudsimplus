package org.cloudsimplus.heuristics;

/**
 * A class to allow the implementation of Null Object Design Pattern
 * for the {@link Heuristic} interface and extensions of it.
 *
 * @author Manoel Campos da Silva Filho
 */
final class HeuristicNull<S extends HeuristicSolution<?>> implements Heuristic<S> {
    @Override public double getAcceptanceProbability() { return 0.0; }
	@Override public int getRandomValue(int maxValue) { return 0; }
	@Override public boolean isToStopSearch() { return false; }
    @Override public S getInitialSolution() { return (S)HeuristicSolution.NULL; }
    @Override public S getNeighborSolution() { return (S)HeuristicSolution.NULL; }
    @Override public S createNeighbor(S source) { return (S)HeuristicSolution.NULL; }
    @Override public S solve() { return (S)HeuristicSolution.NULL; }
	@Override public S getBestSolutionSoFar() { return (S)HeuristicSolution.NULL; }
	@Override public int getNumberOfNeighborhoodSearchesByIteration() { return 0; }
	@Override public void setNumberOfNeighborhoodSearchesByIteration(int numberOfNeighborhoodSearches) {}
	@Override public double getSolveTime() { return 0; }
}
