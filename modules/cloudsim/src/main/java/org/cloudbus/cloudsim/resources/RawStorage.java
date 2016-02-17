package org.cloudbus.cloudsim.resources;

/**
 * A simple storage that just manages the device capacity and raw allocated (used) space.
 * It doesn't deals with files neither with file system operations such as
 * file inclusion or deletion.
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail.com>
 */
public final class RawStorage extends AbstractResource<Long> {
    public RawStorage(Long capacity) {
        super(capacity);
    }
}
