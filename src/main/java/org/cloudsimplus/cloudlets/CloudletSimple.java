/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets;

import lombok.NonNull;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.vms.Vm;

/// Cloudlet implements the basic features of an application/job/task to be executed
/// by a [Vm] on behalf of a given user.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
/// @see DatacenterBroker
public class CloudletSimple extends CloudletAbstract {
    /// Creates a Cloudlet with no priority or id.
    /// The id is defined when the Cloudlet is submitted to
    /// a [DatacenterBroker]. The file size and output size are defined as 1.
    ///
    /// @param length the length (in MI) of this cloudlet to be executed in a VM
    /// (check out [#setLength(long)])
    /// @param pesNumber the number of [Pe]s the Cloudlet will require
    /// @param utilizationModel a [UtilizationModel] to define how the Cloudlet uses CPU, RAM and BW.
    /// To define an independent utilization model for each resource,
    /// call the respective setters.
    ///
    /// @see #setUtilizationModelCpu(UtilizationModel)
    /// @see #setUtilizationModelRam(UtilizationModel)
    /// @see #setUtilizationModelBw(UtilizationModel)
    public CloudletSimple(final long length, final int pesNumber, final UtilizationModel utilizationModel) {
        super(length, pesNumber, utilizationModel);
    }

    /// Creates a Cloudlet with no priority or id.
    /// The id is defined when the Cloudlet is submitted to
    /// a [DatacenterBroker]. The file size and output size are defined as 1.
    ///
    /// **NOTE:** By default, the Cloudlet will use a [UtilizationModel#FULL] to define
    /// CPU utilization and a [UtilizationModel#NULL] for RAM and BW.
    /// To change the default values, use the respective setters.
    ///
    /// @param length the length (in MI) of this cloudlet to be executed in a VM
    /// (check out [#setLength(long)])
    /// @param pesNumber the number of [Pe]s the Cloudlet will require
    /// @see #setUtilizationModelRam(UtilizationModel)
    /// @see #setUtilizationModelBw(UtilizationModel)
    public CloudletSimple(final long length, final int pesNumber) {
        super(length, pesNumber);
    }

    /// Creates a Cloudlet with no priority or id.
    /// The id is defined when the Cloudlet is submitted to
    /// a [DatacenterBroker]. The file size and output size are defined as 1.
    ///
    /// **NOTE:** By default, the Cloudlet will use a [UtilizationModel#FULL] to define
    /// CPU utilization and a [UtilizationModel#NULL] for RAM and BW.
    /// To change the default values, use the respective setters.
    ///
    /// @param length the length (in MI) of this cloudlet to be executed in a VM
    /// (check out [#setLength(long)])
    /// @param pesNumber the number of [Pe]s the Cloudlet will require
    /// @see #setUtilizationModelRam(UtilizationModel)
    /// @see #setUtilizationModelBw(UtilizationModel)
    public CloudletSimple(final long length, final long pesNumber) {
        super(length, pesNumber);
    }

    /// Creates a Cloudlet with no priority, file size and output size equal to 1.
    ///
    /// **NOTE:** By default, the Cloudlet will use a [UtilizationModel#FULL] to define
    /// CPU utilization and a [UtilizationModel#NULL] for RAM and BW.
    /// To change the default values, use the respective setters.
    ///
    /// @param id  the unique ID of this cloudlet
    /// @param length the length (in MI) of this cloudlet to be executed in a VM
    /// (check out [#setLength(long)])
    /// @param pesNumber the number of [Pe]s the Cloudlet will require
    /// @see #setUtilizationModelRam(UtilizationModel)
    /// @see #setUtilizationModelBw(UtilizationModel)
    public CloudletSimple(final long id, final long length, final long pesNumber) {
        super(id, length, pesNumber);
    }

    @Override
    public String toString() {
        return "Cloudlet %d".formatted(getId());
    }

    /// Compare this Cloudlet with another one based on [#getLength()].
    ///
    /// @param other the Cloudlet to compare to
    /// @return {@inheritDoc}
    @Override
    public int compareTo(@NonNull final Cloudlet other) {
        if(this.equals(other)) {
            return 0;
        }

        return Long.compare(getLength(), other.getLength()) +
            Long.compare(this.getId(), other.getId()) +
            this.getBroker().compareTo(other.getBroker());
    }
}
