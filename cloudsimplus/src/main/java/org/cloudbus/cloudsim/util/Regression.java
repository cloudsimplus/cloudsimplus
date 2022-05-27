package org.cloudbus.cloudsim.util;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides utility methods to compute regressions.
 * @since CloudSim Plus 7.0.1
 * @see <a href="https://en.wikipedia.org/wiki/Regression_analysis">Regression Analysis</a>
 */
public final class Regression {

    /** A private constructor to avoid class instantiation. */
    private Regression(){/**/}

    /**
     * Creates a simple linear regression.
     * @param x the independent variable
     * @param y the dependent variable
     * @return
     */
    public static SimpleRegression newLinearRegression(final double[] x, final double[] y) {
        final SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < x.length; i++) {
            regression.addData(x[i], y[i]);
        }
        return regression;
    }

    /**
     * Creates a a multiple linear regression.
     * @param x the independent variable
     * @param y the dependent variable
     * @return
     */
    public static OLSMultipleLinearRegression newLinearRegression(final double[][] x, final double[] y) {
        final OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(y, x);
        return regression;
    }

    /**
     * Creates a weighted linear regression.
     * @param x the independent variable
     * @param y the dependent variable
     * @param weights the weights to apply to x and y
     * @return
     */
    public static SimpleRegression newWeightedLinearRegression(
        final double[] x, final double[] y, final double[] weights)
    {
        final double[] weightedX = new double[x.length];
        final double[] weightedY = new double[y.length];

        final long numZeroWeights = Arrays.stream(weights).filter(weight -> weight <= 0).count();

        for (int i = 0; i < x.length; i++) {
            if (numZeroWeights >= 0.4 * weights.length) {
                // See: http://www.ncsu.edu/crsc/events/ugw07/Presentations/Crooks_Qiao/Crooks_Qiao_Alt_Presentation.pdf
                weightedX[i] = Math.sqrt(weights[i]) * x[i];
                weightedY[i] = Math.sqrt(weights[i]) * y[i];
            } else {
                weightedX[i] = x[i];
                weightedY[i] = y[i];
            }
        }

        return newLinearRegression(weightedX, weightedY);
    }

    /**
     * Gets the Local Regression (LOESS) parameter estimates.
     *
     * @param y the dependent variable
     * @return the Loess parameter estimates
     * @see <a href="https://www.itl.nist.gov/div898/handbook/pmd/section1/pmd144.htm">LOESS</a>
     */
    public static double[] getLoessParameterEstimates(final double... y) {
        final double[] x = createIndependentArray(y.length);
        return newWeightedLinearRegression(x, y, getTricubeWeights(y.length))
                               .regress().getParameterEstimates();
    }

    /**
     * Gets the robust LOESS parameter estimates.
     *
     * @param y the dependent variable
     * @return the robust loess parameter estimates
     */
    public static double[] getRobustLoessParameterEstimates(final double... y) {
        final double[] x = createIndependentArray(y.length);
        final SimpleRegression tricubeRegression =
                newWeightedLinearRegression(x, y, getTricubeWeights(y.length));
        final double[] residuals = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            residuals[i] = y[i] - tricubeRegression.predict(x[i]);
        }
        final SimpleRegression tricubeBySqrRegression =
                newWeightedLinearRegression(x, y, getTricubeBisquareWeights(residuals));

        final double[] estimates = tricubeBySqrRegression.regress().getParameterEstimates();
        if (Double.isNaN(estimates[0]) || Double.isNaN(estimates[1])) {
            return tricubeRegression.regress().getParameterEstimates();
        }
        return estimates;
    }

    /**
     * Gets the tricube weigths.
     *
     * @param weightsNumber the number of weights
     * @return an array of tricube weigths with n elements
     */
    private static double[] getTricubeWeights(final int weightsNumber) {
        final double[] weights = new double[weightsNumber];
        final double top = weightsNumber - 1; //spread
        for (int i = 2; i < weightsNumber; i++) {
            final double k = Math.pow(1 - Math.pow((top - i) / top, 3), 3);
            weights[i] = k > 0 ? 1 / k : Double.MAX_VALUE;
        }

        weights[0] = weights[1] = weights[2];
        return weights;
    }

    /**
     * Gets the tricube bisquare weigths.
     *
     * @param residuals the residuals array
     * @return the tricube bisquare weigths
     */
    private static double[] getTricubeBisquareWeights(final double... residuals) {
        final double[] weights = getTricubeWeights(residuals.length);
        final double[] weights2 = new double[residuals.length];
        final double s6 = MathUtil.median(MathUtil.abs(residuals)) * 6;
        for (int i = 2; i < residuals.length; i++) {
            final double k = Math.pow(1 - Math.pow(residuals[i] / s6, 2), 2);
            weights2[i] = k > 0 ? (1 / k) * weights[i] : Double.MAX_VALUE;
        }

        weights2[0] = weights2[1] = weights2[2];
        return weights2;
    }

    /**
     * Computes correlation coefficients for a set of data.
     *
     * @param data the data to compute the correlation coefficients
     * @return the correlation coefficients
     */
    public static List<Double> correlationCoefficients(final double[][] data) {
        final int rows = data.length;
        final int cols = data[0].length;
        final List<Double> correlationCoefficients = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            final double[][] x = new double[rows - 1][cols];
            int k = 0;
            for (int j = 0; j < rows; j++) {
                if (j != i) {
                    x[k++] = data[j];
                }
            }

            // Transpose the matrix so that it fits the linear model
            final double[][] xT = new Array2DRowRealMatrix(x).transpose().getData();

            // RSquare is the "coefficient of determination"
            correlationCoefficients.add(newLinearRegression(xT, data[i]).calculateRSquared());
        }

        return correlationCoefficients;
    }

    /**
     * Creates an array representing the independent variable for
     * computing a linear regression.
     *
     * @param length the length of the array to create
     * @return
     */
    private static double[] createIndependentArray(final int length) {
        final double[] x = new double[length];
        for (int i = 0; i < length; i++) {
            x[i] = i + 1;
        }

        return x;
    }
}
