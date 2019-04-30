.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicySimple

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core CloudSim

.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterSimple

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: java.util Objects

.. java:import:: java.util.function Consumer

.. java:import:: java.util.function Function

.. java:import:: java.util.function Supplier

Experiment
==========

.. java:package:: org.cloudsimplus.testbeds
   :noindex:

.. java:type:: public abstract class Experiment implements Runnable

   A base class to implement simulation experiments that can be executed in a repeatable way by a \ :java:ref:`ExperimentRunner`\ .

   :author: Manoel Campos da Silva Filho

Fields
------
hostsNumber
^^^^^^^^^^^

.. java:field:: protected int hostsNumber
   :outertype: Experiment

Constructors
------------
Experiment
^^^^^^^^^^

.. java:constructor:: public Experiment(long seed)
   :outertype: Experiment

   Creates a simulation experiment that is not linked to a runner, to enable it to execute just one run.

Experiment
^^^^^^^^^^

.. java:constructor:: public Experiment(int index, ExperimentRunner runner)
   :outertype: Experiment

   Instantiates a simulation experiment with 1 Datacenter by default.

   :param index: the index that identifies the current experiment run.
   :param runner: The \ :java:ref:`ExperimentRunner`\  that is in charge of executing this experiment a defined number of times and to collect data for statistical analysis.

   **See also:** :java:ref:`.setDatacentersNumber(int)`

Experiment
^^^^^^^^^^

.. java:constructor:: protected Experiment(int index, ExperimentRunner runner, long seed)
   :outertype: Experiment

   Instantiates a simulation experiment that will create 1 broker and 1 Datacenter by default.

   :param index: the index that identifies the current experiment run.
   :param runner: The \ :java:ref:`ExperimentRunner`\  to execute the experiment. If omitted, it means the experiment is independent and may be run just once. If you don't provide a runner, you must provide a seed
   :param seed: the seed to be set. If a runner is given, this value is ignored and the seed is generated from the runner base seed. If you don't provide a seed, you must provide a runner.

   **See also:** :java:ref:`.setBrokersNumber(int)`, :java:ref:`.setDatacentersNumber(int)`

Methods
-------
build
^^^^^

.. java:method:: protected final void build()
   :outertype: Experiment

   Creates the simulation scenario to run the experiment.

createAndSubmitCloudletsInternal
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void createAndSubmitCloudletsInternal(DatacenterBroker broker)
   :outertype: Experiment

   Creates all the Cloudlets required by the experiment and submits them to a Broker. This the entry-point for Cloudlets creation.

   :param broker: broker to submit Cloudlets to

createBroker
^^^^^^^^^^^^

.. java:method:: protected abstract DatacenterBroker createBroker()
   :outertype: Experiment

   Creates a DatacenterBroker.

   :return: the created DatacenterBroker

createBrokers
^^^^^^^^^^^^^

.. java:method:: protected void createBrokers()
   :outertype: Experiment

   Creates a list of brokers. This is the entry-point for broker creation.

createCloudlet
^^^^^^^^^^^^^^

.. java:method:: protected abstract Cloudlet createCloudlet(DatacenterBroker broker)
   :outertype: Experiment

createCloudlets
^^^^^^^^^^^^^^^

.. java:method:: protected abstract List<Cloudlet> createCloudlets(DatacenterBroker broker)
   :outertype: Experiment

   Creates a list of Cloudlets to be used by the experiment.

   :param broker: the broker to create the Cloudlets to
   :return: the list of created cloudlets

createDatacenter
^^^^^^^^^^^^^^^^

.. java:method:: protected DatacenterSimple createDatacenter()
   :outertype: Experiment

   Creates a datacenter using a \ :java:ref:`VmAllocationPolicy`\  suplied by the \ :java:ref:`vmAllocationPolicySupplier`\ .

   **See also:** :java:ref:`.setVmAllocationPolicySupplier(Supplier)`

createHost
^^^^^^^^^^

.. java:method:: protected abstract Host createHost(int id)
   :outertype: Experiment

createHosts
^^^^^^^^^^^

.. java:method:: protected final List<Host> createHosts()
   :outertype: Experiment

createVm
^^^^^^^^

.. java:method:: protected abstract Vm createVm(DatacenterBroker broker, int id)
   :outertype: Experiment

createVms
^^^^^^^^^

.. java:method:: protected List<Vm> createVms(DatacenterBroker broker)
   :outertype: Experiment

   Creates the Vms to be used by the experiment.

   :param broker:
   :return: the List of created VMs

getBrokerList
^^^^^^^^^^^^^

.. java:method:: public List<DatacenterBroker> getBrokerList()
   :outertype: Experiment

   Gets the list of created DatacenterBrokers.

getBrokersNumber
^^^^^^^^^^^^^^^^

.. java:method:: public int getBrokersNumber()
   :outertype: Experiment

   Gets the number of brokers to create.

getCloudletList
^^^^^^^^^^^^^^^

.. java:method:: public final List<Cloudlet> getCloudletList()
   :outertype: Experiment

getDatacenterList
^^^^^^^^^^^^^^^^^

.. java:method:: public List<DatacenterSimple> getDatacenterList()
   :outertype: Experiment

getDatacentersNumber
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getDatacentersNumber()
   :outertype: Experiment

getIndex
^^^^^^^^

.. java:method:: public int getIndex()
   :outertype: Experiment

   The index that identifies the current experiment run.

