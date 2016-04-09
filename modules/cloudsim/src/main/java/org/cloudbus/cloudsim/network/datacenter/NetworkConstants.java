/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

/**
 * Network constants
 *
 * @todo This class uses several hard-coded values that appears to be used only
 * for examples. If yes, it should be moved to the examples package. The
 * exceptions are the {@link TaskStage} types and number of switches by level.
 */
public class NetworkConstants {
        
    public static final int PES_NUMBER = 4;
    public static final int FILE_SIZE = 300;
    public static final int OUTPUT_SIZE = 300;

    public static final int COMMUNICATION_LENGTH = 1;

    public static double seed = 199;
    public static boolean logFlag = false;
    public static boolean autoCreateVmsInNetDatacenterBroker = true;

    public static int maxVmsPerHost = 2;
    public static int hostPEs = 8;
    public static double maxMemPerVM = 1024 * 1024;// kb
    
    public static int currentCloudletId = 0;
    public static int currentAppId = 0;
    public static int iteration = 10;
    public static int nextTime = 1000;
    public static int totalDataTransfer = 0;
}
