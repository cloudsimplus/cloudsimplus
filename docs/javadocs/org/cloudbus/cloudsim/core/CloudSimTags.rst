.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.core.events SimEvent

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.autoscaling VerticalVmScaling

CloudSimTags
============

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public final class CloudSimTags

   Contains various static command tags that indicate a type of action that needs to be undertaken by CloudSim entities when they receive or send events. \ **NOTE:**\  To avoid conflicts with other tags, CloudSim reserves numbers lower than 300 and the number 9600.

   :author: Manzur Murshed, Rajkumar Buyya, Anthony Sulistio

Fields
------
CLOUDLET_CANCEL
^^^^^^^^^^^^^^^

.. java:field:: public static final int CLOUDLET_CANCEL
   :outertype: CloudSimTags

   Cancels a Cloudlet submitted in the Datacenter entity. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Cloudlet`\  object.

CLOUDLET_PAUSE
^^^^^^^^^^^^^^

.. java:field:: public static final int CLOUDLET_PAUSE
   :outertype: CloudSimTags

   Pauses a Cloudlet submitted in the Datacenter entity. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Cloudlet`\  object.

CLOUDLET_PAUSE_ACK
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int CLOUDLET_PAUSE_ACK
   :outertype: CloudSimTags

   Pauses a Cloudlet submitted in the Datacenter entity with an acknowledgement. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Cloudlet`\  object.

CLOUDLET_RESUME
^^^^^^^^^^^^^^^

.. java:field:: public static final int CLOUDLET_RESUME
   :outertype: CloudSimTags

   Resumes a Cloudlet submitted in the Datacenter entity. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Cloudlet`\  object.

CLOUDLET_RESUME_ACK
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int CLOUDLET_RESUME_ACK
   :outertype: CloudSimTags

   Resumes a Cloudlet submitted in the Datacenter entity with an acknowledgement. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Cloudlet`\  object.

CLOUDLET_RETURN
^^^^^^^^^^^^^^^

.. java:field:: public static final int CLOUDLET_RETURN
   :outertype: CloudSimTags

   Denotes the return of a finished Cloudlet back to the sender. This tag is normally used by Datacenter entity. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Cloudlet`\  object.

CLOUDLET_SUBMIT
^^^^^^^^^^^^^^^

.. java:field:: public static final int CLOUDLET_SUBMIT
   :outertype: CloudSimTags

   Denotes the submission of a Cloudlet. This tag is normally used between CloudSim User and Datacenter entity. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Cloudlet`\  object.

CLOUDLET_SUBMIT_ACK
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int CLOUDLET_SUBMIT_ACK
   :outertype: CloudSimTags

   Denotes the submission of a Cloudlet with an acknowledgement. This tag is normally used between CloudSim User and Datacenter entity. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Cloudlet`\  object.

DATACENTER_LIST_REQUEST
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int DATACENTER_LIST_REQUEST
   :outertype: CloudSimTags

   Denotes a request from a broker to a \ :java:ref:`CloudInformationService`\  to get the list of all Datacenters, including the ones that can support advanced reservation.

