/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.benchmarks;

import java.io.IOException;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

/**
 * Configures parameters for the JMH Microbenchmarking framework and starts
 * benchmarks execution.
 *
 * To run the benchmarks, clean & build the main project.
 * Then clean & build this project and run this class.
 *
 * @author Manoel Campos da Silva Filho
 * @see <a href="http://tutorials.jenkov.com/java-performance/jmh.html">JMH - Java Microbenchmark Harness Tutorial</a>
 * @see <a href="http://java-performance.info/jmh/">Java Performance: JMH</a>
 */
public final class Run {
    /**
     * Regex that identifies the classes with benchmarks that have to be
     * executed.
     */
    private static final String TEST_CLASSES_REGEX = "org.cloudsimplus.*";

    /**
     * A private constructor to avoid class instantiation.
     */
    private Run(){}

    public static void main(String[] args) throws IOException, RunnerException {
        System.out.println(TEST_CLASSES_REGEX);
        Options options = new OptionsBuilder()
                .include(TEST_CLASSES_REGEX)
                .forks(1)
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(100))
                .threads(1)
                .verbosity(VerboseMode.NORMAL)
                .build();

        new Runner(options).run();

        //Alternative way to receive parameters via command line and run the benchmarks
        //Main.main(args);
    }

}
