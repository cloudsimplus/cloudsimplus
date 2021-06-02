package org.cloudsimplus.testbeds;

/**
 * A base class to to provide base features for {@link Experiment} and {@link ExperimentRunner}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public abstract class AbstractExperiment implements Runnable {
    protected boolean verbose;

    public AbstractExperiment() {
        this.verbose = false;
    }

    /**
     * Indicates if simulation results of the experiment have to be output.
     * @return
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Prints a line break only if {@link #isVerbose()}.
     */
    public AbstractExperiment println(){
        return println("");
    }

    /**
     * Prints a message and a line break only if {@link #isVerbose()}.
     * @param msg the message to print
     */
    public AbstractExperiment println(final String msg){
        if(verbose){
            System.out.println(msg);
        }

        return this;
    }

    /**
     * Prints a formatted message and a line break only if {@link #isVerbose()}.
     * @param format the message format
     * @param args the values to print
     */
    public AbstractExperiment println(final String format, final Object ...args){
        return print(format + "%n", args);
    }

    /**
     * Prints a message only if {@link #isVerbose()}.
     * @param msg the message to print
     */
    public AbstractExperiment print(final String msg){
        if(verbose){
            System.out.print(msg);
        }

        return this;
    }

    /**
     * Prints a formatted message only if {@link #isVerbose()}.
     * @param format the message format
     * @param args the values to print
     */
    public AbstractExperiment print(final String format, final Object ...args){
        if(verbose){
            System.out.printf(format, args);
        }

        return this;
    }

    /**
     * Indicates if simulation results of the experiment don't have to be
     * output.
     * @return
     */
    public boolean isNotVerbose() {
        return !verbose;
    }

    /**
     * Defines if simulation results of the experiment have to be output or not.
     *
     * @param verbose true if the results have to be output, false otherwise
     * @return
     */
    public AbstractExperiment setVerbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }
}