getRunner
^^^^^^^^^

.. java:method:: public ExperimentRunner getRunner()
   :outertype: Experiment

   Gets the object that is in charge to run the experiment.

getSeed
^^^^^^^

.. java:method:: public long getSeed()
   :outertype: Experiment

getSimulation
^^^^^^^^^^^^^

.. java:method:: public final CloudSim getSimulation()
   :outertype: Experiment

getVmList
^^^^^^^^^

.. java:method:: public List<Vm> getVmList()
   :outertype: Experiment

getVmsByBrokerFunction
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Function<DatacenterBroker, Integer> getVmsByBrokerFunction()
   :outertype: Experiment

   Gets a \ :java:ref:`Function`\  that receives a \ :java:ref:`DatacenterBroker`\  and returns the number of Vms to create for that broker.

isNotVerbose
^^^^^^^^^^^^

.. java:method:: public boolean isNotVerbose()
   :outertype: Experiment

   Indicates if simulation results of the experiment don't have to be output.

isVerbose
^^^^^^^^^

.. java:method:: public boolean isVerbose()
   :outertype: Experiment

   Indicates if simulation results of the experiment have to be output.

nextCloudletId
^^^^^^^^^^^^^^

.. java:method:: protected final int nextCloudletId()
   :outertype: Experiment

nextVmId
^^^^^^^^

.. java:method:: protected final int nextVmId()
   :outertype: Experiment

print
^^^^^

.. java:method:: public void print(String msg)
   :outertype: Experiment

   Prints a message only if \ :java:ref:`isVerbose()`\ .

   :param msg: the message to print

print
^^^^^

.. java:method:: public void print(String format, Object... args)
   :outertype: Experiment

   Prints a formatted message only if \ :java:ref:`isVerbose()`\ .

   :param format: the message format
   :param args: the values to print

printResults
^^^^^^^^^^^^

.. java:method:: public abstract void printResults()
   :outertype: Experiment

   Prints the results for the experiment. The method has to be implemented by subclasses in order to output the experiment results.

   **See also:** :java:ref:`.printResultsInternal()`

println
^^^^^^^

.. java:method:: public void println()
   :outertype: Experiment

   Prints a line break only if \ :java:ref:`isVerbose()`\ .

println
^^^^^^^

.. java:method:: public void println(String msg)
   :outertype: Experiment

   Prints a message and a line break only if \ :java:ref:`isVerbose()`\ .

   :param msg: the message to print

println
^^^^^^^

.. java:method:: public void println(String format, Object... args)
   :outertype: Experiment

   Prints a formatted message and a line break only if \ :java:ref:`isVerbose()`\ .

   :param format: the message format
   :param args: the values to print

run
^^^

.. java:method:: @Override public final void run()
   :outertype: Experiment

   Builds the simulation scenario and starts execution.

   :throws RuntimeException:

setAfterExperimentBuild
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public <T extends Experiment> Experiment setAfterExperimentBuild(Consumer<T> afterExperimentBuild)
   :outertype: Experiment

   Sets a \ :java:ref:`Consumer`\  that will be called after the simulation scenario is built, which is before starting the simulation.

   Setting a Consumer object is optional.

   :param <T>: the class of the experiment
   :param afterExperimentBuild: the afterExperimentBuild to set

setAfterExperimentFinish
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public <T extends Experiment> Experiment setAfterExperimentFinish(Consumer<T> afterExperimentFinishConsumer)
   :outertype: Experiment

   Sets a \ :java:ref:`Consumer`\  object that will receive the experiment instance after the experiment finishes executing and performs some post-processing tasks. These tasks are defined by the developer using the current class and can include collecting data for statistical analysis.

   Setting a Consumer object is optional.

   :param <T>: the class of the experiment
   :param afterExperimentFinishConsumer: a \ :java:ref:`Consumer`\  instance to set.

setBrokersNumber
^^^^^^^^^^^^^^^^

.. java:method:: public Experiment setBrokersNumber(int brokersNumber)
   :outertype: Experiment

   Sets the number of brokers to create.

   :param brokersNumber: the value to set

setDatacentersNumber
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDatacentersNumber(int datacentersNumber)
   :outertype: Experiment

setHostsNumber
^^^^^^^^^^^^^^

.. java:method:: protected final void setHostsNumber(int hostsNumber)
   :outertype: Experiment

setVerbose
^^^^^^^^^^

.. java:method:: public Experiment setVerbose(boolean verbose)
   :outertype: Experiment

   Defines if simulation results of the experiment have to be output or not.

   :param verbose: true if the results have to be output, false otherwise

setVmAllocationPolicySupplier
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setVmAllocationPolicySupplier(Supplier<VmAllocationPolicy> vmAllocationPolicySupplier)
   :outertype: Experiment

setVmsByBrokerFunction
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public final void setVmsByBrokerFunction(Function<DatacenterBroker, Integer> vmsByBrokerFunction)
   :outertype: Experiment

   Sets a \ :java:ref:`Function`\  that receives a \ :java:ref:`DatacenterBroker`\  and returns the number of Vms to create for that broker. If you want all brokers to have the same amount of VMs, you can give a lambda expression such as \ ``broker -> NUMER_OF_VMS_TO_CREATE``\ .

   :param vmsByBrokerFunction: the \ :java:ref:`Function`\  to set

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: Experiment

