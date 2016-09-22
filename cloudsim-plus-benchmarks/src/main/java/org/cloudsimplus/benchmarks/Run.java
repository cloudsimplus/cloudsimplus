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
public class Run {
    /**
     * Regex that identifies the classes with benchmarks that have to be
     * executed.
     */
    private static final String TEST_CLASSES_REGEX = "org.cloudsimplus.*";
 
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