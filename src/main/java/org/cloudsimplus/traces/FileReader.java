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
package org.cloudsimplus.traces;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.cloudsimplus.util.ResourceLoader;
import org.cloudsimplus.util.Util;

import java.io.*;
import java.util.Arrays;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * Reads and parses data inside text, zip and gzip files.
 *
 * @author Manoel Campos da Silva Filho
 * @author Anton Beloglazov
 * @since CloudSim Plus 8.1.0
 */
public class FileReader {
    public static final String DEF_FIELD_DELIMITER_REGEX = "\\s+";

    @Getter
    private final String filePath;

    /// A regex defining how fields are delimited in the trace file.
    /// Usually, this can be just a String with a single character such as
    /// a space, comma, semicolon or tab (`\t`).
    @Getter @Setter @NonNull
    private String fieldDelimiterRegex;

    @Getter
    private int lastLineNumber;

    /**
     * The maximum number of lines from the trace reader that will be read.
     * {@link Integer#MAX_VALUE} indicates that all lines will be read.
     */
    @Getter
    private int maxLinesToRead;

    private String[] commentString = {";", "#"};

    /**
     * Creates a file reader that considers spaces as field delimiter.
     * @param filePath path of the file to read
     * @see #getSingleLineReader(String)
     */
    public FileReader(@NonNull final String filePath) {
        this(DEF_FIELD_DELIMITER_REGEX, filePath, Integer.MAX_VALUE);
    }

    public FileReader(final String fieldDelimiterRegex, @NonNull final String filePath) {
        this(fieldDelimiterRegex, filePath, Integer.MAX_VALUE);
    }

    /**
     * Creates a file reader that considers spaces as field delimiter.
     * @param filePath path of the file to read
     * @param maxLinesToRead The maximum number of lines from the trace reader that will be read.
     *                       {@link Integer#MAX_VALUE} indicates that all lines will be read.
     * @see #getSingleLineReader(String)
     */
    public FileReader(@NonNull final String filePath, final int maxLinesToRead) {
        this(DEF_FIELD_DELIMITER_REGEX, filePath, maxLinesToRead);
    }

    /**
     * Creates a file reader.
     * @param fieldDelimiterRegex A regex defining how fields are delimited in the trace file.
     *                            See {@link #getFieldDelimiterRegex()} for details.
     * @param filePath path of the file to read
     * @param maxLinesToRead The maximum number of lines from the trace reader that will be read.
     *                       {@link Integer#MAX_VALUE} indicates that all lines will be read.
     * @see #getSingleLineReader(String)
     */
    public FileReader(final String fieldDelimiterRegex, @NonNull final String filePath, final int maxLinesToRead) {
        this.fieldDelimiterRegex = fieldDelimiterRegex;
        this.filePath = filePath;
        this.maxLinesToRead = maxLinesToRead;
    }

    /**
     * Gets a reader that parses only the first line inside the given file,
     * considering space as the field delimiter
     * @param filePath the path of the file to read
     * @return the file reader
     */
    public static FileReader getSingleLineReader(@NonNull final String filePath){
        return new FileReader(filePath, 1);
    }

    /**
     * Sets Strings that identify the start of a comment line.
     * @param commentString the comment Strings to set
     */
    public FileReader setCommentString(@NonNull final String... commentString) {
        if (commentString.length == 0) {
            throw new IllegalArgumentException("A comment String is required");
        }

        // Creates a defensive copy of the array to avoid directly changes its values after storing it
        this.commentString = Arrays.copyOf(commentString, commentString.length);
        return this;
    }

    /**
     * Gets the Strings that identifies the start of a comment line.
     * <br>For instance <b>; # % //</b>, according to the file to read.
     */
    public String[] getCommentString() {
        return Arrays.copyOf(commentString, commentString.length);
    }

