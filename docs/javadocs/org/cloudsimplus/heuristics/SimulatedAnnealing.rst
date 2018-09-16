.. java:import:: org.cloudbus.cloudsim.distributions ContinuousDistribution

SimulatedAnnealing
==================

.. java:package:: org.cloudsimplus.heuristics
   :noindex:

.. java:type:: public abstract class SimulatedAnnealing<S extends HeuristicSolution<?>> extends HeuristicAbstract<S>

   A base class for implementation of \ `Simulated Annealing <http://en.wikipedia.org/wiki/Simulated_annealing>`_\  algorithms used to find a suboptimal solution for a problem defined by sub-classes of this one. The Simulated Annealing is a heuristic that starts with a random solution and iteratively generates a random neighbor solution that its fitness is assessed in order to reach a sub-optimal result. The algorithm try to avoid local maximums, randomly selecting worse solutions to get away from being stuck in these locals.

   The algorithm basically works as follows:

   ..

   #. Starts generating a random solution as you wish;
   #. Computes its fitness using some function (defined by the developer implementing the heuristic);
   #. Generates a neighbor random solution from the current solution and computes its fitness;
   #. Assesses the neighbor and current solution (the conditions below are ensured by the \ :java:ref:`getAcceptanceProbability()`\  method):

      ..

      * \ ``if neighbor.getFitness() > current.getFitness()``\  then move to the new solution;
      * \ ``if neighbor.getFitness() < current.getFitness()``\  then randomly decide if move to the new solution;

   #. Repeat steps 3 to 4 until an aceptable solution is found or some number of iterations or time is reached. These conditions are defined by the developer implementing the heuristic.

   :author: Manoel Campos da Silva Filho
   :param <S>: the class of solutions the heuristic will deal with, starting with a random solution and execute the solution search in order to achieve a satisfying solution (defined by a stop criteria)

   **See also:** \ `[1] R. A. Rutenbar, “Simulated Annealing Algorithms: An overview,” IEEE Circuits Devices Mag., vol. 1, no. 5, pp. 19–26, 1989. <https://doi.org/10.1109/101.17235>`_\

Constructors
------------
SimulatedAnnealing
^^^^^^^^^^^^^^^^^^

.. java:constructor::  SimulatedAnnealing(ContinuousDistribution random, Class<S> solutionClass)
   :outertype: SimulatedAnnealing

   Instantiates a simulated annealing heuristic.

   :param random: a pseudo random number generator
   :param solutionClass: reference to the generic class that will be used to instantiate heuristic solutions

Methods
-------
getAcceptanceProbability
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getAcceptanceProbability()
   :outertype: SimulatedAnnealing

   {@inheritDoc}

   It is used the Boltzmann distribution to define the probability of a worse solution (considering its cost) to be accepted or not in order to avoid local minima. The computed Boltzmann factor also ensures that better solutions are always accepted. The Boltzmann Constant has different values depending of the used unit. In this case, it was used the natural unit of information.

   :return: {@inheritDoc}

   **See also:** \ `Boltzmann distribution <http://www.wikiwand.com/en/Boltzmann_distribution>`_\, \ `Boltzmann constant <http://en.wikipedia.org/wiki/Boltzmann_constant>`_\

getColdTemperature
^^^^^^^^^^^^^^^^^^

.. java:method:: public double getColdTemperature()
   :outertype: SimulatedAnnealing

   :return: the temperature that defines the system is cold enough and solution search may be stopped.

getCoolingRate
^^^^^^^^^^^^^^

.. java:method:: public double getCoolingRate()
   :outertype: SimulatedAnnealing

   :return: percentage rate in which the system will be cooled, in scale from [0 to 1[.

getCurrentTemperature
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getCurrentTemperature()
   :outertype: SimulatedAnnealing

   Gets the current system temperature that represents the system state at the time of the method call.

   :return: the current system temperature

isToStopSearch
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isToStopSearch()
   :outertype: SimulatedAnnealing

   {@inheritDoc}

   :return: true if the system is cold enough and solution search can be stopped, false otherwise

setColdTemperature
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setColdTemperature(double coldTemperature)
   :outertype: SimulatedAnnealing

   Sets the temperature that defines the system is cold enough and solution search may be stopped.

   :param coldTemperature: the cold temperature to set

setCoolingRate
^^^^^^^^^^^^^^

.. java:method:: public void setCoolingRate(double coolingRate)
   :outertype: SimulatedAnnealing

   Sets the percentage rate in which the system will be cooled, in scale from [0 to 1[.

   :param coolingRate: the rate to set

setCurrentTemperature
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setCurrentTemperature(double currentTemperature)
   :outertype: SimulatedAnnealing

   Sets the current system temperature.

   :param currentTemperature: the temperature to set

updateSystemState
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void updateSystemState()
   :outertype: SimulatedAnnealing

   {@inheritDoc} Cools the system at a the defined \ :java:ref:`cooling rate <getCoolingRate()>`\ .

   **See also:** :java:ref:`.getCurrentTemperature()()`

