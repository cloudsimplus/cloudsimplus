.. java:import:: com.google.gson Gson

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io FileReader

AwsEc2Template
==============

.. java:package:: org.cloudsimplus.vmtemplates
   :noindex:

.. java:type:: public class AwsEc2Template

   Represents an \ `Amazon EC2 Instance <http://aws.amazon.com/ec2/>`_\  template. This class enables reading a template from a JSON file, containing actual configurations for VMs available in \ `Amazon Web Services <http://aws.amazon.com/>`_\ .

   :author: raysaoliveira

   **See also:** :java:ref:`.getInstance(String)`

Constructors
------------
AwsEc2Template
^^^^^^^^^^^^^^

.. java:constructor:: public AwsEc2Template()
   :outertype: AwsEc2Template

   Default constructor used to create an \ :java:ref:`AwsEc2Template`\  instance. If you want to get a template from a JSON file, you shouldn't call the constructor directly. Instead, use some methods such as the \ :java:ref:`getInstance(String)`\ .

AwsEc2Template
^^^^^^^^^^^^^^

.. java:constructor:: public AwsEc2Template(AwsEc2Template source)
   :outertype: AwsEc2Template

   A clone constructor which receives an \ :java:ref:`AwsEc2Template`\  and creates a clone of it.

   :param source: the \ :java:ref:`AwsEc2Template`\  to be cloned

Methods
-------
getCpus
^^^^^^^

.. java:method:: public int getCpus()
   :outertype: AwsEc2Template

getInstance
^^^^^^^^^^^

.. java:method:: public static AwsEc2Template getInstance(String jsonTemplateFilePath) throws FileNotFoundException
   :outertype: AwsEc2Template

   Gets an AWS EC2 Instance from a JSON file.

   :param jsonTemplateFilePath: the full path to the JSON file representing the template with configurations for an AWS EC2 Instance
   :return: the AWS EC2 Instance from the JSON file

getInstanceFromResourcesDir
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static AwsEc2Template getInstanceFromResourcesDir(String jsonFilePath) throws FileNotFoundException
   :outertype: AwsEc2Template

   Gets an AWS EC2 Instance from a JSON file inside the application's resource directory.

   :param jsonFilePath: the relative path to the JSON file representing the template with configurations for an AWS EC2 Instance
   :return: the AWS EC2 Instance from the JSON file

getMaxNumberOfVmsForCustomer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMaxNumberOfVmsForCustomer()
   :outertype: AwsEc2Template

   Gets the maximum number of VMs which can be created with this configuration for a specific customer, considering the maximum price the customer expects to pay hourly for all his/her running VMs.

   This is not a field inside the JSON file and doesn't in fact represent a AWS EC2 Instance attribute. It's a value which may be computed externally and assigned to the attribute. It's usage is optional and it's default value is zero.

getMemoryInMB
^^^^^^^^^^^^^

.. java:method:: public int getMemoryInMB()
   :outertype: AwsEc2Template

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: AwsEc2Template

getPricePerHour
^^^^^^^^^^^^^^^

.. java:method:: public double getPricePerHour()
   :outertype: AwsEc2Template

main
^^^^

.. java:method:: public static void main(String[] args) throws FileNotFoundException
   :outertype: AwsEc2Template

   A main method just to try the class implementation.

   :param args:

setCpus
^^^^^^^

.. java:method:: public void setCpus(int cpus)
   :outertype: AwsEc2Template

setMaxNumberOfVmsForCustomer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public AwsEc2Template setMaxNumberOfVmsForCustomer(double maxNumberOfVmsForCustomer)
   :outertype: AwsEc2Template

   Sets the maximum number of VMs which can be created with this configuration for a specific customer, considering the maximum price the customer expects to pay hourly for all his/her running VMs.

   This is not a field inside the JSON file and doesn't in fact represent a AWS EC2 Instance attribute. It's a value which may be computed externally and assigned to the attribute. It's usage is optional and it's default value is zero.

   :param maxNumberOfVmsForCustomer: the maximum number of VMs to set

setMemoryInMB
^^^^^^^^^^^^^

.. java:method:: public void setMemoryInMB(int memoryInMB)
   :outertype: AwsEc2Template

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: AwsEc2Template

setPricePerHour
^^^^^^^^^^^^^^^

.. java:method:: public void setPricePerHour(double pricePerHour)
   :outertype: AwsEc2Template

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: AwsEc2Template

