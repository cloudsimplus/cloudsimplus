package org.cloudsimplus.traces;

import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * An abstract class to implement trace file readers for specific file formats.
 *
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
@Accessors
public abstract class TraceReaderAbstract extends FileReader implements TraceReader {

    /**
     * Create a SwfWorkloadFileReader object.
     *
     * @param filePath the workload trace file path in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @throws IllegalArgumentException when the workload trace file name is null or empty; or the resource PE mips is less or equal to 0
     * @throws IllegalArgumentException when the workload trace file name is null or empty
     */
    public TraceReaderAbstract(@NonNull final String filePath) {
        super(filePath);
        if (filePath.isBlank()) {
            throw new IllegalArgumentException("Trace file name cannot be blank.");
        }

        this.setMaxLinesToRead(Integer.MAX_VALUE);
    }

}
