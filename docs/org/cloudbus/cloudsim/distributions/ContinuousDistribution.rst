ContinuousDistribution
======================

.. java:package:: PackageDeclaration
   :noindex:

.. java:type:: public interface ContinuousDistribution

   Interface to be implemented by a pseudo random number generator.

   :author: Marcos Dias de Assuncao

Fields
------
NULL
^^^^

.. java:field::  ContinuousDistribution NULL
   :outertype: ContinuousDistribution

   A property that implements the Null Object Design Pattern for \ :java:ref:`ContinuousDistribution`\  objects.

Methods
-------
getSeed
^^^^^^^

.. java:method::  long getSeed()
   :outertype: ContinuousDistribution

   :return: the seed used to initialize the generator

sample
^^^^^^

.. java:method::  double sample()
   :outertype: ContinuousDistribution

   Generate a new pseudo random number.

   :return: the next pseudo random number in the sequence, following the implemented distribution.

