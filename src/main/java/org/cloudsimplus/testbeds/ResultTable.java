/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.testbeds;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.cloudsimplus.builders.tables.CsvTableColumn.alignStringRight;

/**
 * Generates LaTeX tables from experiment's results.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.0.1
 */
final class ResultTable<T extends Experiment<T>> {
    private final ExperimentRunner<T> runner;
    private final List<ConfidenceInterval> confidenceIntervals;

    /**
     * @param runner experiment runner that is collecting the results
     * @param confidenceIntervals a List of
     *                 {@link ConfidenceInterval} objects summarizing the results for different metrics
     *                 from all simulation runs.
     */
    ResultTable(final ExperimentRunner<T> runner, final List<ConfidenceInterval> confidenceIntervals){
        this.runner = runner;
        this.confidenceIntervals = confidenceIntervals;
    }

    void buildCsvResultsTable() {
        final String cols = confidenceIntervals.stream().map(ConfidenceInterval::getMetricName).collect(joining("; "));
        System.out.printf("Type of Value;%s%n", cols);

        final String format = "%.2f";
        final String values =
            confidenceIntervals
                .stream()
                .map(ci -> alignStringRight(format.formatted(ci.getValue()), ci.getMetricName().length()))
                .collect(joining("; "));
        final String valueType = runner.getSimulationRuns() > 1 ? "CI           " : "Mean         ";
        System.out.printf("%s;%s%n", valueType, values);

        final String errorMargins =
            confidenceIntervals
                .stream()
                .map(ci -> alignStringRight(format.formatted(ci.getErrorMargin()), ci.getMetricName().length()))
                .collect(joining("; "));
        System.out.printf("Error Margin ;%s%n", errorMargins);
    }

    /**
     * Generates the LaTeX table for metrics results.
     */
    void buildLatexMetricsResultTable() {
        if(!runner.isLatexTableResultsGeneration()) {
            return;
        }

        if (runner.getSimulationRuns() == 1) {
            System.out.println("Latex table with metrics' results is just built when the number of simulation runs is greater than 1.");
            return;
        }

        final StringBuilder latex = startLatexTable();
        confidenceIntervals.forEach(ci -> latexRow(latex, ci));
        latex.append("  \\end{tabular}\n\\end{table}\n");
        System.out.println();
        System.out.println(latex);
    }

    private StringBuilder startLatexTable() {
        final var fmt =
            """
            \\begin{table}[!hbt]
                \\caption{%s}
                \\label{%s}

                \\begin{tabular}{|p{2.8cm}|p{1.3cm}p{1.3cm}|>{\\raggedleft\\arraybackslash}p{1.2cm}|}
                \\hline
                \\textbf{Metric} & \\multicolumn{2}{p{3.0cm}|}{\\textbf{95\\% Confidence Interval}} & \\textbf{*Std. Dev.} \\\\
                \\hline
            """;

        final var str = fmt.formatted(runner.getDescription(), runner.getResultsTableId());
        return new StringBuilder(str);
    }

    /**
     * Creates a row for the LaTeX table containing the result metrics
     * @param latex the StringBuilder where the LaTeX table is being built
     * @param ci {@link ConfidenceInterval} summarizing results for this metric
     */
    private void latexRow(final StringBuilder latex, final ConfidenceInterval ci) {
        final var COL_SEPARATOR = " & ";
        //if there is only one metric sample, it doesn't show the ± symbol (LaTeX \pm), since there is no error margin
        final var errorMargin = ci.isComputed() ? " & $\\pm$ %.2f".formatted(ci.getErrorMargin()) : COL_SEPARATOR;

        //If there is a % in the metric name, that needs to be escaped to show on Latex, since % starts a Latex comment
        final var escapedMetricName = StringUtils.replace(ci.getMetricName(),"%", "\\%");
        latex.append(escapedMetricName)
             .append(COL_SEPARATOR)
             .append("%.2f".formatted(ci.getValue()))
             .append(errorMargin)
             .append(COL_SEPARATOR)
             .append("%.2f".formatted(ci.getStdDev()))
             .append("\\\\ \\hline\n");
    }
}
