package org.cloudsimplus.heuristics;

/**
 * A class that implements the Null Object Design Pattern for {@link HeuristicSolution}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see HeuristicSolution#NULL
 */
final class HeuristicSolutionNull implements HeuristicSolution {
    private static final Object object = new Object();
    @Override public double getFitness() {
        return 0.0;
    }
    @Override public double getCost() {
        return 0.0;
    }
    @Override public int compareTo(Object o) {
        return 0;
    }
    @Override public Object getResult() {
        return object;
    }
    @Override public Heuristic getHeuristic() {
        return Heuristic.NULL;
    }
}
