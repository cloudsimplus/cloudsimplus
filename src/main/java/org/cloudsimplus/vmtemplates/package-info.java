/// Provides template classes which enable reading VM configurations
/// from a JSON file, representing actual types of VMs available in
/// real Cloud Providers such as [Amazon Web Services (AWS)](http://aws.amazon.com).
///
/// A JSON file can be read using a method such as the
/// [org.cloudsimplus.vmtemplates.AwsEc2Template#getInstance(java.lang.String)].
/// Then, one can call the usual [org.cloudsimplus.vms.Vm] constructors
/// to create an actual VM, using the configurations from the template file.
///
/// @author raysaoliveira
/// @since CloudSim Plus 1.2.2
/// @see org.cloudsimplus.vmtemplates.AwsEc2Template
package org.cloudsimplus.vmtemplates;
