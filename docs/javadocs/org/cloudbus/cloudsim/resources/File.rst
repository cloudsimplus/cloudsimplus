.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

File
====

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class File

   A class for representing a physical file in a DataCloud environment

   :author: Uros Cibej, Anthony Sulistio

Fields
------
NOT_REGISTERED
^^^^^^^^^^^^^^

.. java:field:: public static final int NOT_REGISTERED
   :outertype: File

   Denotes that this file has not been registered to a Replica Catalogue.

TYPE_UNKNOWN
^^^^^^^^^^^^

.. java:field:: public static final int TYPE_UNKNOWN
   :outertype: File

   Denotes that the type of this file is unknown.

Constructors
------------
File
^^^^

.. java:constructor:: public File(String fileName, int fileSize)
   :outertype: File

   Creates a new DataCloud file with a given size (in MBytes).  NOTE: By default, a newly-created file is set to a \ **master**\  copy.

   :param fileName: file name
   :param fileSize: file size in MBytes
   :throws IllegalArgumentException: when one of the following scenarios occur:

   ..

   * the file name is empty or \ ``null``\
   * the file size is zero or negative numbers

File
^^^^

.. java:constructor:: public File(File file) throws IllegalArgumentException
   :outertype: File

   Copy constructor that creates a clone from a source file and set the given file as a \ **replica**\ .

   :param file: the source file to create a copy and that will be set as a replica
   :throws IllegalArgumentException: when the source file is \ ``null``\

File
^^^^

.. java:constructor:: protected File(File file, boolean masterCopy) throws IllegalArgumentException
   :outertype: File

   Copy constructor that creates a clone from a source file and set the given file as a \ **replica**\  or \ **master copy**\ .

   :param file: the file to clone
   :param masterCopy: false to set the cloned file as a replica, true to set the cloned file as a master copy
   :throws IllegalArgumentException:

Methods
-------
createAttribute
^^^^^^^^^^^^^^^

.. java:method:: protected void createAttribute(int fileSize)
   :outertype: File

getAttribute
^^^^^^^^^^^^

.. java:method:: public FileAttribute getAttribute()
   :outertype: File

   Gets an attribute of this file.

   :return: a file attribute

getAttributeSize
^^^^^^^^^^^^^^^^

.. java:method:: public int getAttributeSize()
   :outertype: File

   Gets the size of this object (in byte).  NOTE: This object size is NOT the actual file size. Moreover, this size is used for transferring this object over a network.

   :return: the object size (in byte)

getChecksum
^^^^^^^^^^^

.. java:method:: public int getChecksum()
   :outertype: File

   Gets the file checksum.

   :return: file checksum

getCost
^^^^^^^

.. java:method:: public double getCost()
   :outertype: File

   Gets the cost associated with the file.

   :return: the cost of this file

getCreationTime
^^^^^^^^^^^^^^^

.. java:method:: public long getCreationTime()
   :outertype: File

   Gets the file creation time (in millisecond).

   :return: the file creation time (in millisecond)

getDatacenter
^^^^^^^^^^^^^

.. java:method:: public Datacenter getDatacenter()
   :outertype: File

   Gets the Datacenter that stores the file.

getLastUpdateTime
^^^^^^^^^^^^^^^^^

.. java:method:: public double getLastUpdateTime()
   :outertype: File

   Gets the last update time (in seconds).

   :return: the last update time (in seconds)

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: File

   Gets the file name.

   :return: the file name

getOwnerName
^^^^^^^^^^^^

.. java:method:: public String getOwnerName()
   :outertype: File

   Gets the owner name of this file.

   :return: the owner name or \ ``null``\  if empty

getRegistrationID
^^^^^^^^^^^^^^^^^

.. java:method:: public long getRegistrationID()
   :outertype: File

   Gets the file registration ID.

   :return: registration ID

getSize
^^^^^^^

.. java:method:: public int getSize()
   :outertype: File

   Gets the file size (in MBytes).

   :return: the file size (in MBytes)

getSizeInByte
^^^^^^^^^^^^^

.. java:method:: public int getSizeInByte()
   :outertype: File

   Gets the file size (in bytes).

   :return: the file size (in bytes)

getTransactionTime
^^^^^^^^^^^^^^^^^^

.. java:method:: public double getTransactionTime()
   :outertype: File

   Gets the last transaction time of the file (in second).

   :return: the transaction time (in second)

getType
^^^^^^^

.. java:method:: public int getType()
   :outertype: File

   Gets the file type.

   :return: file type

isDeleted
^^^^^^^^^

.. java:method:: public boolean isDeleted()
   :outertype: File

   Checks if the file was deleted or not.

   :return: \ ``true``\  if it was deleted, false otherwise

