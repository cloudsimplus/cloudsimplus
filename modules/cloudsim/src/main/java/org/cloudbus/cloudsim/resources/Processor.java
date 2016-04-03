package org.cloudbus.cloudsim.resources;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A Central Unit Processing (CPU) that can have multiple 
 * cores ({@link Pe Processing Elements}).
 * @author Manoel Campos da Silva Filho
 */
public class Processor extends AbstractResource<Double>{
    /** @see #getNumberOfPes() */
    private int numberOfPes;
    
    /**
     * Instantiates a new Processor.
     * 
     * @param individualPeCapacity capacity of each {@link Pe Processing Elements (cores)}
     * @param numberOfPes number of {@link Pe Processing Elements (cores)}
     */
    public Processor(Double individualPeCapacity, int numberOfPes) {
        super(individualPeCapacity);
        this.numberOfPes = numberOfPes;
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
        if(mipsList.isEmpty()){
            throw new IllegalArgumentException("The mipsList cannot be empty or having just elements of zero capacity.");
        }
        
        final Double firstItem = mipsList.get(0);
        if(mipsList.size() > 1){
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
    @Override
    public Double getCapacity() {
        return super.getCapacity();
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
    public void setNumberOfPes(int numberOfPes) {
        this.numberOfPes = numberOfPes;
    }
}
