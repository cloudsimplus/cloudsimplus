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
    /**
     * Types of {@link TaskStage}
     *
     * @todo should be an enum
     */
    public static final int EXECUTION = 0;
    public static final int WAIT_SEND = 1;
    public static final int WAIT_RECV = 2;
    public static final int FINISH = -2;

    /**
     * Number of switches at root level.
     */
    public static final int ROOT_SWITCHES_NUMBER = 0;
    
    /**
     * Number of switches at aggregation level.
     */
    public static final int AGGREGATION_SWITCHES_NUMBER = 1;
    
    /**
     * Number of switches at edge level.
     */
    public static final int EDGE_SWITCHES_NUMBER = 2;

    public static final int PES_NUMBER = 4;
    public static final int FILE_SIZE = 300;
    public static final int OUTPUT_SIZE = 300;

    public static final int COMMUNICATION_LENGTH = 1;

    /**
     * The downlink bandwidth of {@link RootSwitch} in Megabits/s.
     * It also represents the uplink bandwidth of connected aggregation switches.
     */
    public static long RootSwitchDownlinkBW  =  40 * 1024 * 1024 * 1024; // 40000 Megabits (40 Gigabits)

    /**
     * The downlink bandwidth of {@link AggregateSwitch} in Megabits/s.
     * It also represents the uplink bandwidth of connected edge switches.
     */
    public static long AggregationSwitchDownlinkBW  = 100 * 1024 * 1024; // 100 Megabits
    
    /**
     * The downlink bandwidth of {@link EdgeSwitch} in Megabits/s.
     * It also represents the uplink bandwidth of connected hosts.
     */
    public static long EdgeSwitchDownlinkBW = 100 * 1024 * 1024;

    /**
     * The delay of {@link RootSwitch} in milliseconds.
     */
    public static double RootSwitchDelay = .00285;
    
    /**
     * The delay of {@link AggregateSwitch} in milliseconds.
     */
    public static double AggregationSwitchDelay  = .00245;
    
    /**
     * The delay of {@link EdgeSwitch} in milliseconds.
     */
    public static double EdgeSwitchDelay = .00157; 

    /**
     * Number of root switch ports that defines the number of
     * {@link AggregateSwitch} that can be connected to it.
     */
    public static int RootSwitchPorts = 1;

    /**
     * Number of aggregation switch ports that defines the number of
     * {@link EdgeSwitch} that can be connected to it.
     */
    public static int AggregationSwitchPorts = 1;

    /**
     * Number of edge switch ports that defines the number of
     * {@link org.cloudbus.cloudsim.Host} that can be connected to it.
     */
    public static int EdgeSwitchPorts = 4;

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
