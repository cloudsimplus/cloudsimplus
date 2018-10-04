.. java:import:: org.apache.commons.lang3 StringUtils

.. java:import:: org.cloudbus.cloudsim.util Conversion

.. java:import:: org.cloudbus.cloudsim.util DataCloudTags

.. java:import:: java.util Calendar

.. java:import:: java.util Date

.. java:import:: java.util Objects

FileAttribute
=============

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type:: public class FileAttribute

   Stores related information regarding to a \ :java:ref:`org.cloudbus.cloudsim.resources.File`\  entity.

   :author: Uros Cibej, Anthony Sulistio

Constructors
------------
FileAttribute
^^^^^^^^^^^^^

.. java:constructor:: public FileAttribute(File file, int fileSize)
   :outertype: FileAttribute

   Creates a new FileAttribute object.

   :param file: the file that this attribute object is related to
   :param fileSize: the size for the File

Methods
-------
copyValue
^^^^^^^^^

.. java:method:: public void copyValue(FileAttribute destinationAttr)
   :outertype: FileAttribute

   Copy the values of the object into a given FileAttribute instance.

   :param destinationAttr: the destination FileAttribute object to copy the current object to

getAttributeSize
^^^^^^^^^^^^^^^^

.. java:method:: public int getAttributeSize()
   :outertype: FileAttribute

   Gets the size of the object (in byte).  NOTE: This object size is NOT the actual file size. Moreover, this size is used for transferring this object over a network.

   :return: the object size (in byte)

getChecksum
^^^^^^^^^^^

.. java:method:: public int getChecksum()
   :outertype: FileAttribute

   Gets the file checksum.

   :return: file checksum

getCost
^^^^^^^

.. java:method:: public double getCost()
   :outertype: FileAttribute

   Gets the cost associated with the file.

   :return: the cost of this file

getCreationTime
^^^^^^^^^^^^^^^

.. java:method:: public long getCreationTime()
   :outertype: FileAttribute

   Gets the file creation time (in millisecond).

   :return: the file creation time (in millisecond)

getFileSize
^^^^^^^^^^^

.. java:method:: public int getFileSize()
   :outertype: FileAttribute

   Gets the file size (in MBytes).

   :return: the file size (in MBytes)

getFileSizeInByte
^^^^^^^^^^^^^^^^^

.. java:method:: public int getFileSizeInByte()
   :outertype: FileAttribute

   Gets the file size (in bytes).

   :return: the file size (in bytes)

getLastUpdateTime
^^^^^^^^^^^^^^^^^

.. java:method:: public double getLastUpdateTime()
   :outertype: FileAttribute

   Gets the last update time (in seconds).

   :return: the last update time (in seconds)

getOwnerName
^^^^^^^^^^^^

.. java:method:: public String getOwnerName()
   :outertype: FileAttribute

   Gets the owner name of the file.

   :return: the owner name or \ ``null``\  if empty

getRegistrationID
^^^^^^^^^^^^^^^^^

.. java:method:: public long getRegistrationID()
   :outertype: FileAttribute

   Gets the file registration ID.

   :return: registration ID

getType
^^^^^^^

.. java:method:: public int getType()
   :outertype: FileAttribute

   Gets the file type.

   :return: file type

isMasterCopy
^^^^^^^^^^^^

.. java:method:: public boolean isMasterCopy()
   :outertype: FileAttribute

   Checks whether the file is a master copy or replica.

   :return: \ ``true``\  if it is a master copy or \ ``false``\  if it is a replica

isRegistered
^^^^^^^^^^^^

.. java:method:: public boolean isRegistered()
   :outertype: FileAttribute

   Checks if the file is already registered to a Replica Catalogue.

   :return: \ ``true``\  if it is registered, \ ``false``\  otherwise

isValid
^^^^^^^

.. java:method:: public static boolean isValid(String fileName)
   :outertype: FileAttribute

   Check if the name of a file is valid or not.

   :param fileName: the file name to be checked for validity
   :return: \ ``true``\  if the file name is valid, \ ``false``\  otherwise

setChecksum
^^^^^^^^^^^

.. java:method:: public boolean setChecksum(int checksum)
   :outertype: FileAttribute

   Sets the checksum of the file.

   :param checksum: the checksum of this file
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setCost
^^^^^^^

.. java:method:: public boolean setCost(double cost)
   :outertype: FileAttribute

   Sets the cost associated with the file.

   :param cost: cost of this file
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setCreationTime
^^^^^^^^^^^^^^^

.. java:method:: public boolean setCreationTime(long creationTime)
   :outertype: FileAttribute

   Sets the file creation time (in millisecond).

   :param creationTime: the file creation time (in millisecond)
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setFileSize
^^^^^^^^^^^

.. java:method:: public final boolean setFileSize(int fileSize)
   :outertype: FileAttribute

   Sets the file size (in MBytes).

   :param fileSize: the file size (in MBytes)
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setMasterCopy
^^^^^^^^^^^^^

.. java:method:: public void setMasterCopy(boolean masterCopy)
   :outertype: FileAttribute

   Marks the file as a master copy or replica.

   :param masterCopy: a flag denotes \ ``true``\  for master copy or \ ``false``\  for a replica

setOwnerName
^^^^^^^^^^^^

.. java:method:: public boolean setOwnerName(String name)
   :outertype: FileAttribute

   Sets the owner name of the file.

   :param name: the owner name
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setRegistrationId
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean setRegistrationId(long id)
   :outertype: FileAttribute

   Sets the file registration ID (published by a Replica Catalogue entity).

   :param id: registration ID
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setType
^^^^^^^

.. java:method:: public boolean setType(int type)
   :outertype: FileAttribute

   Sets the file type (for instance raw, tag, etc).

   :param type: a file type
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

setUpdateTime
^^^^^^^^^^^^^

.. java:method:: public boolean setUpdateTime(double time)
   :outertype: FileAttribute

   Sets the last update time of the file (in seconds).  NOTE: This time is relative to the start time. Preferably use \ :java:ref:`org.cloudbus.cloudsim.core.CloudSim.clock()`\  method.

   :param time: the last update time (in seconds)
   :return: \ ``true``\  if successful, \ ``false``\  otherwise

