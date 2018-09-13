package org.cloudbus.cloudsim.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import static java.util.Objects.requireNonNull;

/**
 * An abstract class providing features for subclasses implementing trace file readers for specific file formats.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public abstract class TraceReaderAbstract implements TraceReader {
    private final String filePath;
    private final InputStream reader;

    /** @see #getFieldDelimiterRegex() */
    private String fieldDelimiterRegex;

    /**
     * @see #getMaxLinesToRead()
     */
    private int maxLinesToRead;

    private String[] commentString = {";", "#"};

    /**
     * Create a new SwfWorkloadFileReader object.
     *
     * @param filePath the workload trace file path in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @throws IllegalArgumentException when the workload trace file name is null or empty; or the resource PE mips <= 0
     * @throws FileNotFoundException    when the trace file is not found
     * @throws IllegalArgumentException when the workload trace file name is null or empty
     */
    public TraceReaderAbstract(final String filePath) throws IOException {
        this(filePath, Files.newInputStream(Paths.get(filePath)));
    }

    /**
     * Create a new SwfWorkloadFileReader object.
     *
     * @param filePath the workload trace file path in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @param reader   a {@link InputStreamReader} object to read the file
     * @throws IllegalArgumentException when the workload trace file name is null or empty; or the resource PE mips <= 0
     */
    protected TraceReaderAbstract(final String filePath, final InputStream reader) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid trace reader name.");
        }

        this.fieldDelimiterRegex = "\\s+";
        this.maxLinesToRead = -1;
        this.reader = reader;
        this.filePath = filePath;
    }

    @Override
    public TraceReader setCommentString(final String... commentString) {
        if (requireNonNull(commentString).length == 0) {
            throw new IllegalArgumentException("A comment String is required");
        }
        //Creates a defensive copy of the array to avoid directly change its values after storing it
        this.commentString = Arrays.copyOf(commentString, commentString.length);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>It's returned a defensive copy of the array.</p>
     * @return {@inheritDoc}
     */
    @Override
    public String[] getCommentString() {
        return Arrays.copyOf(commentString, commentString.length);
    }

    @Override
    public String getFieldDelimiterRegex() {
        return fieldDelimiterRegex;
    }

    @Override
    public final TraceReader setFieldDelimiterRegex(String fieldDelimiterRegex) {
        this.fieldDelimiterRegex = fieldDelimiterRegex;
        return this;
    }

    @Override
    public int getMaxLinesToRead() {
        return maxLinesToRead;
    }

    @Override
    public TraceReader setMaxLinesToRead(int maxLinesToRead) {
        this.maxLinesToRead = maxLinesToRead;
        return this;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    protected InputStream getReader() {
        return reader;
    }

    protected String[] parseTraceLine(final String line){
        if (isComment(line)) {
            return new String[0];
        }

        return line.trim().split(fieldDelimiterRegex);
    }

    private boolean isComment(final String line) {
        return Arrays.stream(commentString).anyMatch(line::startsWith);
    }

    /**
     * Reads traces from a text reader, then creates a Cloudlet for each line read.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @return <code>true</code> if successful, <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the reader
     */
    protected void readTextFile(final InputStream inputStream, final Function<String[], Boolean> processParsedLineFunction) throws IOException {
        readFile(inputStream, processParsedLineFunction);
    }

    /**
     * Reads traces from a gzip reader, then creates a Cloudlet for each line read.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @return <code>true</code> if successful; <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the reader
     */
    protected void readGZIPFile(final InputStream inputStream, final Function<String[], Boolean> processParsedLineFunction) throws IOException {
        readFile(new GZIPInputStream(inputStream), processParsedLineFunction);
    }

    /**
     * Reads a set of trace files inside a Zip reader, then creates a Cloudlet for each line read.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @return <code>true</code> if reading a reader is successful;
     * <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the reader
     */
    protected boolean readZipFile(final InputStream inputStream, final Function<String[], Boolean> processParsedLineFunction) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(requireNonNull(inputStream))) {
            while (zipInputStream.getNextEntry() != null) {
                readFile(zipInputStream, processParsedLineFunction);
            }
            return true;
        }
    }

    /**
     * Reads traces from the file indicated by the {@link #getFilePath()},
     * then creates a Cloudlet for each line read.
     *
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @return <code>true</code> if successful, <code>false</code> otherwise.
     * @throws UncheckedIOException if the there was any error reading the reader
     */
    protected void readFile(final Function<String[], Boolean> processParsedLineFunction) {
        /*@todo It would be implemented using specific classes to avoid this if chain.
        If a new format is included, the code has to be changed to include another if*/
        try {
            if (getFilePath().endsWith(".gz")) {
                readGZIPFile(getReader(), processParsedLineFunction);
            } else if (getFilePath().endsWith(".zip")) {
                readZipFile(getReader(), processParsedLineFunction);
            } else {
                readTextFile(getReader(), processParsedLineFunction);
            }
        } catch(IOException e){
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads traces from a InputStream linked to a file in any supported format,
     * then creates a Cloudlet for each line read.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @return <code>true</code> if successful, <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the reader
     */
    private void readFile(final InputStream inputStream, final Function<String[], Boolean> processParsedLineFunction) throws IOException {
        requireNonNull(inputStream);
        requireNonNull(processParsedLineFunction);

        //The reader is safely closed by the caller
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int lineNum = 1;
        String line;
        while ((line = readNextLine(reader, lineNum)) != null) {
            final String[] parsedTraceLine = parseTraceLine(line);
            if(parsedTraceLine.length > 0 && processParsedLineFunction.apply(parsedTraceLine)) {
                lineNum++;
            }
        }
    }

    /**
     * Reads the next line of the workload reader.
     *
     * @param reader     the object that is reading the workload reader
     * @param lineNumber the number of the line that that will be read from the workload reader
     * @return the line read; or null if there isn't any more lines to read or if
     * the number of lines read reached the {@link #getMaxLinesToRead()}
     */
    private String readNextLine(final BufferedReader reader, final int lineNumber) throws IOException {
        if (reader.ready() && (maxLinesToRead == -1 || lineNumber <= maxLinesToRead)) {
            return reader.readLine();
        }

        return null;
    }
}
