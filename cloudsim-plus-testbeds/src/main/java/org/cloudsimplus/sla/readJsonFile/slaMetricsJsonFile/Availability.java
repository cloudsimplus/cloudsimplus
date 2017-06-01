package org.cloudsimplus.sla.readJsonFile.slaMetricsJsonFile;

import java.io.FileNotFoundException;
import java.util.List;

/**
 *
 * This class takes the availability metric threshold in the sla contract.
 *
 * Created by raysaoliveira on 30/05/17.
 */
public class Availability {
    private SlaReader reader;
    private double minValueAvailability;
    private double maxValueAvailability;

    public Availability(SlaReader reader) {
        this.reader = reader;
    }

    public void checkAvailabilitySlaContract() throws FileNotFoundException {
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
            .filter(m -> m.isAvailability())
            .findFirst()
            .ifPresent(this::availabilityThreshold);

    }

    private void availabilityThreshold(SlaMetric metric) {
        double minValue =
            metric.getDimensions().stream()
                .filter(d -> d.isValueMin())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MIN_VALUE);
        double maxValue =
            metric.getDimensions().stream()
                .filter(d -> d.isValueMax())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MAX_VALUE);

        minValueAvailability = minValue;
        maxValueAvailability = maxValue;
    }

    /**
     * @return the minValueAvailability
     */
    public double getMinValueAvailability() {
        return minValueAvailability;
    }

    /**
     * @param minValueAvailability the minValueAvailability to set
     */
    public void setMinValueAvailability(double minValueAvailability) {
        this.minValueAvailability = minValueAvailability;
    }

    /**
     * @return the maxValueAvailability
     */
    public double getMaxValueAvailability() {
        return maxValueAvailability;
    }

    /**
     * @param maxValueAvailability the maxValueAvailability to set
     */
    public void setMaxValueAvailability(double maxValueAvailability) {
        this.maxValueAvailability = maxValueAvailability;
    }
}
