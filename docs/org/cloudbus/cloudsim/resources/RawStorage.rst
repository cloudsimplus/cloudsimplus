RawStorage
==========

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public final class RawStorage extends ResourceAbstract

   A simple storage that just manages the device capacity and raw allocated (used) space. It doesn't deals with files neither with file system operations such as file inclusion or deletion.

   :author: Manoel Campos da Silva Filho

Constructors
------------
RawStorage
^^^^^^^^^^

.. java:constructor:: public RawStorage(long capacity)
   :outertype: RawStorage

   Creates a new Storage device.

   :param capacity: the storage capacity in Megabytes

