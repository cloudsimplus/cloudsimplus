package org.cloudsimplus.heuristics;

/**
 * A solution for a complex problem found using a {@link Heuristic} implementation.
 *
 * @author Manoel Campos da Silva Filho
 * @param <T> defines the class used to store the result of the solution.
 * For instance, if a implementation of this interface aims to provide
 * a mapping between Cloudlets and Vm's, this class would be
 * a {@code  Map<Cloudet, Vm>}, that will indicate which Vm will
 * run each Cloudlet.
 */
public interface HeuristicSolution<T> extends Comparable<HeuristicSolution<T>> {
	/**
	 * @return the heuristic associated to this solution.
	 */
	public Heuristic getHeuristic();
	
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
    public default double getFitness() {
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
     * Creates a neighbor solution cloning the current one
     * and randomly changing some of its values.
     * A neighbor solution is one that is close to the current solution
     * and has just little changes.
     *
     * @param <T> the class of this solution
     * @return the cloned and randomly changed solution that represents a neighbor solution
     */
    <T extends HeuristicSolution> T createNeighbor();

    /**
     * @return the object containing the result of the generated solution.
     */
    T getResult();

    /**
     * A property that implements the Null Object Design Pattern for {@link HeuristicSolution}
     * objects.
     */
    public static final HeuristicSolution NULL = new HeuristicSolution() {
        private final Object object = new Object();
        @Override public double getFitness() { return 0.0; }
        @Override public double getCost() { return 0.0; }
        @Override public HeuristicSolution createNeighbor() { return this; }
        @Override public int compareTo(Object o) { return 0; }
        @Override public Object getResult() { return object; }
		@Override public Heuristic getHeuristic() { return Heuristic.NULL; }
    };

}
