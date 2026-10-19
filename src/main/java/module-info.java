module cloudsimplus {
    requires ch.qos.logback.classic;
    requires com.google.gson;
    requires transitive org.apache.commons.rng.api;
    requires org.apache.commons.rng.simple;
    requires transitive org.apache.commons.statistics.descriptive;
    requires transitive org.apache.commons.statistics.distribution;
    requires static lombok;
    requires org.apache.commons.collections4;
    requires org.apache.commons.lang3;
    requires static transitive org.jspecify;
    requires org.slf4j;
}
