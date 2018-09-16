.. java:import:: org.cloudbus.cloudsim.cloudlets CloudletSimple

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Optional

NetworkCloudlet
===============

.. java:package:: org.cloudbus.cloudsim.cloudlets.network
   :noindex:

.. java:type:: public class NetworkCloudlet extends CloudletSimple

   NetworkCloudlet class extends Cloudlet to support simulation of complex applications. Each NetworkCloudlet represents a task of the application. Each task consists of several tasks.

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
NetworkCloudlet
^^^^^^^^^^^^^^^

.. java:constructor:: public NetworkCloudlet(int id, long length, int pesNumber)
   :outertype: NetworkCloudlet

   Creates a NetworkCloudlet with no priority and file size and output size equal to 1.

   :param id: the unique ID of this cloudlet
   :param length: the length or size (in MI) of this cloudlet to be executed in a VM (check out \ :java:ref:`setLength(long)`\ )
   :param pesNumber: the pes number

Methods
-------
addTask
^^^^^^^

.. java:method:: public NetworkCloudlet addTask(CloudletTask task)
   :outertype: NetworkCloudlet

   Adds a task to the \ :java:ref:`task list <getTasks()>`\  and links the task to the NetworkCloudlet.

   :param task: Task to be added
   :return: the NetworkCloudlet instance

getCurrentTask
^^^^^^^^^^^^^^

.. java:method:: public Optional<CloudletTask> getCurrentTask()
   :outertype: NetworkCloudlet

   Gets an \ :java:ref:`Optional`\  containing the current task or an \ :java:ref:`Optional.empty()`\ .

getLength
^^^^^^^^^

.. java:method:: @Override public long getLength()
   :outertype: NetworkCloudlet

   {@inheritDoc}

   The length of a NetworkCloudlet is the length sum of all its \ :java:ref:`CloudletExecutionTask`\ 's.

   :return: the length sum of all \ :java:ref:`CloudletExecutionTask`\ 's

getMemory
^^^^^^^^^

.. java:method:: public long getMemory()
   :outertype: NetworkCloudlet

   Gets the Cloudlet's RAM memory.

getNumberOfTasks
^^^^^^^^^^^^^^^^

.. java:method:: public double getNumberOfTasks()
   :outertype: NetworkCloudlet

getTasks
^^^^^^^^

.. java:method:: public List<CloudletTask> getTasks()
   :outertype: NetworkCloudlet

   :return: a read-only list of cloudlet's tasks.

isFinished
^^^^^^^^^^

.. java:method:: @Override public boolean isFinished()
   :outertype: NetworkCloudlet

isTasksStarted
^^^^^^^^^^^^^^

.. java:method:: public boolean isTasksStarted()
   :outertype: NetworkCloudlet

   Checks if the some Cloudlet Task has started yet.

   :return: true if some task has started, false otherwise

setMemory
^^^^^^^^^

.. java:method:: public NetworkCloudlet setMemory(long memory)
   :outertype: NetworkCloudlet

   Sets the Cloudlet's RAM memory.

   :param memory: amount of RAM to set

startNextTaskIfCurrentIsFinished
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean startNextTaskIfCurrentIsFinished(double nextTaskStartTime)
   :outertype: NetworkCloudlet

   Change the current task to the next one in order to start executing it, if the current task is finished.

   :param nextTaskStartTime: the time that the next task will start
   :return: true if the current task finished and the next one was started, false otherwise

