.. java:import:: org.cloudbus.cloudsim.core ChangeableId

.. java:import:: org.cloudbus.cloudsim.provisioners PeProvisioner

Pe.Status
=========

.. java:package:: org.cloudbus.cloudsim.resources
   :noindex:

.. java:type::  enum Status
   :outertype: Pe

   Status of PEs.

Enum Constants
--------------
BUSY
^^^^

.. java:field:: public static final Pe.Status BUSY
   :outertype: Pe.Status

   Denotes PE is allocated and hence busy processing some Cloudlet.

FAILED
^^^^^^

.. java:field:: public static final Pe.Status FAILED
   :outertype: Pe.Status

   Denotes PE is failed and hence it can't process any Cloudlet at this moment. This PE is failed because it belongs to a machine which is also failed.

FREE
^^^^

.. java:field:: public static final Pe.Status FREE
   :outertype: Pe.Status

   Denotes PE is FREE for allocation.

