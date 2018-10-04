.. java:import:: com.google.gson Gson

.. java:import:: org.cloudbus.cloudsim.util ResourceLoader

.. java:import:: java.io InputStreamReader

.. java:import:: java.nio.file Path

.. java:import:: java.nio.file Paths

AwsEc2Template
==============

.. java:package:: org.cloudsimplus.vmtemplates
   :noindex:

.. java:type:: public class AwsEc2Template implements Comparable<AwsEc2Template>

   Represents an \ `Amazon EC2 Instance <http://aws.amazon.com/ec2/>`_\  template. This class enables reading a template from a JSON file, containing actual configurations for VMs available in \ `Amazon Web Services <http://aws.amazon.com/>`_\ .

   For more details, check \ `Raysa Oliveira's Master Thesis (only in Portuguese) <http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf>`_\ .

   :author: raysaoliveira

   **See also:** :java:ref:`.getInstance(String)`

Fields
------
NULL
^^^^

.. java:field:: public static final AwsEc2Template NULL
   :outertype: AwsEc2Template

Constructors
------------
AwsEc2Template
^^^^^^^^^^^^^^

.. java:constructor:: public AwsEc2Template()
   :outertype: AwsEc2Template

   Default constructor used to create an \ :java:ref:`AwsEc2Template`\  instance. If you want to get a template from a JSON file, you shouldn't call the constructor directly. Instead, use some methods such as the \ :java:ref:`getInstance(String)`\ .

   This constructor is just provided to enable the \ :java:ref:`Gson`\  object to use reflection to instantiate a AwsEc2Template.

   **See also:** :java:ref:`.getInstance(String)`

AwsEc2Template
^^^^^^^^^^^^^^

.. java:constructor:: public AwsEc2Template(AwsEc2Template source)
   :outertype: AwsEc2Template

   A clone constructor which receives an \ :java:ref:`AwsEc2Template`\  and creates a clone of it.

   :param source: the \ :java:ref:`AwsEc2Template`\  to be cloned

AwsEc2Template
^^^^^^^^^^^^^^

.. java:constructor:: public AwsEc2Template(String jsonFilePath)
   :outertype: AwsEc2Template

   Instantiates an AWS EC2 Instance from a JSON file.

   :param jsonFilePath: the full path to the JSON file representing the template with configurations for an AWS EC2 Instance

   **See also:** :java:ref:`.getInstance(String)`

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(AwsEc2Template template)
   :outertype: AwsEc2Template

getCpus
^^^^^^^

.. java:method:: public int getCpus()
   :outertype: AwsEc2Template

getFileName
^^^^^^^^^^^

.. java:method:: public String getFileName()
   :outertype: AwsEc2Template

   Gets only the name of the JSON template file used to create this template, without the path.

getFilePath
^^^^^^^^^^^

.. java:method:: public String getFilePath()
   :outertype: AwsEc2Template

   Gets the full path to the JSON template file used to create this template.

getInstance
^^^^^^^^^^^

.. java:method:: public static AwsEc2Template getInstance(String jsonFilePath)
   :outertype: AwsEc2Template

   Gets an AWS EC2 Instance from a JSON file inside the \ **application's resource directory**\ . Use the available constructors if you want to load a file outside the resource directory.

   :param jsonFilePath: the \ **relative path**\  to the JSON file representing the template with configurations for an AWS EC2 Instance
   :return: the AWS EC2 Instance from the JSON file

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

   Gets the price per hour of a VM created from this template

setCpus
^^^^^^^

.. java:method:: public void setCpus(int cpus)
   :outertype: AwsEc2Template

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

