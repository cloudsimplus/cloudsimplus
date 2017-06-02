package org.cloudsimplus.sla.readJsonFile.slaMetricsJsonFile;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by raysaoliveira on 01/06/17.
 */
public class CostPrice {
    private SlaReader reader;
    private double maxValueCostPrice;

    public CostPrice(SlaReader reader) {
        this.reader = reader;
    }

    public void checkCostPriceSlaContract() throws FileNotFoundException {
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
            .filter(m -> m.isCostPrice())
            .findFirst()
            .ifPresent(this::costPriceThreshold);
    }

    private void costPriceThreshold(SlaMetric metric) {
        double maxValue =
            metric.getDimensions().stream()
                .filter(d -> d.isValueMax())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MAX_VALUE);

        maxValueCostPrice = maxValue;
    }

    /**
     * @return the maxValueCostPrice
     */
    public double getMaxValueCostPrice() {
        return maxValueCostPrice;
    }

    /**
     * @param maxValueCostPrice the maxValueCostPrice to set
     */
    public void setMaxValueCostPrice(double maxValueCostPrice) {
        this.maxValueCostPrice = maxValueCostPrice;
    }
}
