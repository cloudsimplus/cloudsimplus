CloudletExecutionTask
=====================

.. java:package:: org.cloudbus.cloudsim.cloudlets.network
   :noindex:

.. java:type:: public class CloudletExecutionTask extends CloudletTask

   A processing task that can be executed by a \ :java:ref:`NetworkCloudlet`\  in a single \ :java:ref:`org.cloudbus.cloudsim.resources.Pe`\ . The tasks currently just execute in a sequential manner.

   Please refer to following publication for more details:

   ..

   * \ `Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011. <https://doi.org/10.1109/UCC.2011.24>`_\

   :author: Saurabh Kumar Garg, Manoel Campos da Silva Filho

Constructors
------------
CloudletExecutionTask
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CloudletExecutionTask(int id, long executionLength)
   :outertype: CloudletExecutionTask

   Creates a new task.

   :param id: task id
   :param executionLength: the execution length of the task (in MI)

Methods
-------
getLength
^^^^^^^^^

.. java:method:: public long getLength()
   :outertype: CloudletExecutionTask

   Gets the execution length of the task (in MI).

getTotalExecutedLength
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public long getTotalExecutedLength()
   :outertype: CloudletExecutionTask

   Gets the length of this CloudletTask that has been executed so far (in MI).

process
^^^^^^^

.. java:method:: public boolean process(long partialFinishedMI)
   :outertype: CloudletExecutionTask

   Sets a given number of MI to the \ :java:ref:`total MI executed so far <getTotalExecutedLength()>`\  by the cloudlet.

   :param partialFinishedMI: the partial executed length of this Cloudlet (in MI)
   :return: {@inheritDoc}

setLength
^^^^^^^^^

.. java:method:: public void setLength(long length)
   :outertype: CloudletExecutionTask

   Sets the execution length of the task (in MI).

   :param length: the length to set

