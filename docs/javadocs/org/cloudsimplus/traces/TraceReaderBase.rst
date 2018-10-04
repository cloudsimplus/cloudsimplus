.. java:import:: org.cloudbus.cloudsim.util TraceReaderAbstract

.. java:import:: java.io InputStream

.. java:import:: java.util Objects

TraceReaderBase
===============

.. java:package:: org.cloudsimplus.traces
   :noindex:

.. java:type:: public abstract class TraceReaderBase extends TraceReaderAbstract

   An abstract class providing additional features for subclasses implementing trace file readers for specific file formats.

   :author: Manoel Campos da Silva Filho

Constructors
------------
TraceReaderBase
^^^^^^^^^^^^^^^

.. java:constructor:: protected TraceReaderBase(String filePath, InputStream reader)
   :outertype: TraceReaderBase

Methods
-------
getFieldDoubleValue
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected <T extends Enum> double getFieldDoubleValue(T field)
   :outertype: TraceReaderBase

   Gets a field's value from the \ :java:ref:`last parsed line <getLastParsedLineArray()>`\  as double.

   :param field: a enum value representing the index of the field to get the value

getFieldDoubleValue
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected <T extends Enum> double getFieldDoubleValue(T field, double defaultValue)
   :outertype: TraceReaderBase

   Gets a field's value from the \ :java:ref:`last parsed line <getLastParsedLineArray()>`\  as double.

   :param field: a enum value representing the index of the field to get the value
   :param defaultValue: the default value to be returned if the field value is not a number

getFieldIntValue
^^^^^^^^^^^^^^^^

.. java:method:: protected <T extends Enum> int getFieldIntValue(T field)
   :outertype: TraceReaderBase

   Gets a field's value from the \ :java:ref:`last parsed line <getLastParsedLineArray()>`\  as an int.

   :param field: a enum value representing the index of the field to get the value

getFieldIntValue
^^^^^^^^^^^^^^^^

.. java:method:: protected <T extends Enum> int getFieldIntValue(T field, int defaultValue)
   :outertype: TraceReaderBase

   Gets a field's value from the \ :java:ref:`last parsed line <getLastParsedLineArray()>`\  as an int.

   :param field: a enum value representing the index of the field to get the value
   :param defaultValue: the default value to be returned if the field value is not an int

getFieldLongValue
^^^^^^^^^^^^^^^^^

.. java:method:: protected <T extends Enum> long getFieldLongValue(T field)
   :outertype: TraceReaderBase

   Gets a field's value from the \ :java:ref:`last parsed line <getLastParsedLineArray()>`\  as an int.

   :param field: a enum value representing the index of the field to get the value

getFieldLongValue
^^^^^^^^^^^^^^^^^

.. java:method:: protected <T extends Enum> long getFieldLongValue(T field, long defaultValue)
   :outertype: TraceReaderBase

   Gets a field's value from the \ :java:ref:`last parsed line <getLastParsedLineArray()>`\  as an int.

   :param field: a enum value representing the index of the field to get the value
   :param defaultValue: the default value to be returned if the field value is not an int

getFieldValue
^^^^^^^^^^^^^

.. java:method:: protected <T extends Enum> String getFieldValue(T field)
   :outertype: TraceReaderBase

   Gets a field's value from the \ :java:ref:`last parsed line <getLastParsedLineArray()>`\  as String.

   :param field: a enum value representing the index of the field to get the value

getLastParsedLineArray
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected String[] getLastParsedLineArray()
   :outertype: TraceReaderBase

   Gets an array containing the field values from the last parsed trace line.

setLastParsedLineArray
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setLastParsedLineArray(String[] lastParsedLineArray)
   :outertype: TraceReaderBase

   Sets an array containing the field values from the last parsed trace line.

   :param lastParsedLineArray: the field values from the last parsed trace line

