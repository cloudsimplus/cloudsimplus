HeuristicNull
=============

.. java:package:: org.cloudsimplus.heuristics
   :noindex:

.. java:type::  class HeuristicNull<S extends HeuristicSolution<?>> implements Heuristic<S>

   A class to allow the implementation of Null Object Design Pattern for the \ :java:ref:`Heuristic`\  interface and extensions of it.

   :author: Manoel Campos da Silva Filho

Methods
-------
createNeighbor
^^^^^^^^^^^^^^

.. java:method:: @Override public S createNeighbor(S source)
   :outertype: HeuristicNull

getAcceptanceProbability
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAcceptanceProbability()
   :outertype: HeuristicNull

getBestSolutionSoFar
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public S getBestSolutionSoFar()
   :outertype: HeuristicNull

getInitialSolution
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public S getInitialSolution()
   :outertype: HeuristicNull

getNeighborSolution
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public S getNeighborSolution()
   :outertype: HeuristicNull

getNeighborhoodSearchesByIteration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getNeighborhoodSearchesByIteration()
   :outertype: HeuristicNull

getRandomValue
^^^^^^^^^^^^^^

.. java:method:: @Override public int getRandomValue(int maxValue)
   :outertype: HeuristicNull

getSolveTime
^^^^^^^^^^^^

.. java:method:: @Override public double getSolveTime()
   :outertype: HeuristicNull

isToStopSearch
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isToStopSearch()
   :outertype: HeuristicNull

setNeighborhoodSearchesByIteration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setNeighborhoodSearchesByIteration(int neighborhoodSearches)
   :outertype: HeuristicNull

solve
^^^^^

.. java:method:: @Override public S solve()
   :outertype: HeuristicNull