DATACENTER_REGISTRATION_REQUEST
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int DATACENTER_REGISTRATION_REQUEST
   :outertype: CloudSimTags

   Denotes a request from a Datacenter to register itself. This tag is normally used between \ :java:ref:`CloudInformationService`\  and Datacenter entities. When such a \ :java:ref:`SimEvent`\  is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Datacenter`\  object.

END_OF_SIMULATION
^^^^^^^^^^^^^^^^^

.. java:field:: public static final int END_OF_SIMULATION
   :outertype: CloudSimTags

   Denotes the end of simulation.

FAILURE
^^^^^^^

.. java:field:: public static final int FAILURE
   :outertype: CloudSimTags

   Defines the base tag to be used for failure events such as failure of hosts or VMs.

HOST_FAILURE
^^^^^^^^^^^^

.. java:field:: public static final int HOST_FAILURE
   :outertype: CloudSimTags

   Defines the tag that represents a request to generate a host failure.

ICMP_PKT_RETURN
^^^^^^^^^^^^^^^

.. java:field:: public static final int ICMP_PKT_RETURN
   :outertype: CloudSimTags

   This tag is used to return the ping request back to sender.

ICMP_PKT_SUBMIT
^^^^^^^^^^^^^^^

.. java:field:: public static final int ICMP_PKT_SUBMIT
   :outertype: CloudSimTags

   This tag is used by an entity to send ping requests.

NETWORK_EVENT_DOWN
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int NETWORK_EVENT_DOWN
   :outertype: CloudSimTags

NETWORK_EVENT_HOST
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int NETWORK_EVENT_HOST
   :outertype: CloudSimTags

NETWORK_EVENT_SEND
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int NETWORK_EVENT_SEND
   :outertype: CloudSimTags

NETWORK_EVENT_UP
^^^^^^^^^^^^^^^^

.. java:field:: public static final int NETWORK_EVENT_UP
   :outertype: CloudSimTags

NETWORK_HOST_REGISTER
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int NETWORK_HOST_REGISTER
   :outertype: CloudSimTags

REGISTER_REGIONAL_CIS
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int REGISTER_REGIONAL_CIS
   :outertype: CloudSimTags

   Denotes a request to register a \ :java:ref:`CloudInformationService`\  entity as a regional CIS. When such a \ :java:ref:`SimEvent`\  is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`CloudInformationService`\  object.

REQUEST_REGIONAL_CIS
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int REQUEST_REGIONAL_CIS
   :outertype: CloudSimTags

   Denotes a request to get a list of other regional CIS entities from the system CIS entity.

VM_CREATE
^^^^^^^^^

.. java:field:: public static final int VM_CREATE
   :outertype: CloudSimTags

   Denotes a request to create a new VM in a \ :java:ref:`Datacenter`\  without requiring and acknowledgement to be sent back to the sender.

VM_CREATE_ACK
^^^^^^^^^^^^^

.. java:field:: public static final int VM_CREATE_ACK
   :outertype: CloudSimTags

   Denotes a request to create a new VM in a \ :java:ref:`Datacenter`\  with acknowledgement information sent by the Datacenter, where the \ :java:ref:`SimEvent.getData()`\  of the reply event is a \ :java:ref:`Vm`\  object. To check if the VM was in fact created inside the requested Datacenter one has only to call \ :java:ref:`Vm.isCreated()`\ .

VM_DESTROY
^^^^^^^^^^

.. java:field:: public static final int VM_DESTROY
   :outertype: CloudSimTags

   Denotes a request to destroy a VM in a \ :java:ref:`Datacenter`\ . When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Vm`\  object.

VM_DESTROY_ACK
^^^^^^^^^^^^^^

.. java:field:: public static final int VM_DESTROY_ACK
   :outertype: CloudSimTags

   Denotes a request to destroy a new VM in a \ :java:ref:`Datacenter`\  with acknowledgement information sent by the Datacener. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`Vm`\  object.

VM_MIGRATE
^^^^^^^^^^

.. java:field:: public static final int VM_MIGRATE
   :outertype: CloudSimTags

   Denotes a request to migrate a new VM in a \ :java:ref:`Datacenter`\ . When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ ``Map.Entry<Vm, Host>``\  representing to which Host a VM must be migrated.

VM_MIGRATE_ACK
^^^^^^^^^^^^^^

.. java:field:: public static final int VM_MIGRATE_ACK
   :outertype: CloudSimTags

   Denotes a request to migrate a new VM in a \ :java:ref:`Datacenter`\  with acknowledgement information sent by the Datacenter. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ ``Map.Entry<Vm, Host>``\  representing to which Host a VM must be migrated.

VM_UPDATE_CLOUDLET_PROCESSING_EVENT
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int VM_UPDATE_CLOUDLET_PROCESSING_EVENT
   :outertype: CloudSimTags

   Denotes an internal event generated in a \ :java:ref:`Datacenter`\  to notify itself to update the processing of cloudlets. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  can be a \ :java:ref:`Host`\  object to indicate that just the Cloudlets running in VMs inside such a Host must be updated. The Host is an optional parameter which if omitted, means that all Hosts from the Datacenter will have its cloudlets updated.

VM_VERTICAL_SCALING
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int VM_VERTICAL_SCALING
   :outertype: CloudSimTags

   Defines the tag to be used to request vertical scaling of VM resources such as Ram, Bandwidth or Pe. When an event of this type is sent, the \ :java:ref:`SimEvent.getData()`\  must be a \ :java:ref:`VerticalVmScaling`\  object.

