package org.cloudsimplus.core;

import lombok.Getter;
import lombok.Setter;
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

    @Setter
    private double shutdownBeginTime = NOT_ASSIGNED;

    @Override
    public boolean isShuttingDown() {
        return !isFinished() && shutdownBeginTime > 0;
    }

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
