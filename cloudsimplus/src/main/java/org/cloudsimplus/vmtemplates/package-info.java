/**
 * Provides template classes which enable reading VM configurations
 * from a JSON file, representing actual types of VMs available in
 * real Cloud Providers such as <a href="http://aws.amazon.com">Amazon Web Services (AWS)</a>.
 *
 * <p>A JSON file can be read using a method such as the
 * {@link org.cloudsimplus.vmtemplates.AwsEc2Template#getInstance(java.lang.String)}.
 * Then, one can call the usual {@link org.cloudbus.cloudsim.vms.Vm} constructors
 * to create an actual VM, using the configurations from the template file.
 * </p>
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.2
 * @see org.cloudsimplus.vmtemplates.AwsEc2Template
 */
package org.cloudsimplus.vmtemplates;
