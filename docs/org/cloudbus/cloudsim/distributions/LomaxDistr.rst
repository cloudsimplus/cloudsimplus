LomaxDistr
==========

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public class LomaxDistr extends ParetoDistr

   A pseudo random number generator following the \ `Lomax distribution <https://en.wikipedia.org/wiki/Lomax_distribution>`_\ .

   :author: Marcos Dias de Assuncao

Constructors
------------
LomaxDistr
^^^^^^^^^^

.. java:constructor:: public LomaxDistr(double shape, double location, double shift)
   :outertype: LomaxDistr

   Instantiates a new lomax pseudo random number generator.

   :param shape: the shape
   :param location: the location
   :param shift: the shift

LomaxDistr
^^^^^^^^^^

.. java:constructor:: public LomaxDistr(long seed, double shape, double location, double shift)
   :outertype: LomaxDistr

   Instantiates a new lomax pseudo random number generator.

   :param seed: the seed
   :param shape: the shape
   :param location: the location
   :param shift: the shift

Methods
-------
sample
^^^^^^

.. java:method:: @Override public double sample()
   :outertype: LomaxDistr

