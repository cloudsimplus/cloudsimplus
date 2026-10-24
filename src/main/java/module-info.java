/// CloudSim Plus: a framework for modeling and simulation of cloud computing infrastructures and services.
module cloudsimplus {
    /* Modules whose types are part of the CloudSim Plus public API
     * (for instance, in method parameters or return types).
     * They are "transitive", so that projects using CloudSim Plus can use such types
     * without requiring the modules themselves. */
    requires transitive ch.qos.logback.classic;
    requires transitive org.apache.commons.lang3;
    requires transitive org.apache.commons.rng.api;
    requires transitive org.apache.commons.statistics.descriptive;
    requires transitive org.apache.commons.statistics.distribution;
    requires transitive org.slf4j;

    // Modules used just internally
    requires com.google.gson;
    requires org.apache.commons.collections4;
    requires org.apache.commons.rng.simple;

    // Annotations only needed at compile time
    requires static lombok;
    requires static transitive org.jspecify;

    exports org.cloudsimplus.allocationpolicies;
    exports org.cloudsimplus.allocationpolicies.migration;
    exports org.cloudsimplus.autoscaling;
    exports org.cloudsimplus.autoscaling.resources;
    exports org.cloudsimplus.brokers;
    exports org.cloudsimplus.builders;
    exports org.cloudsimplus.builders.tables;
    exports org.cloudsimplus.cloudlets;
    exports org.cloudsimplus.cloudlets.network;
    exports org.cloudsimplus.core;
    exports org.cloudsimplus.core.events;
    exports org.cloudsimplus.datacenters;
    exports org.cloudsimplus.datacenters.network;
    exports org.cloudsimplus.distributions;
    exports org.cloudsimplus.faultinjection;
    exports org.cloudsimplus.heuristics;
    exports org.cloudsimplus.hosts;
    exports org.cloudsimplus.hosts.network;
    exports org.cloudsimplus.listeners;
    exports org.cloudsimplus.network;
    exports org.cloudsimplus.network.switches;
    exports org.cloudsimplus.network.topologies;
    exports org.cloudsimplus.network.topologies.readers;
    exports org.cloudsimplus.power;
    exports org.cloudsimplus.power.models;
    exports org.cloudsimplus.provisioners;
    exports org.cloudsimplus.resources;
    exports org.cloudsimplus.schedulers.cloudlet;
    exports org.cloudsimplus.schedulers.cloudlet.network;
    exports org.cloudsimplus.schedulers.vm;
    exports org.cloudsimplus.selectionpolicies;
    exports org.cloudsimplus.slametrics;
    exports org.cloudsimplus.testbeds;
    exports org.cloudsimplus.traces;
    exports org.cloudsimplus.traces.google;
    exports org.cloudsimplus.util;
    exports org.cloudsimplus.utilizationmodels;
    exports org.cloudsimplus.vms;
    exports org.cloudsimplus.vms.network;
    exports org.cloudsimplus.vmtemplates;

    /* Gson creates objects of these packages from JSON files using reflection
     * (see SlaContract.getInstance() and AwsEc2Template.getInstance()).
     * Without opening them, that fails with an InaccessibleObjectException. */
    opens org.cloudsimplus.slametrics to com.google.gson;
    opens org.cloudsimplus.vmtemplates to com.google.gson;
}
