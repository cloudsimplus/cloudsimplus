.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

CloudletSimple
==============

.. java:package:: org.cloudbus.cloudsim.cloudlets
   :noindex:

.. java:type:: public class CloudletSimple extends CloudletAbstract

   Cloudlet implements the basic features of an application/job/task to be executed by a \ :java:ref:`Vm`\  on behalf of a given user. It stores, despite all the information encapsulated in the Cloudlet, the ID of the VM running it.

   :author: Rodrigo N. Calheiros, Anton Beloglazov

   **See also:** :java:ref:`DatacenterBroker`

Constructors
------------
CloudletSimple
^^^^^^^^^^^^^^

.. java:constructor:: public CloudletSimple(long cloudletLength, int pesNumber)
   :outertype: CloudletSimple

   Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to a \ :java:ref:`DatacenterBroker`\ . The file size and output size is defined as 1.

   :param cloudletLength: the length or size (in MI) of this cloudlet to be executed in a VM
   :param pesNumber: number of PEs that Cloudlet will require

CloudletSimple
^^^^^^^^^^^^^^

.. java:constructor:: public CloudletSimple(long cloudletLength, long pesNumber)
   :outertype: CloudletSimple

   Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to a \ :java:ref:`DatacenterBroker`\ . The file size and output size is defined as 1.

   :param cloudletLength: the length or size (in MI) of this cloudlet to be executed in a VM
   :param pesNumber: number of PEs that Cloudlet will require

CloudletSimple
^^^^^^^^^^^^^^

.. java:constructor:: public CloudletSimple(int id, long cloudletLength, long pesNumber)
   :outertype: CloudletSimple

   Creates a Cloudlet with no priority and file size and output size equal to 1. To change these values, use the respective setters.

   :param id: the unique ID of this cloudlet
   :param cloudletLength: the length or size (in MI) of this cloudlet to be executed in a VM
   :param pesNumber: the pes number

CloudletSimple
^^^^^^^^^^^^^^

.. java:constructor:: @Deprecated public CloudletSimple(int id, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw)
   :outertype: CloudletSimple

   Creates a Cloudlet with the given parameters.

   :param id: the unique ID of this cloudlet
   :param cloudletLength: the length or size (in MI) of this cloudlet to be executed in a VM
   :param cloudletFileSize: the file size (in bytes) of this cloudlet \ ``BEFORE``\  submitting to a Datacenter
   :param cloudletOutputSize: the file size (in bytes) of this cloudlet \ ``AFTER``\  finish executing by a VM
   :param pesNumber: the pes number
   :param utilizationModelCpu: the utilization model of CPU
   :param utilizationModelRam: the utilization model of RAM
   :param utilizationModelBw: the utilization model of BW

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(Cloudlet o)
   :outertype: CloudletSimple

   Compare this Cloudlet with another one based on \ :java:ref:`getLength()`\ .

   :param o: the Cloudlet to compare to
   :return: {@inheritDoc}

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: CloudletSimple

