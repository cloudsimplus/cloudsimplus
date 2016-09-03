package org.cloudbus.cloudsim.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.cloudbus.cloudsim.CloudletExecutionInfo;

/**
 * A Central Unit Processing (CPU) that can have multiple 
 * cores ({@link Pe Processing Elements}).
 * 
 * @author Manoel Campos da Silva Filho
 */
public class Processor implements ResourceCapacity<Double>{
    private double capacity;
    
    /** @see #getNumberOfPes() */
    private int numberOfPes;
    
    /** @see #getCloudletExecList() */
    private List<CloudletExecutionInfo> cloudletExecList;
    
    /**
     * Instantiates a new Processor.
     * 
     * @param individualPeCapacity capacity of each {@link Pe Processing Elements (cores)}
     * @param numberOfPes number of {@link Pe Processing Elements (cores)}
     */
    public Processor(Double individualPeCapacity, int numberOfPes) {
        cloudletExecList = new ArrayList<>();
        setCapacity(individualPeCapacity);
        setNumberOfPes(numberOfPes);
    }
    
    /**
     * Instantiates a new Processor from a given MIPS list,
     * ignoring all elements having zero capacity.
     * 
     * @param mipsList a list of {@link Pe Processing Elements (cores)} capacity
     * where all elements have the same capacity. This list represents
     * the capacity of each processor core.
     * @param cloudletExecList list of information about cloudlets for starting
     * executing in this processor.
     * 
     * @return the new processor
     */
    public static Processor fromMipsList(List<Double> mipsList,
            List<CloudletExecutionInfo> cloudletExecList) {
        if(mipsList == null){
            throw new IllegalArgumentException("The mipsList cannot be null.");
        }

        mipsList = getNonZeroMipsElements(mipsList);
        
        Double peMips = 0.0;
        if(!mipsList.isEmpty()){
            peMips = mipsList.get(0);
            
            if(mipsList.stream().distinct().count() > 1){
                throw new IllegalArgumentException(
                    String.format(
                        "mipsShare list doesn't have all elements with %.2f MIPS", 
                        peMips));
            }
        }

        Processor p = new Processor(peMips, mipsList.size());
        p.setCloudletExecList(cloudletExecList);
        return p;
    }
    
    /**
     * Instantiates a new Processor from a given MIPS list,
     * ignoring all elements having zero capacity.
     * 
     * @param mipsList a list of {@link Pe Processing Elements (cores)} capacity
     * where all elements have the same capacity. This list represents
     * the capacity of each processor core.
     * 
     * @return the new processor
     */
    public static Processor fromMipsList(List<Double> mipsList) {
        return Processor.fromMipsList(mipsList, Collections.EMPTY_LIST);
    }    

    private static List<Double> getNonZeroMipsElements(List<Double> mipsList) {
        return mipsList.stream().filter(mips -> mips > 0).collect(Collectors.toList());
    }

    /**
     * Gets the total MIPS capacity of the Processor,
     * that is the sum of all its {@link Pe Processing Elements (cores)} capacity.
     * @return 
     */
    public Double getTotalMipsCapacity(){
        return getCapacity()*getNumberOfPes();
    }

    /**
     * Gets the individual MIPS capacity of each {@link Pe Processing Elements (cores)}.
     * @return 
     */
    @Override
    public Double getCapacity() {
        return capacity;
    }
    
    /**
     * Gets the amount of MIPS available (free) for each Processor PE,
     * considering the currently executing cloudlets in this processor
     * and their number of PEs these cloudlets require.
     * This is the amount of MIPS that each Cloudlet is allowed to used,
     * considering that the processor is shared among all executing
     * cloudlets. In the case of space shared schedulers,
     * there is no concurrency for PEs because some cloudlets
     * may wait in a queue until there is available PEs to be used
     * exclusively by them.
     * 
     * @return the amount of available MIPS for each Processor PE.
     */
    public double getAvailableMipsByPe(){
        final int totalPesOfAllExecCloudlets = totalPesOfAllExecCloudlets();
        if(totalPesOfAllExecCloudlets > getNumberOfPes())
            return getTotalMipsCapacity()/ totalPesOfAllExecCloudlets;
        else return getTotalMipsCapacity() / getNumberOfPes();
    }

    /**
     * 
     * @return the total number of PEs of all cloudlets currently executing in this processor.
     */
    private int totalPesOfAllExecCloudlets() {
        return cloudletExecList.stream().mapToInt(c -> c.getNumberOfPes()).reduce(0, Integer::sum);
    }
    
    /**
     * Gets the number of {@link Pe Processing Elements (cores)} of the Processor
     * @return 
     */
    public int getNumberOfPes() {
        return numberOfPes;
    }

    /**
     * Sets the number of {@link Pe Processing Elements (cores)} of the Processor
     * @param numberOfPes
     */
    public final void setNumberOfPes(int numberOfPes) {
        if(numberOfPes < 0){
            throw new IllegalArgumentException("The numberOfPes cannot be negative.");
        }
        this.numberOfPes = numberOfPes;
    }

    /**
     * Sets the individual MIPS capacity of each {@link Pe Processing Elements (cores)}.
     * @param newCapacity the new MIPS capacity of each PE
     * @return true if the capacity is valid and was set, false otherwise
     * @pre newCapacity != null && newCapacity >= 0
     */
    public final boolean setCapacity(Double newCapacity) {
        if(newCapacity == null || newCapacity < 0)
            throw new IllegalArgumentException("Capacity cannot be null or negative");
        this.capacity = newCapacity;
        return true;
    }

    /**
     * 
     * @return a read-only list of information about cloudlets currently executing 
     * in this processor
     */
    public List<CloudletExecutionInfo> getCloudletExecList() {
        return Collections.unmodifiableList(cloudletExecList);
    }

    public void setCloudletExecList(List<CloudletExecutionInfo> cloudletExecList) {
        this.cloudletExecList = cloudletExecList;
    }
}