isMasterCopy
^^^^^^^^^^^^

.. java:method:: public boolean isMasterCopy()
   :outertype: File

   Checks whether the file is a master copy or replica.

   :return: \ ``true``\  if it is a master copy or \ ``false``\  otherwise

isRegistered
^^^^^^^^^^^^

.. java:method:: public boolean isRegistered()
   :outertype: File

   Checks if the file is already registered to a Replica Catalogue.

   :return: \ ``true``\  if it is registered, \ ``false``\  otherwise

isValid
^^^^^^^

.. java:method:: public static boolean isValid(String fileName)
   :outertype: File

   Check if the name of a file is valid or not.

   :param fileName: the file name to be checked for validity
   :return: \ ``true``\  if the file name is valid, \ ``false``\  otherwise

isValid
^^^^^^^

.. java:method:: public static boolean isValid(File file)
   :outertype: File

   Check if a file object is valid or not. This method checks whether the given file object itself and its file name are valid.

   :param file: the file to be checked for validity
   :return: \ ``true``\  if the file is valid, \ ``false``\  otherwise

makeMasterCopy
^^^^^^^^^^^^^^

.. java:method:: public File makeMasterCopy()
   :outertype: File

   Clone the current file and make the new file as a \ **master**\  copy as well.

   :return: a clone of the current file (as a master copy) or \ ``null``\  if an error occurs

makeReplica
^^^^^^^^^^^

.. java:method:: public File makeReplica()
   :outertype: File

   Clone the current file and set the cloned one as a \ **replica**\ .

   :return: a clone of the current file (as a replica) or \ ``null``\  if an error occurs

setAttribute
^^^^^^^^^^^^

.. java:method:: protected void setAttribute(FileAttribute attribute)
   :outertype: File

   Sets an attribute of this file.

   :param attribute: file attribute

setChecksum
^^^^^^^^^^^

.. java:method:: public boolean setChecksum(int checksum)
   :outertype: File

   Sets the checksum of the file.

   :param checksum: the checksum of this file
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setCost
^^^^^^^

.. java:method:: public boolean setCost(double cost)
   :outertype: File

   Sets the cost associated with the file.

   :param cost: cost of this file
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setDatacenter
^^^^^^^^^^^^^

.. java:method:: public final File setDatacenter(Datacenter datacenter)
   :outertype: File

   Sets the Datacenter that will store the file. When the file is added to a \ :java:ref:`FileStorage`\  and such a storage is attached to a Datacenter, the Datacenter sets itself for all files of that storage.

   :param datacenter: the Datacenter that will store the file

setDeleted
^^^^^^^^^^

.. java:method:: public void setDeleted(boolean deleted)
   :outertype: File

   Sets the file as deleted or not.

   :param deleted: \ ``true``\  if it was deleted, false otherwise

setMasterCopy
^^^^^^^^^^^^^

.. java:method:: public void setMasterCopy(boolean masterCopy)
   :outertype: File

   Marks the file as a master copy or replica.

   :param masterCopy: a flag denotes \ ``true``\  for master copy or \ ``false``\  for a replica

setName
^^^^^^^

.. java:method:: public final void setName(String name)
   :outertype: File

   Sets the file name.

   :param name: the file name

setOwnerName
^^^^^^^^^^^^

.. java:method:: public boolean setOwnerName(String name)
   :outertype: File

   Sets the owner name of this file.

   :param name: the owner name
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setRegistrationID
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean setRegistrationID(int id)
   :outertype: File

   Sets the file registration ID (published by a Replica Catalogue entity).

   :param id: registration ID
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setSize
^^^^^^^

.. java:method:: public boolean setSize(int fileSize)
   :outertype: File

   Sets the file size (in MBytes).

   :param fileSize: the file size (in MBytes)
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setTransactionTime
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean setTransactionTime(double time)
   :outertype: File

   Sets the current transaction time (in second) of this file. This transaction time can be related to the operation of adding, deleting or getting the file on a Datacenter's storage.

   :param time: the transaction time (in second)
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setType
^^^^^^^

.. java:method:: public boolean setType(int type)
   :outertype: File

   Sets the file type (for instance, raw, tag, etc).

   :param type: a file type
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setUpdateTime
^^^^^^^^^^^^^

.. java:method:: public boolean setUpdateTime(double time)
   :outertype: File

   Sets the last update time of this file (in seconds).  NOTE: This time is relative to the start time. Preferably use \ :java:ref:`org.cloudbus.cloudsim.core.CloudSim.clock()`\  method.

   :param time: the last update time (in seconds)
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: File