    /**
     * Sets the maximum number of lines from the workload reader that will be read.
     * {@link Integer#MAX_VALUE} indicates that all lines will be read.
     *
     * @param maxLinesToRead the maximum number of lines to set
     * @return the file reader
     */
    public final FileReader setMaxLinesToRead(final int maxLinesToRead) {
        if (maxLinesToRead <= 0) {
            throw new IllegalArgumentException("Maximum number of lines to read from the trace must be greater than 0. If you want to read the entire file, provide Integer.MAX_VALUE.");
        }

        this.maxLinesToRead = maxLinesToRead;
        return this;
    }

    private boolean isComment(final String line) {
        return Arrays.stream(commentString).anyMatch(line::startsWith);
    }

    /**
     * Reads a trace file indicated by the {@link #getFilePath()}.
     *
     * @return the last parsed line
     * @throws UncheckedIOException if there was any error reading the file
     */
    public String[] readFile() {
        return readFile(parsedLine -> true);
    }

    /**
     * Reads a trace file indicated by the {@link #getFilePath()}.
     * It performs additional processing after parsing the line.
     *
     * @param processParsedLineFunc a {@link Function} that receives each parsed line as an array
     *                              and performs an operation over it, returning true if the operation was executed
     * @return the last parsed line
     * @throws UncheckedIOException if there was any error reading the file
     */
    protected String[] readFile(final Function<String[], Boolean> processParsedLineFunc) {
        try {
            final var ext = Util.getFileExtension(getFilePath());
            final var is = ResourceLoader.newInputStream(getFilePath(), getClass());
            return switch (ext) {
                case ".gz" -> readFileInternal(new GZIPInputStream(is), processParsedLineFunc);
                case ".zip" -> readZipFile(is, processParsedLineFunc);
                default -> readFileInternal(is, processParsedLineFunc);
            };
        } catch(IOException e){
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads a trace file inside a zip.
     *
     * @param is a {@link InputStream} to read the file
     * @param processParsedLineFunc a {@link Function} that receives each parsed line as an array
     *                              and performs an operation over it, returning true if the operation was executed
     * @return the last parsed line
     * @throws IOException if there was any error reading the file
     */
    @SneakyThrows(IOException.class)
    private String[] readZipFile(
        @NonNull final InputStream is,
        final Function<String[], Boolean> processParsedLineFunc)
    {
        try (var zipInputStream = new ZipInputStream(is)) {
            //Get the first file inside the zip (other ones are ignored)
            if (zipInputStream.getNextEntry() != null) {
                return readFileInternal(zipInputStream, processParsedLineFunc);
            }
        }

        return new String[0];
    }

    /**
     * Reads a trace file from an {@link InputStream} linked to a file in any supported format.
     *
     * @param is a {@link InputStream} to read the file
     * @param processParsedLineFunc a {@link Function} that receives each parsed line as an array
     *                              and performs an operation over it, returning true if the operation was executed.
     * @return the last parsed line
     * @throws IOException if there was any error reading the file
     */
    @SneakyThrows(IOException.class)
    private String[] readFileInternal(
        @NonNull final InputStream is,
        @NonNull final Function<String[], Boolean> processParsedLineFunc)
    {
        this.lastLineNumber = 0;
        String[] parsedLine = new String[0];
        try(var reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = readNextLine(reader)) != null) {
                parsedLine = parseLine(line);
                if (parsedLine.length > 0 && processParsedLineFunc.apply(parsedLine)) {
                    this.lastLineNumber++;
                }
            }
        }

        return parsedLine;
    }

    /**
     * Reads the next line of the trace file.
     *
     * @param reader the object that is reading the trace file
     * @return the line read; otherwise null if there isn't any more lines to read or if
     * the number of lines to read was reached
     * @see #getMaxLinesToRead()
     */
    @SneakyThrows
    private String readNextLine(final BufferedReader reader) {
        if (reader.ready() && lastLineNumber <= maxLinesToRead - 1) {
            return reader.readLine();
        }

        return null;
    }

    private String[] parseLine(final String line) {
        if (isComment(line)) {
            return new String[0];
        }

        // Splits the string, ensuring that empty fields won't be discarded
        return line.trim().split(fieldDelimiterRegex, -1);
    }
}
