DataCloudTags
=============

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class DataCloudTags

   Contains additional tags for DataCloud features, such as file information retrieval, file transfers, and storage info.

   :author: Uros Cibej, Anthony Sulistio

Fields
------
CTLG_BASE
^^^^^^^^^

.. java:field:: public static final int CTLG_BASE
   :outertype: DataCloudTags

   Base value for catalogue tags.

CTLG_DELETE_MASTER
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int CTLG_DELETE_MASTER
   :outertype: DataCloudTags

   Denotes the request to de-register / delete a master file from the Replica Catalogue.

   The format of this request is Object[2] = {String lfn, Integer resourceID}.

   The reply tag name is \ :java:ref:`CTLG_DELETE_MASTER_RESULT`\ .

CTLG_DELETE_MASTER_RESULT
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int CTLG_DELETE_MASTER_RESULT
   :outertype: DataCloudTags

   Sends the result of de-registering a master file back to sender.

   The format of the reply is Object[2] = {String lfn, Integer resultID}.

   NOTE: The result id is in the form of CTLG_DELETE_MASTER_XXXX where XXXX means the error/success message

DEFAULT_MTU
^^^^^^^^^^^

.. java:field:: public static final int DEFAULT_MTU
   :outertype: DataCloudTags

   Default Maximum Transmission Unit (MTU) of a link in bytes.

FILE_ADD_ERROR_EXIST_READ_ONLY
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int FILE_ADD_ERROR_EXIST_READ_ONLY
   :outertype: DataCloudTags

   Denotes that file addition is failed because the file already exists in the catalogue and it is read-only file.

FILE_ADD_ERROR_STORAGE_FULL
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int FILE_ADD_ERROR_STORAGE_FULL
   :outertype: DataCloudTags

   Denotes that file addition is failed because the storage is full.

FILE_ADD_SUCCESSFUL
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int FILE_ADD_SUCCESSFUL
   :outertype: DataCloudTags

   Denotes that file addition is successful.

FILE_DELETE_ERROR
^^^^^^^^^^^^^^^^^

.. java:field:: public static final int FILE_DELETE_ERROR
   :outertype: DataCloudTags

   Denotes that file deletion is failed due to an unknown error.

FILE_DELETE_SUCCESSFUL
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int FILE_DELETE_SUCCESSFUL
   :outertype: DataCloudTags

   Denotes that file deletion is successful.

PKT_SIZE
^^^^^^^^

.. java:field:: public static final int PKT_SIZE
   :outertype: DataCloudTags

   The default packet size (in byte) for sending events to other entity.

RM_BASE
^^^^^^^

.. java:field:: public static final int RM_BASE
   :outertype: DataCloudTags

   Base value used for Replica Manager tags.

