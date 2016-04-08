package org.cloudbus.cloudsim.resources;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A Central Unit Processing (CPU) that can have multiple 
 * cores ({@link Pe Processing Elements}).
 * @author Manoel Campos da Silva Filho
 * 
 * @todo Restructure resource related interfaces in order to avoid
 * the code duplication in this class (such as capacity attribute and methods).
 * The {@link Resource} interface is supposed to be the most basic kind
 * of resource and all concrete resources must implement it.
 * Classes such as {@link FileStorage} doesn't implement a Resource.
 */
public class Processor {
    private double capacity;
    /** @see #getNumberOfPes() */
    private int numberOfPes;
    
    /**
     * Instantiates a new Processor.
     * 
     * @param individualPeCapacity capacity of each {@link Pe Processing Elements (cores)}
     * @param numberOfPes number of {@link Pe Processing Elements (cores)}
     */
    public Processor(Double individualPeCapacity, int numberOfPes) {
        setCapacity(individualPeCapacity);
        setNumberOfPes(numberOfPes);
    }
    
    /**
     * Instantiates a new Processor from a given MIPS list,
     * ignoring all elements having zero capacity.
     * 
     * @param mipsList a list of {@link Pe Processing Elements (cores)} capacity
     * where all elements have the same capacity.
     * @return the new processor
     */
    public static Processor getProcessorFromMipsListRemovingAllZeroMips(List<Double> mipsList) {
        if(mipsList == null){
            throw new IllegalArgumentException("The mipsList cannot be null.");
        }

        mipsList = getNonZeroMipsElements(mipsList);
        
        Double firstItem = 0.0;
        if(!mipsList.isEmpty()){
            firstItem = mipsList.get(0);
            for(Double mips: mipsList){
                if(!Objects.equals(mips, firstItem)){
                    throw new IllegalArgumentException(
                        String.format(
                            "Mips %.2f is different from the other ones in the mipsShare list: %.2f", 
                            mips, firstItem));
                }
            }
        }

        return new Processor(firstItem, mipsList.size());
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
    public Double getCapacity() {
        return capacity;
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

    public final boolean setCapacity(Double newCapacity) {
        if(newCapacity == null || newCapacity < 0)
            throw new IllegalArgumentException("Capacity cannot be null or negative");
        this.capacity = newCapacity;
        return true;
    }
}
