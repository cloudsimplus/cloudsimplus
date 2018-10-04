TraceField
==========

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: public interface TraceField<R extends GoogleTraceReaderAbstract>

   An interface to be implemented by \ :java:ref:`Enum`\ s representing a field in a Google Trace File. Each enum instance is used to get values from fields of the trace in the correct generic type T and possibly making some unit conversions (if required by the specific field represented by the enum instance).

   :author: Manoel Campos da Silva Filho

Methods
-------
getValue
^^^^^^^^

.. java:method::  <T> T getValue(R reader)
   :outertype: TraceField

   Gets the value (from a line read from a trace file) of the field associated to the enum instance.

   :param reader: the reader for the trace file
   :param <T>: the type to convert the value read from the trace to
   :return: the field value converted to a specific type

