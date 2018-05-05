.. java:import:: org.cloudbus.cloudsim.allocationpolicies VmAllocationPolicy

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling.resources ResourceScaling

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.listeners VmHostEventInfo

.. java:import:: java.util.function Function

VerticalVmScaling
=================

.. java:package:: org.cloudsimplus.autoscaling
   :noindex:

.. java:type:: public interface VerticalVmScaling extends VmScaling

   A Vm \ `Vertical Scaling <https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling>`_\  mechanism used by a \ :java:ref:`DatacenterBroker`\  to request the dynamic scale of VM resources up or down, according to the current resource usage. For each resource supposed to be scaled, a different \ ``VerticalVmScaling``\  instance should be provided. If a scaling object is going to be set to a Vm, it has to be exclusive of that Vm. Different Vms must have different instances of a scaling object.

   A \ :java:ref:`Vm`\  runs a set of \ :java:ref:`Cloudlet`\ s. When a \ ``VerticalVmScaling``\  object is attached to a \ :java:ref:`Vm`\ , it's required to define which \ :java:ref:`resource will be scaled <getResourceClass()>`\  (\ :java:ref:`Ram`\ , \ :java:ref:`Bandwidth`\ , etc) when it's \ :java:ref:`under <getLowerThresholdFunction()>`\  or \ :java:ref:`overloaded <getUpperThresholdFunction()>`\ .

   The scaling request follows this path:

   ..

   * a \ :java:ref:`Vm`\  that has a \ :java:ref:`VerticalVmScaling`\  object set monitors its own resource usage using an \ :java:ref:`EventListener`\ , to check if an \ :java:ref:`under <getLowerThresholdFunction()>`\  or \ :java:ref:`overload <getUpperThresholdFunction()>`\  condition is met;
   * if any of these conditions is met, the Vm uses the VerticalVmScaling to send a scaling request to its \ :java:ref:`DatacenterBroker`\ ;
   * the DatacenterBroker fowards the request to the \ :java:ref:`Datacenter`\  where the Vm is hosted;
   * the Datacenter delegates the task to its \ :java:ref:`VmAllocationPolicy`\ ;
   * the VmAllocationPolicy checks if there is resource availability and then finally scale the Vm.

   \ **WARNING**\

   Make sure that the \ :java:ref:`UtilizationModel`\  of some of these \ ``Cloudlets``\  is defined as \ :java:ref:`ABSOLUTE <Unit.ABSOLUTE>`\ . Defining the \ ``UtilizationModel``\  of all \ ``Cloudlets``\  running inside the \ ``Vm``\  as \ :java:ref:`PERCENTAGE <Unit.PERCENTAGE>`\  causes these \ ``Cloudlets``\  to automatically increase/decrease their resource usage when the \ ``Vm``\  resource is vertically scaled. This is not a CloudSim Plus issue, but the natural and maybe surprising effect that may trap researchers trying to implement and assess VM scaling policies.

   Consider the following example: a \ ``VerticalVmScaling``\  is attached to a \ ``Vm``\  to double its \ :java:ref:`Ram`\  when its usage reaches 50%. The \ ``Vm``\  has 10GB of RAM. All \ ``Cloudlets``\  running inside this \ ``Vm``\  have a \ :java:ref:`UtilizationModel`\  for their RAM utilization define in \ :java:ref:`PERCENTAGE <Unit.PERCENTAGE>`\ . When the RAM utilization of all these \ ``Cloudlets``\  reach the 50% (5GB), the \ ``Vm``\  \ :java:ref:`Ram`\  will be doubled. However, as the RAM usage of the running \ ``Cloudlets``\  is defined in percentage, they will continue to use 50% of \ ``Vm``\ 's RAM, that now represents 10GB from the 20GB capacity. This way, the vertical scaling will have no real benefit.

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  VerticalVmScaling NULL
   :outertype: VerticalVmScaling

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`VerticalVmScaling`\  objects.

Methods
-------
getAllocatedResource
^^^^^^^^^^^^^^^^^^^^

.. java:method::  long getAllocatedResource()
   :outertype: VerticalVmScaling

   Gets the current amount allocated to the \ :java:ref:`resource <getResource()>`\  managed by this scaling object. It is just a shortcut to \ ``getVmResourceToScale.getAllocatedResource()``\ .

   :return: the amount of allocated resource

getLowerThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Function<Vm, Double> getLowerThresholdFunction()
   :outertype: VerticalVmScaling

   Gets a \ :java:ref:`Function`\  that defines the lower utilization threshold for a \ :java:ref:`Vm <getVm()>`\  which indicates if it is underloaded or not. If it is underloaded, the Vm's \ :java:ref:`DatacenterBroker`\  will request to down scale the VM. The down scaling is performed by decreasing the amount of the \ :java:ref:`resource <getResourceClass()>`\  the scaling is associated to.

   This function must receive a \ :java:ref:`Vm`\  and return the lower utilization threshold for it as a percentage value between 0 and 1 (where 1 is 100%). The VM will be defined as underloaded if the utilization of the \ :java:ref:`Resource`\  this scaling object is related to is lower than the value returned by the \ :java:ref:`Function`\  returned by this method.

   **See also:** :java:ref:`.setLowerThresholdFunction(Function)`

getResource
^^^^^^^^^^^

.. java:method::  Resource getResource()
   :outertype: VerticalVmScaling

   Gets the actual Vm \ :java:ref:`Resource`\  this scaling object is in charge of scaling. This resource is defined after calling the \ :java:ref:`setResourceClass(Class)`\ .

getResourceAmountToScale
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getResourceAmountToScale()
   :outertype: VerticalVmScaling

   Gets the absolute amount of the Vm resource which has to be scaled up or down, based on the \ :java:ref:`scaling factor <getScalingFactor()>`\ .

   :return: the absolute amount of the Vm resource to scale

   **See also:** :java:ref:`.getResourceClass()`

getResourceClass
^^^^^^^^^^^^^^^^

.. java:method::  Class<? extends ResourceManageable> getResourceClass()
   :outertype: VerticalVmScaling

   Gets the class of Vm resource this scaling object will request up or down scaling. Such a class can be \ :java:ref:`Ram`\ .class, \ :java:ref:`Bandwidth`\ .class or \ :java:ref:`Pe`\ .class.

   **See also:** :java:ref:`.getResource()`

getResourceUsageThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Function<Vm, Double> getResourceUsageThresholdFunction()
   :outertype: VerticalVmScaling

   Gets the lower or upper resource utilization threshold \ :java:ref:`Function`\ , depending if the Vm resource is under or overloaded, respectively.

   :return: the lower resource utilization threshold function if the Vm resource is underloaded, upper resource utilization threshold function if the Vm resource is overloaded, or a function that always returns 0 if the Vm isn't in any of these conditions.

   **See also:** :java:ref:`.getLowerThresholdFunction()`, :java:ref:`.getUpperThresholdFunction()`

getScalingFactor
^^^^^^^^^^^^^^^^

.. java:method::  double getScalingFactor()
   :outertype: VerticalVmScaling

   Gets the factor that will be used to scale a Vm resource up or down, whether such a resource is over or underloaded, according to the defined predicates.

   If the resource to scale is a \ :java:ref:`Pe`\ , this is the number of PEs to request adding or removing when the VM is over or underloaded, respectively. For any other kind of resource, this is a percentage value in scale from 0 to 1. Every time the VM needs to be scaled up or down, this factor will be applied to increase or reduce a specific VM allocated resource.

   :return: the scaling factor to set which may be an absolute value (for \ :java:ref:`Pe`\  scaling) or percentage (for scaling other resources)

   **See also:** :java:ref:`.getUpperThresholdFunction()`

getUpperThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Function<Vm, Double> getUpperThresholdFunction()
   :outertype: VerticalVmScaling

   Gets a \ :java:ref:`Function`\  that defines the upper utilization threshold for a \ :java:ref:`Vm <getVm()>`\  which indicates if it is overloaded or not. If it is overloaded, the Vm's \ :java:ref:`DatacenterBroker`\  will request to up scale the VM. The up scaling is performed by increasing the amount of the \ :java:ref:`resource <getResourceClass()>`\  the scaling is associated to.

   This function must receive a \ :java:ref:`Vm`\  and return the upper utilization threshold for it as a percentage value between 0 and 1 (where 1 is 100%). The VM will be defined as overloaded if the utilization of the \ :java:ref:`Resource`\  this scaling object is related to is higher than the value returned by the \ :java:ref:`Function`\  returned by this method.

   **See also:** :java:ref:`.setUpperThresholdFunction(Function)`

isVmOverloaded
^^^^^^^^^^^^^^

.. java:method::  boolean isVmOverloaded()
   :outertype: VerticalVmScaling

   Checks if the Vm is overloaded or not, based on the \ :java:ref:`getUpperThresholdFunction()`\ .

   :return: true if the Vm is overloaded, false otherwise

isVmUnderloaded
^^^^^^^^^^^^^^^

