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
 * <p>
 * <b>NOTES:</b>
 * <ul>
 *   <li>This class can only read trace files in the following format:
 *       <b>ASCII text, zip, gz.</b>
 *   </li>
 *   <li>If you need to load multiple trace files, create multiple instances of this class.</li>
 *   <li>If size of the trace reader is huge or contains lots of traces, please
 *       increase the JVM heap size accordingly by using <b>java -Xmx</b> option
 *       when running the simulation. For instance, you can use <b>java -Xmx200M</b>
 *       to define the JVM heap size will be 200MB.
 *   </li>
 * </ul>
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public abstract class TraceReaderAbstract implements TraceReader {
    private final String filePath;
    private final InputStream inputStream;

    /** @see #getFieldDelimiterRegex() */
    private String fieldDelimiterRegex;

    /**
     * @see #getMaxLinesToRead()
     */
    private int maxLinesToRead;

    private String[] commentString = {";", "#"};

    /** @see #getLastLineNumber() */
    private int lastLineNumber;

    /**
     * Create a new SwfWorkloadFileReader object.
     *
     * @param filePath the workload trace file path in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @throws IllegalArgumentException when the workload trace file name is null or empty; or the resource PE mips is less or equal to 0
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
     * @param inputStream   a {@link InputStreamReader} object to read the file
     * @throws IllegalArgumentException when the workload trace file name is null or empty; or the resource PE mips is less or equal to 0
     */
    protected TraceReaderAbstract(final String filePath, final InputStream inputStream) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid trace file name.");
        }

        this.fieldDelimiterRegex = "\\s+";
        this.maxLinesToRead = -1;
        this.inputStream = inputStream;
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

    protected InputStream getInputStream() {
        return inputStream;
    }

    protected String[] parseTraceLine(final String line){
        if (isComment(line)) {
            return new String[0];
        }

        //Splits the string, ensuring that empty fields won't be discarded
        return line.trim().split(fieldDelimiterRegex, -1);
    }

    private boolean isComment(final String line) {
        return Arrays.stream(commentString).anyMatch(line::startsWith);
    }

    /**
     * Reads traces from a text file, then creates a Cloudlet for each line read.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @throws IOException if the there was any error reading the file
     */
    protected void readTextFile(final InputStream inputStream, final Function<String[], Boolean> processParsedLineFunction) throws IOException {
        readFile(inputStream, processParsedLineFunction);
    }

    /**
     * Reads traces from a gzip file, then creates a Cloudlet for each line read.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @throws IOException if the there was any error reading the file
     */
    protected void readGZIPFile(final InputStream inputStream, final Function<String[], Boolean> processParsedLineFunction) throws IOException {
        readFile(new GZIPInputStream(inputStream), processParsedLineFunction);
    }

    /**
     * Reads a set of trace files inside a Zip file, then creates a Cloudlet for each line read.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @return <code>true</code> if reading a file is successful;
     * <code>false</code> otherwise.
     * @throws IOException if the there was any error reading the file
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
     * @throws UncheckedIOException if the there was any error reading the file
     */
    protected void readFile(final Function<String[], Boolean> processParsedLineFunction) {
        /*@TODO It would be implemented using specific classes to avoid this "if" chain.
                If a new format is included, the code has to be changed to include another if*/
        try {
            if (getFilePath().endsWith(".gz")) {
                readGZIPFile(getInputStream(), processParsedLineFunction);
            } else if (getFilePath().endsWith(".zip")) {
                readZipFile(getInputStream(), processParsedLineFunction);
            } else {
                readTextFile(getInputStream(), processParsedLineFunction);
            }
        } catch(IOException e){
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads traces from an {@link InputStream} linked to a file in any supported format,
     * then creates a Cloudlet for each line read.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @param processParsedLineFunction a {@link Function} that receives each parsed line as an array
     *                          and performs an operation over it, returning true if the operation was executed
     * @throws IOException if the there was any error reading the file
     */
    private void readFile(final InputStream inputStream, final Function<String[], Boolean> processParsedLineFunction) throws IOException {
        requireNonNull(inputStream);
        requireNonNull(processParsedLineFunction);

        //The reader is safely closed by the caller
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        lastLineNumber = 0;
        String line;
        while ((line = readNextLine(reader, lastLineNumber)) != null) {
            final String[] parsedTraceLine = parseTraceLine(line);
            if(parsedTraceLine.length > 0 && processParsedLineFunction.apply(parsedTraceLine)) {
                lastLineNumber++;
            }
        }
    }

    /**
     * Reads the next line of the workload file.
     *
     * @param reader     the object that is reading the workload file
     * @param lineNumber the number of the line that that will be read from the workload file (starting from 0)
     * @return the line read; or null if there isn't any more lines to read or if
     * the number of lines read reached the {@link #getMaxLinesToRead()}
     */
    private String readNextLine(final BufferedReader reader, final int lineNumber) throws IOException {
        if (reader.ready() && (maxLinesToRead == -1 || lineNumber <= maxLinesToRead-1)) {
            return reader.readLine();
        }

        return null;
    }

    @Override
    public int getLastLineNumber() {
        return lastLineNumber;
    }
}
