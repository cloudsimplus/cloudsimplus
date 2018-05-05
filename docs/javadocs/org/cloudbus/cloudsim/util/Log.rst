.. java:import:: java.io IOException

.. java:import:: java.io OutputStream

.. java:import:: java.lang.management ManagementFactory

Log
===

.. java:package:: org.cloudbus.cloudsim.util
   :noindex:

.. java:type:: public final class Log

   Logger used for performing logging of the simulation process. It provides the ability to substitute the output stream by any OutputStream subclass.

   :author: Anton Beloglazov

Methods
-------
disable
^^^^^^^

.. java:method:: public static void disable()
   :outertype: Log

   Disables the output.

enable
^^^^^^

.. java:method:: public static void enable()
   :outertype: Log

   Enables the output.

getOutput
^^^^^^^^^

.. java:method:: public static OutputStream getOutput()
   :outertype: Log

   Gets the output stream.

   :return: the output

isDebug
^^^^^^^

.. java:method:: public static boolean isDebug()
   :outertype: Log

isDisabled
^^^^^^^^^^

.. java:method:: public static boolean isDisabled()
   :outertype: Log

   Checks if the output is disabled.

   :return: true, if it is disable

isEnabled
^^^^^^^^^

.. java:method:: public static boolean isEnabled()
   :outertype: Log

   Checks if the output is enabled.

   :return: true, if it is enable

print
^^^^^

.. java:method:: public static void print(String message)
   :outertype: Log

   Prints a message.

   :param message: the message

print
^^^^^

.. java:method:: public static void print(Object message)
   :outertype: Log

   Prints the message passed as a non-String object.

   :param message: the message

printConcatLine
^^^^^^^^^^^^^^^

.. java:method:: public static void printConcatLine(Object... messages)
   :outertype: Log

   Prints the concatenated text representation of the arguments and a new line.

   :param messages: the messages to print

printFormatted
^^^^^^^^^^^^^^

.. java:method:: public static void printFormatted(String format, Object... args)
   :outertype: Log

   Prints a string formatted as in String.printFormatted().

   :param format: the printFormatted
   :param args: the args

printFormattedLine
^^^^^^^^^^^^^^^^^^

.. java:method:: public static void printFormattedLine(String format, Object... args)
   :outertype: Log

   Prints a string formatted as in String.printFormatted(), followed by a new line.

   :param format: the printFormatted
   :param args: the args

printLine
^^^^^^^^^

.. java:method:: public static void printLine(String message)
   :outertype: Log

   Prints a message and a new line.

   :param message: the message

printLine
^^^^^^^^^

.. java:method:: public static void printLine()
   :outertype: Log

   Prints an empty line.

printLine
^^^^^^^^^

.. java:method:: public static void printLine(Object message)
   :outertype: Log

   Prints the message passed as a non-String object and a new line.

   :param message: the message

println
^^^^^^^

.. java:method:: public static void println(Level level, Class klass, double time, String format, Object... args)
   :outertype: Log

   Prints a string formatted as in String.printFormatted(), followed by a new line, that will be printed only according to the specified level

   :param level: the level that define the kind of message
   :param klass: Class that is asking to print a message (where the print method is being called)
   :param time: current simulation time
   :param format: the printFormatted
   :param args: the args

setDisabled
^^^^^^^^^^^

.. java:method:: public static void setDisabled(boolean disable)
   :outertype: Log

   Sets the disable output flag.

   :param disable: the new disabled

setOutput
^^^^^^^^^

.. java:method:: public static void setOutput(OutputStream newOutput)
   :outertype: Log

   Sets the output stream.

   :param newOutput: the new output

