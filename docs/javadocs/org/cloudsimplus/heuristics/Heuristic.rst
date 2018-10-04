Heuristic
=========

.. java:package:: org.cloudsimplus.heuristics
   :noindex:

.. java:type:: public interface Heuristic<S extends HeuristicSolution<?>>

   Provides the methods to be used for implementation of heuristics to find solution for complex problems where the solution space to search is large. These problems are usually NP-Hard ones which the time to find a solution increases, for instance, in exponential time. Such problems can be, for instance, mapping a set of VMs to existing Hosts or mapping a set of Cloudlets to VMs. A heuristic implementation thus provides an approximation of an optimal solution (a suboptimal solution).

   Different heuristic can be implemented, such as \ `Tabu search <https://en.wikipedia.org/wiki/Tabu_search>`_\ , \ `Simulated annealing <https://en.wikipedia.org/wiki/Simulated_annealing>`_\ , \ `Hill climbing <https://en.wikipedia.org/wiki/Hill_climbing>`_\  or \ `Ant colony optimization <https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms>`_\ , to name a few.

   :author: Manoel Campos da Silva Filho
   :param <S>: the \ :java:ref:`class of solutions <HeuristicSolution>`\  the heuristic will deal with

Fields
------
NULL
^^^^

.. java:field::  Heuristic NULL
   :outertype: Heuristic

   A property that implements the Null Object Design Pattern for \ :java:ref:`Heuristic`\  objects.

Methods
-------
createNeighbor
^^^^^^^^^^^^^^

.. java:method::  S createNeighbor(S source)
   :outertype: Heuristic

   Creates a neighbor solution cloning a source one and randomly changing some of its values. A neighbor solution is one that is close to the current solution and has just little changes.

   :param source: the source to create a neighbor solution
   :return: the cloned and randomly changed solution that represents a neighbor solution

getAcceptanceProbability
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getAcceptanceProbability()
   :outertype: Heuristic

   Computes the acceptance probability to define if a neighbor solution has to be accepted or not, compared to the \ :java:ref:`getBestSolutionSoFar()`\ .

   :return: the acceptance probability, in scale from [0 to 1] where 0 is to maintain the \ :java:ref:`current solution <getBestSolutionSoFar()>`\ , 1 is to accept the neighbor solution, while intermediate values defines the probability that the neighbor solution will be randomly accepted.

getBestSolutionSoFar
^^^^^^^^^^^^^^^^^^^^

.. java:method::  S getBestSolutionSoFar()
   :outertype: Heuristic

   :return: best solution found out up to now

getInitialSolution
^^^^^^^^^^^^^^^^^^

.. java:method::  S getInitialSolution()
   :outertype: Heuristic

   Gets the initial solution that the heuristic will start from in order to try to improve it. If not initial solution was generated yet, one should be randomly generated.

   :return: the initial randomly generated solution

getNeighborSolution
^^^^^^^^^^^^^^^^^^^

.. java:method::  S getNeighborSolution()
   :outertype: Heuristic

   :return: latest neighbor solution created

   **See also:** :java:ref:`.createNeighbor(HeuristicSolution)`

getNeighborhoodSearchesByIteration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  int getNeighborhoodSearchesByIteration()
   :outertype: Heuristic

   :return: the number of times a neighbor solution will be searched at each iteration of the \ :java:ref:`solution find <solve()>`\ .

getRandomValue
^^^^^^^^^^^^^^

.. java:method::  int getRandomValue(int maxValue)
   :outertype: Heuristic

   Gets a random number between 0 (inclusive) and maxValue (exclusive).

   :param maxValue: the max value to get a random number (exclusive)
   :return: the random number

getSolveTime
^^^^^^^^^^^^

.. java:method::  double getSolveTime()
   :outertype: Heuristic

   :return: the time taken to finish the solution search (in seconds).

   **See also:** :java:ref:`.solve()`

isToStopSearch
^^^^^^^^^^^^^^

.. java:method::  boolean isToStopSearch()
   :outertype: Heuristic

   Checks if the solution search can be stopped.

   :return: true if the solution search can be stopped, false otherwise.

setNeighborhoodSearchesByIteration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setNeighborhoodSearchesByIteration(int numberOfNeighborhoodSearches)
   :outertype: Heuristic

   Sets the number of times a neighbor solution will be searched at each iteration of the \ :java:ref:`solution find <solve()>`\ .

   :param numberOfNeighborhoodSearches: number of neighbor searches to perform at each iteration

solve
^^^^^

.. java:method::  S solve()
   :outertype: Heuristic

   Starts the heuristic to find a suboptimal solution. After the method finishes, you can call the \ :java:ref:`getBestSolutionSoFar()`\  to get the final solution.

   :return: the final solution

   **See also:** :java:ref:`.getBestSolutionSoFar()`

