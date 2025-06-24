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
package org.cloudsimplus.util;

import org.apache.commons.lang3.StringUtils;

import static org.cloudsimplus.util.MathUtil.percentValue;

/**
 * A utility class with general purpose functions.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.0
 */
public final class Util {
    /** A private constructor to avoid class instantiation. */
    private Util(){/**/}

    /**
     * Makes the current thread to sleep for a given amount ot milliseconds.
     * @param millis the time to sleep in milliseconds
     */
    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Prints a progress bar at the command line for any general process
     * represented by several tasks (steps). The bar is updated at the current line.
     * If nothing is printed between updates, the bar is shown at the same place.
     * You can use it like the sample below:
     * {@snippet :
     *  final int total = 100;
     *  for (int i = 0; i <= total; i++) {
     *      Util.sleep(120); //simulates some task (use your own code here)
     *      Util.printProgress(i, total);
     *  }
     * }
     * @param current the index of the current finished task (step)
     * @param total the total number of tasks (steps)
     * @see #printProgress(int, int, boolean)
     */
    public static void printProgress(final int current, final int total){
        printProgress(current, total, false);
    }

    /**
     * Prints a progress bar at the command line for any general process
     * represented by several tasks (steps).
     * You can use it like the sample below:
     * {@snippet :
     *  final int total = 100;
     *  for (int i = 0; i <= total; i++) {
     *      Util.sleep(120); //simulates some task (use your own code here)
     *      Util.printProgress(i, total);
     *  }
     * }
     * @param current the index of the current finished task (step)
     * @param total the total number of tasks (steps)
     * @param progressBarInNewLine indicates if the progress bar is to be printed in a new line or not
     * @see #printProgress(int, int)
     */
    public static void printProgress(final int current, final int total, final boolean progressBarInNewLine){
        final String progress = StringUtils.repeat('#', current);

        final String end = progressBarInNewLine ? "%n" : "\r";
        final String format = "%120s[%-"+total+"s] %3.0f%% (%d/%d)" + end;
        System.out.printf(format, " ", progress, percentValue(current, total), current, total);
    }

    /**
     * Creates a square matrix with a given size
     * @param size the matrix size, defining the number of columns and rows
     * @return the new square matrix
     */
    public static double[][] newSquareMatrix(final int size) {
        return new double[size][size];
    }

    /**
     * Creates a square matrix with a given size
     * @param size the matrix size, defining the number of columns and rows
     * @param defaultValue default value for all matrix positions.
     * @return the new square matrix
     */
    public static double[][] newSquareMatrix(final int size, final double defaultValue) {
        final var matrix = newSquareMatrix(size);
        for (final double[] line : matrix) {
            for (int col = 0; col < size; ++col) {
                line[col] = defaultValue;
            }
        }

        return matrix;
    }

    /**
     * {@return the file extension including the dot}
     * @param fileName file name to extract extension
     */
    public static String getFileExtension(final String fileName){
        final int i = fileName.lastIndexOf(".");
        return i == -1 ? "" : fileName.substring(i).toLowerCase();

    }
}
