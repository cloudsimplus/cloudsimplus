package org.cloudsimplus.sla.readJsonFile;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class read the sla agreements in json format.
 * 
 * The sla agreements is in the {@link SlaMetric}. This class 
 * contains the name of the metric, the minimum and maximum 
 * acceptable value, and the metric unit. 
 * The minimum and maximum values will be used to check 
 * the violation of the metric. If the simulation metric is
 * not within these limits, it is violated and actions taken.
 * 
 *
 * @author raysaoliveira
 */
public class SlaReader {
    
    public static final String RESPONSE_TIME_FIELD = "responseTime";
    public static final String CPU_UTILIZATION_FIELD = "cpuUtilization";
    public static final String WAIT_TIME_FIELD = "waitTime";
    
    private List<SlaMetric> metrics;

    public SlaReader(String slaFileName) throws FileNotFoundException{
        Gson gson = new Gson();
        SlaMetric[] array = gson.fromJson(new FileReader(slaFileName), SlaMetric[].class);
        metrics = Arrays.asList(array);
    }
    
    /**
     * Gets a read-only list of metrics that were read from the 
     * SLA contract file.
     * @return 
     */
    public List<SlaMetric> getMetrics() {
        return Collections.unmodifiableList(metrics);
    }

}
