Conversion
==========

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class Conversion

   Provides a set of methods for unit conversion.

   :author: Manoel Campos da Silva Filho

Fields
------
GIGA
^^^^

.. java:field:: public static final double GIGA
   :outertype: Conversion

   The value of 1 GigaByte in Bytes or 1 Gigabit in bits.

   **See also:** :java:ref:`.MEGA`

HUNDRED_PERCENT
^^^^^^^^^^^^^^^

.. java:field:: public static final double HUNDRED_PERCENT
   :outertype: Conversion

   A value that represents 100% in a scale from 0 to 1.

KILO
^^^^

.. java:field:: public static final double KILO
   :outertype: Conversion

   The value of 1 KiloByte in Bytes or 1 Kilobit in bits. It is declared as double because such a value is commonly used in divisions. This way, it avoids explicit double casts to ensure a double instead an integer division.

MEGA
^^^^

.. java:field:: public static final double MEGA
   :outertype: Conversion

   The value of 1 MegaByte in Bytes or 1 Megabit in bits.

   **See also:** :java:ref:`.KILO`

MILLION
^^^^^^^

.. java:field:: public static final int MILLION
   :outertype: Conversion

   One million in absolute value, usually used to convert to and from Number of Instructions (I) and Million Instructions (MI) units.

TERA
^^^^

.. java:field:: public static final double TERA
   :outertype: Conversion

   The value of 1 TeraByte in Bytes or 1 TeraBit in bits.

   **See also:** :java:ref:`.GIGA`

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

bytesToGigaBytes
^^^^^^^^^^^^^^^^

.. java:method:: public static double bytesToGigaBytes(double bytes)
   :outertype: Conversion

   Converts a value in bytes to GigaBytes (GB)

   :param bytes: the value in bytes
   :return: the value in GigaBytes (GB)

bytesToKiloBytes
^^^^^^^^^^^^^^^^

.. java:method:: public static double bytesToKiloBytes(double bytes)
   :outertype: Conversion

   Converts a value in bytes to KiloBytes (KB)

   :param bytes: the value in bytes
   :return: the value in KiloBytes (KB)

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

bytesToSuitableUnit
^^^^^^^^^^^^^^^^^^^

.. java:method:: public static String bytesToSuitableUnit(double bytes)
   :outertype: Conversion

   Converts a value in bytes to the most suitable unit, such as Kilobytes (KB), MegaBytes (MB) or Gigabytes (GB)

   :param bytes: the value in bytes
   :return: the converted value concatenated with the unit converted to (KB, MB or GB)

gigaToMega
^^^^^^^^^^

.. java:method:: public static double gigaToMega(double giga)
   :outertype: Conversion

   Converts any value in giga to mega, doesn't matter if it's gigabits or gigabytes.

   :param giga: the value in gigabits or gigabytes
   :return: the value in megabits or megabytes (according to the input value)

megaBytesToBytes
^^^^^^^^^^^^^^^^

.. java:method:: public static double megaBytesToBytes(double megaBytes)
   :outertype: Conversion

   Converts a value in MegaBytes (MB) to bytes

   :param megaBytes: the value in MegaBytes (MB)
   :return: the value in bytes

microToMilli
^^^^^^^^^^^^

.. java:method:: public static double microToMilli(double micro)
   :outertype: Conversion

   Converts any value in micro (μ) to milli (m) scale, such as microseconds to milliseconds.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param micro: the value in micro (μ) scale
   :return: the value in milli (m) scale

microToSeconds
^^^^^^^^^^^^^^

.. java:method:: public static double microToSeconds(double micro)
   :outertype: Conversion

   Converts any value in microseconds (μ) to seconds.

   The existing \ :java:ref:`java.util.concurrent.TimeUnit`\  and \ :java:ref:`java.time.Duration`\  classes don't provide the double precision required here.

   :param micro: the value in microseconds (μ)
   :return: the value in seconds

teraToGiga
^^^^^^^^^^

.. java:method:: public static double teraToGiga(double tera)
   :outertype: Conversion

   Converts any value in tera to giga, doesn't matter if it's terabits or terabytes.

   :param tera: the value in terabits or terabytes
   :return: the value in gigabits or gigabytes (according to the input value)

teraToMega
^^^^^^^^^^

.. java:method:: public static double teraToMega(double tera)
   :outertype: Conversion

   Converts any value in tera to mega, doesn't matter if it's terabits or terabytes.

   :param tera: the value in terabits or terabytes
   :return: the value in megabits or megabytes (according to the input value)

