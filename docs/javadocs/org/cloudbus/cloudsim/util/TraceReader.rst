TraceReader
===========

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public interface TraceReader

   A basic interface for classes that read specific trace file formats.

   :author: Marcos Dias de Assuncao, Manoel Campos da Silva Filho

Methods
-------
getCommentString
^^^^^^^^^^^^^^^^

.. java:method::  String[] getCommentString()
   :outertype: TraceReader

   Gets the Strings that identifies the start of a comment line. For instance \ **; # % //**\ .

getFieldDelimiterRegex
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  String getFieldDelimiterRegex()
   :outertype: TraceReader

   Gets the regex defining how fields are delimited in the trace file. Usually, this can be just a String with a single character such as a space, comma, semi-colon or tab (\t).

getFilePath
^^^^^^^^^^^

.. java:method::  String getFilePath()
   :outertype: TraceReader

   Gets the path of the trace file.

getMaxLinesToRead
^^^^^^^^^^^^^^^^^

.. java:method::  int getMaxLinesToRead()
   :outertype: TraceReader

   Gets the maximum number of lines of the workload reader that will be read. The value -1 indicates that all lines will be read, creating a cloudlet from every one.

setCommentString
^^^^^^^^^^^^^^^^

.. java:method::  TraceReader setCommentString(String... commentString)
   :outertype: TraceReader

   Sets a string that identifies the start of a comment line. If there are multiple ways to comment a line, the different Strings representing comments can be specified as parameters.

   :param commentString: the comment Strings to set

setFieldDelimiterRegex
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  TraceReader setFieldDelimiterRegex(String fieldDelimiterRegex)
   :outertype: TraceReader

   Sets the regex defining how fields are delimited in the trace file. Usually, this can be just a String with a single character such as a space, comma or semi-colon or tab (\t).

   :param fieldDelimiterRegex: the field separator regex to set

setMaxLinesToRead
^^^^^^^^^^^^^^^^^

.. java:method::  TraceReader setMaxLinesToRead(int maxLinesToRead)
   :outertype: TraceReader

   Sets the maximum number of lines of the workload reader that will be read. The value -1 indicates that all lines will be read, creating a cloudlet from every one.

   :param maxLinesToRead: the maximum number of lines to set

