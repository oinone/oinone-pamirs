package pro.shushi.pamirs.core.common.progress.extension;

import pro.shushi.pamirs.core.common.progress.PamirsProgress;
import pro.shushi.pamirs.core.common.progress.constant.PamirsProgressConstant;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Adamancy Zhang on 2021-05-09 13:25
 */
public class DefaultPamirsProgress implements PamirsProgress {

    private final AtomicInteger value = new AtomicInteger(PamirsProgressConstant.MIN_PROGRESS);

    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    private final AtomicBoolean isFinished = new AtomicBoolean(false);

    private long startTime = -1;

    private long endTime = -1;

    @Override
    public int get() {
        return value.get();
    }

    @Override
    public void set(int value) {
        verificationIsNotStarted();
        verificationIsFinished();
        verificationAllowSetProgress(value, PamirsProgressConstant.MIN_PROGRESS, PamirsProgressConstant.MAX_PROGRESS);
        this.value.set(value);
    }

    @Override
    public void increment(int value) {
        verificationIsNotStarted();
        verificationIsFinished();
        verificationAllowSetProgress(value, PamirsProgressConstant.MIN_PROGRESS, PamirsProgressConstant.MAX_PROGRESS);
        verificationAllowSetProgress(this.value.addAndGet(value), PamirsProgressConstant.MIN_PROGRESS, PamirsProgressConstant.MAX_PROGRESS);
    }

    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    @Override
    public boolean isFinished() {
        return isFinished.get();
    }

    @Override
    public boolean isRunning() {
        return isStarted() && !isFinished();
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void finish() {
        if (isFinished.compareAndSet(false, true)) {
            endTime = System.currentTimeMillis();
            value.set(PamirsProgressConstant.MAX_PROGRESS);
        }
    }

    protected void verificationIsNotStarted() {
        if (!isStarted()) {
            throw new IllegalStateException("The progress is not started.");
        }
    }

    protected void verificationIsFinished() {
        if (isFinished()) {
            throw new IllegalStateException("The progress has ended.");
        }
    }

    protected void verificationAllowSetProgress(int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException("Invalid progress value. min=" + min + ", max=" + max);
        }
    }
}
