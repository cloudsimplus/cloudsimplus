HeuristicSolution
=================

.. java:package:: org.cloudsimplus.heuristics
   :noindex:

.. java:type:: public interface HeuristicSolution<T> extends Comparable<HeuristicSolution<T>>

   A solution for a complex problem found using a \ :java:ref:`Heuristic`\  implementation.

   :author: Manoel Campos da Silva Filho
   :param <T>: the type used to store the result of the solution. For instance, if a implementation of this interface aims to provide a mapping between Cloudlets and Vm's, this type would be a \ ``Map<Cloudet, Vm>``\ , that will indicate which Vm will run each Cloudlet. Such result can be obtained by calling the \ :java:ref:`getResult()`\  method.

Fields
------
NULL
^^^^

.. java:field::  HeuristicSolution NULL
   :outertype: HeuristicSolution

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`HeuristicSolution`\  objects.

Methods
-------
getCost
^^^^^^^

.. java:method::  double getCost()
   :outertype: HeuristicSolution

   Defines the cost of using this solution. As higher is the cost, worse is a solution. How a solution cost is computed is totally dependent of the heuristic implementation being used to find a solution.

   :return: the solution cost

   **See also:** :java:ref:`.getFitness()`

getFitness
^^^^^^^^^^

.. java:method::  double getFitness()
   :outertype: HeuristicSolution

   Defines how good the solution is and it the inverse of the \ :java:ref:`getCost()`\ . As higher is the fitness, better is a solution. How a solution fitness is computed is totally dependent of the heuristic implementation being used to find a solution.

   :return: the solution fitness

   **See also:** :java:ref:`.getCost()`

getHeuristic
^^^^^^^^^^^^

.. java:method::  Heuristic<HeuristicSolution<T>> getHeuristic()
   :outertype: HeuristicSolution

   :return: the heuristic associated to this solution.

getResult
^^^^^^^^^

.. java:method::  T getResult()
   :outertype: HeuristicSolution

   :return: the object containing the result of the generated solution.

