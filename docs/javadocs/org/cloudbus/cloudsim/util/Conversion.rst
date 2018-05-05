Conversion
==========

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class Conversion

   Provides a set of methods for unit conversion.

   :author: Manoel Campos da Silva Filho

Fields
------
GIBABYTE
^^^^^^^^

.. java:field:: public static final double GIBABYTE
   :outertype: Conversion

   The value of 1 GibaByte in Bytes.

   **See also:** :java:ref:`.MEGABYTE`

HUNDRED_PERCENT
^^^^^^^^^^^^^^^

.. java:field:: public static final double HUNDRED_PERCENT
   :outertype: Conversion

   A value that represents 100% in a scale from 0 to 1.

KILOBYTE
^^^^^^^^

.. java:field:: public static final double KILOBYTE
   :outertype: Conversion

   The value of 1 KiloByte in Bytes. It is declared as double because such a value is commonly used in divisions. By this way, it avoids explicit double casts to ensure a double instead an integer division.

MEGABYTE
^^^^^^^^

.. java:field:: public static final double MEGABYTE
   :outertype: Conversion

   The value of 1 MegaByte in Bytes.

   **See also:** :java:ref:`.KILOBYTE`

MILLION
^^^^^^^

.. java:field:: public static final int MILLION
   :outertype: Conversion

   One million in absolute value, usually used to convert to and from Number of Instructions (I) and Million Instructions (MI) units.

Methods
-------
bitesToBytes
^^^^^^^^^^^^

.. java:method:: public static double bitesToBytes(double bits)
   :outertype: Conversion

   Converts any value in bits to bytes, doesn't matter if the unit is Kilobites (Kb), Megabites (Mb), Gigabites (Gb), etc.

   :param bits: the value in bites, Kb, Mb, Gb, etc
   :return: the value in bites, Kbytes, Mbytes, Gbytes and so on, according to the given value

bytesToBits
^^^^^^^^^^^

.. java:method:: public static double bytesToBits(double bytes)
   :outertype: Conversion

   Converts any value in bytes to bits, doesn't matter if the unit is Kilobytes (KB), Megabytes (MB), Gigabytes (GB), etc.

   :param bytes: the value in bytes, KB, MB, GB, etc
   :return: the value in bites, Kbits, Mbits, Gbits and so on, according to the given value

bytesToMegaBits
^^^^^^^^^^^^^^^

.. java:method:: public static double bytesToMegaBits(double bytes)
   :outertype: Conversion

   Converts a value in bytes to Megabites (Mb)

   :param bytes: the value in bytes
   :return: the value in Megabites (Mb)

bytesToMegaBytes
^^^^^^^^^^^^^^^^

.. java:method:: public static double bytesToMegaBytes(double bytes)
   :outertype: Conversion

   Converts a value in bytes to MegaBytes (MB)

   :param bytes: the value in bytes
   :return: the value in MegaBytes (MB)

