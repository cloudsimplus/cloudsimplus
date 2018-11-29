/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.util;

/**
 * A basic interface for classes that read specific trace file formats.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 */
public interface TraceReader {

    /**
     * Gets the Strings that identifies the start of a comment line.
     * For instance <b>; # % //</b>.
     */
    String[] getCommentString();

    /**
     * Sets a string that identifies the start of a comment line.
     * If there are multiple ways to comment a line,
     * the different Strings representing comments can be specified as parameters.
     *
     * @param commentString the comment Strings to set
     */
    TraceReader setCommentString(String... commentString);

    /**
     * Gets the regex defining how fields are delimited in the trace file.
     * Usually, this can be just a String with a single character such as
     * a space, comma, semi-colon or tab (\t).
     * @return
     */
    String getFieldDelimiterRegex();

    /**
     * Sets the regex defining how fields are delimited in the trace file.
     * Usually, this can be just a String with a single character such as
     * a space, comma or semi-colon or tab (\t).
     * @param fieldDelimiterRegex the field separator regex to set
     */
    TraceReader setFieldDelimiterRegex(String fieldDelimiterRegex);

    /**
     * Gets the maximum number of lines of the workload reader that will be read.
     * The value -1 indicates that all lines will be read, creating
     * a cloudlet from every one.
     *
     * @return
     */
    int getMaxLinesToRead();

    /**
     * Sets the maximum number of lines of the workload reader that will be read.
     * The value -1 indicates that all lines will be read, creating
     * a cloudlet from every one.
     *
     * @param maxLinesToRead the maximum number of lines to set
     */
    TraceReader setMaxLinesToRead(int maxLinesToRead);

    /**
     * Gets the path of the trace file.
     * @return
     */
    String getFilePath();

    /**
     * Gets the number of the last line read from the trace file (starting from 0).
     * @return
     */
    int getLastLineNumber();
}
