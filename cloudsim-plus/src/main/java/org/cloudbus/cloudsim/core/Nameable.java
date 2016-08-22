package org.cloudbus.cloudsim.core;

/**
 * An interface for objects that have to be identified by an id
 * and that also have a name.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface Nameable extends Identificable {
    String getName();
}
