.. java:import:: org.apache.commons.math3.random RandomGenerator

.. java:import:: java.util.concurrent ThreadLocalRandom

JDKThreadLocalRandomGenerator
=============================

.. java:package:: org.cloudbus.cloudsim.distributions
   :noindex:

.. java:type:: public final class JDKThreadLocalRandomGenerator implements RandomGenerator

   A \ :java:ref:`RandomGenerator`\  that internally uses the \ :java:ref:`ThreadLocalRandom`\ , a very fast Pseudo-Random Number Generator (PRNG) with higher performance than \ :java:ref:`java.util.Random`\ , mainly in concurrent environments. The \ :java:ref:`ThreadLocalRandom`\  also has much better performance than PRNGs available in \ :java:ref:`org.apache.commons.math3`\ , despite it probably has worse statistical properties.

   This generator has some drawbacks. It only generates the seed internally and doesn't allow setting an explicit seed. Calling the \ ``setSeed()``\  methods will throw an \ :java:ref:`UnsupportedOperationException`\  and there is no way to get the generated seed. This later issue makes it impossible to reproduce a simulation experiment to verify the generated results if the seed is unknown.

   Finally, it doesn't allow applying the \ `Antithetic Variates Technique <https://en.wikipedia.org/wiki/Antithetic_variates>`_\  in order to try reducing variance of experiments using the generated numbers. Classes such as \ :java:ref:`UniformDistr`\  provide such a feature if the underlying PRNG allows setting a seed. That is explained because the technique is applied when multiple runs of the same simulation are executed. In such scenario, the second half of experiments have to use the seeds from the first half.

   :author: Manoel Campos da Silva Filho

Methods
-------
getInstance
^^^^^^^^^^^

.. java:method:: public static JDKThreadLocalRandomGenerator getInstance()
   :outertype: JDKThreadLocalRandomGenerator

nextBoolean
^^^^^^^^^^^

.. java:method:: @Override public boolean nextBoolean()
   :outertype: JDKThreadLocalRandomGenerator

nextBytes
^^^^^^^^^

.. java:method:: @Override public void nextBytes(byte[] bytes)
   :outertype: JDKThreadLocalRandomGenerator

nextDouble
^^^^^^^^^^

.. java:method:: @Override public double nextDouble()
   :outertype: JDKThreadLocalRandomGenerator

nextFloat
^^^^^^^^^

.. java:method:: @Override public float nextFloat()
   :outertype: JDKThreadLocalRandomGenerator

nextGaussian
^^^^^^^^^^^^

.. java:method:: @Override public double nextGaussian()
   :outertype: JDKThreadLocalRandomGenerator

nextInt
^^^^^^^

.. java:method:: @Override public int nextInt()
   :outertype: JDKThreadLocalRandomGenerator

nextInt
^^^^^^^

.. java:method:: @Override public int nextInt(int n)
   :outertype: JDKThreadLocalRandomGenerator

nextLong
^^^^^^^^

.. java:method:: @Override public long nextLong()
   :outertype: JDKThreadLocalRandomGenerator

setSeed
^^^^^^^

.. java:method:: @Override public void setSeed(int seed)
   :outertype: JDKThreadLocalRandomGenerator

setSeed
^^^^^^^

.. java:method:: @Override public void setSeed(int[] seed)
   :outertype: JDKThreadLocalRandomGenerator

setSeed
^^^^^^^

.. java:method:: @Override public void setSeed(long seed)
   :outertype: JDKThreadLocalRandomGenerator