.. java:method::  boolean isVmUnderloaded()
   :outertype: VerticalVmScaling

   Checks if the Vm is underloaded or not, based on the \ :java:ref:`getLowerThresholdFunction()`\ .

   :return: true if the Vm is underloaded, false otherwise

requestUpScalingIfPredicateMatches
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override  boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt)
   :outertype: VerticalVmScaling

   Performs the vertical scale if the Vm is overloaded, according to the \ :java:ref:`getUpperThresholdFunction()`\  predicate, increasing the Vm resource to which the scaling object is linked to (that may be RAM, CPU, BW, etc), by the factor defined a scaling factor.

   The time interval in which it will be checked if the Vm is overloaded depends on the \ :java:ref:`Datacenter.getSchedulingInterval()`\  value. Make sure to set such a value to enable the periodic overload verification.

   :param evt: current simulation time

   **See also:** :java:ref:`.getScalingFactor()`

setLowerThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction)
   :outertype: VerticalVmScaling

   Sets a \ :java:ref:`Function`\  that defines the lower utilization threshold for a \ :java:ref:`Vm <getVm()>`\  which indicates if it is underloaded or not. If it is underloaded, the Vm's \ :java:ref:`DatacenterBroker`\  will request to down scale the VM. The down scaling is performed by decreasing the amount of the \ :java:ref:`resource <getResourceClass()>`\  the scaling is associated to.

   This function must receive a \ :java:ref:`Vm`\  and return the lower utilization threshold for it as a percentage value between 0 and 1 (where 1 is 100%).

   By setting the lower threshold as a \ :java:ref:`Function`\  instead of a directly storing a \ :java:ref:`Double`\  value which represent the threshold, it is possible to define the threshold dynamically instead of using a static value. Furthermore, the threshold function can be reused for scaling objects of different VMs.

   :param lowerThresholdFunction: the lower utilization threshold function to set. The VM will be defined as underloaded if the utilization of the \ :java:ref:`Resource`\  this scaling object is related to is lower than the value returned by this \ :java:ref:`Function`\ .

setResourceClass
^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling setResourceClass(Class<? extends ResourceManageable> resourceClass)
   :outertype: VerticalVmScaling

   Sets the class of Vm resource that this scaling object will request up or down scaling. Such a class can be \ :java:ref:`Ram`\ .class, \ :java:ref:`Bandwidth`\ .class or \ :java:ref:`Pe`\ .class.

   :param resourceClass: the resource class to set

setResourceScaling
^^^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling setResourceScaling(ResourceScaling resourceScaling)
   :outertype: VerticalVmScaling

   Sets the \ :java:ref:`ResourceScaling`\  that defines how the resource has to be resized.

   :param resourceScaling: the \ :java:ref:`ResourceScaling`\  to set

setScalingFactor
^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling setScalingFactor(double scalingFactor)
   :outertype: VerticalVmScaling

   Sets the factor that will be used to scale a Vm resource up or down, whether such a resource is over or underloaded, according to the defined predicates.

   If the resource to scale is a \ :java:ref:`Pe`\ , this is the number of PEs to request adding or removing when the VM is over or underloaded, respectively. For any other kind of resource, this is a percentage value in scale from 0 to 1. Every time the VM needs to be scaled up or down, this factor will be applied to increase or reduce a specific VM allocated resource.

   :param scalingFactor: the scaling factor to set which may be an absolute value (for \ :java:ref:`Pe`\  scaling) or percentage (for scaling other resources)

   **See also:** :java:ref:`.getUpperThresholdFunction()`

setUpperThresholdFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction)
   :outertype: VerticalVmScaling

   Sets a \ :java:ref:`Function`\  that defines the upper utilization threshold for a \ :java:ref:`Vm <getVm()>`\  which indicates if it is overloaded or not. If it is overloaded, the Vm's \ :java:ref:`DatacenterBroker`\  will request to up scale the VM. The up scaling is performed by increasing the amount of the \ :java:ref:`resource <getResourceClass()>`\  the scaling is associated to.

   This function must receive a \ :java:ref:`Vm`\  and return the upper utilization threshold for it as a percentage value between 0 and 1 (where 1 is 100%).

   By setting the upper threshold as a \ :java:ref:`Function`\  instead of a directly storing a \ :java:ref:`Double`\  value which represent the threshold, it is possible to define the threshold dynamically instead of using a static value. Furthermore, the threshold function can be reused for scaling objects of different VMs.

   :param upperThresholdFunction: the upper utilization threshold function to set. The VM will be defined as overloaded if the utilization of the \ :java:ref:`Resource`\  this scaling object is related to is higher than the value returned by this \ :java:ref:`Function`\ .

