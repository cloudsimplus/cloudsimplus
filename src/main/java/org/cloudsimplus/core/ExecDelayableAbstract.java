package org.cloudsimplus.core;

import lombok.Getter;
import org.cloudsimplus.util.MathUtil;

/**
 * A base implementation for {@link ExecDelayable} entities.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 8.3.0
 */
@Getter
public abstract class ExecDelayableAbstract extends StartableAbstract implements ExecDelayable {
    private double startupDelay;
    private double shutDownDelay;

    @Override
    public ExecDelayable setStartupDelay(final double delay) {
        this.startupDelay = MathUtil.nonNegative(delay, "Startup Delay");
        return this;
    }

    @Override
    public ExecDelayable setShutDownDelay(final double delay) {
        this.shutDownDelay = MathUtil.nonNegative(delay, "Shutdown Delay");
        return this;
    }

}
