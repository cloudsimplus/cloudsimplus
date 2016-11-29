package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used to help setting up {@link CloudletScheduler} objects to be used
 * by tests.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class CloudletSchedulerUtil {

    public static List<Double> createMipsList(int pesNumber, double mips) {
        List<Double> mipsList = new ArrayList<>();
        for (int i = 0; i < pesNumber; i++) {
            mipsList.add(mips);
        }
        return mipsList;
    }

    /**
     * Creates a mips list with just one element.
     * @param mips the mips value to be added to the list
     * @return the create unitary mips list
     */
    public static List<Double> createUnitaryMipsList(double mips) {
        return createMipsList(1, mips);
    }

}
