package org.cloudbus.cloudsim.util;

import org.cloudbus.cloudsim.listeners.EventListener;

/**
 * An {@link EventListener} that register the number of times the
 * {@link #update(double, java.lang.Object, java.lang.Object)} method
 * was called. The class can be used by Unit and Integration/Functional Tests
 * in order to verify that a given event was called
 * an exact amount of times.<p/>
 * 
 * <b>NOTE:</b> When creating a subclass of this one (an anonymous class or not),
 * make sure to call {@code super.update(time, observed, data)}
 * to ensure that the count will be performed.
 * 
 * @see #getUpdatesCount() 
 * @author Manoel Campos da Silva Filho
 */
public abstract class UpdatesCountEventListener<T, D> implements EventListener<T, D> {
    /** @see #getUpdatesCount() */
    private int updatesCount = 0;

    @Override
    public void update(double time, T observed, D data) {
        updatesCount++;
    }

    /**
     * @return the number of times the {@link #update(double, java.lang.Object, java.lang.Object)}
     * method was called.
     */
    public int getUpdatesCount() {
        return updatesCount;
    }
    
}
