.. java:import:: ch.qos.logback.classic Level

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

Log
===

.. java:package:: org.cloudsimplus.util
   :noindex:

.. java:type:: public final class Log

   An utility class to enable changing logging configuration such as the logging level.

   :author: Manoel Campos da Silva Filho

Methods
-------
setLevel
^^^^^^^^

.. java:method:: public static void setLevel(Logger logger, Level level)
   :outertype: Log

   Sets the logging \ :java:ref:`Level`\  for a given LOGGER instance. You can enable just a specific type of log messages by using, for instance, \ :java:ref:`Level.WARN`\  value. To completely disable the given LOGGER, use \ :java:ref:`Level.OFF`\ .

   :param level: the logging level to set

setLevel
^^^^^^^^

.. java:method:: public static void setLevel(Level level)
   :outertype: Log

   Sets the logging \ :java:ref:`Level`\  for \ **all LOGGER instances**\ . You can enable just a specific type of log messages by using, for instance, \ :java:ref:`Level.WARN`\  value. To completely disable logging, use \ :java:ref:`Level.OFF`\ .

   :param level: the logging level to set

