package pro.shushi.pamirs.core.common.task.extension;

import pro.shushi.pamirs.core.common.progress.PamirsProgress;
import pro.shushi.pamirs.core.common.progress.extension.DefaultPamirsProgress;
import pro.shushi.pamirs.core.common.signature.extension.AbstractPamirsSignature;
import pro.shushi.pamirs.core.common.task.PamirsTask;
import pro.shushi.pamirs.core.common.task.constant.PamirsTaskConstant;

/**
 * 默认标准任务实现
 *
 * @author Adamancy Zhang on 2021-05-09 15:59
 */
public class DefaultPamirsTask extends AbstractPamirsSignature implements PamirsTask {

    private final PamirsProgress progress;

    private String message;

    private boolean hasError;

    private String errorMessage;

    private Throwable throwable;

    private long weight;

    public DefaultPamirsTask(String signature) {
        super(signature);
        this.message = PamirsTaskConstant.DEFAULT_TASK_MESSAGE;
        this.hasError = false;
        this.progress = generatorProgress();
    }

    @Override
    public boolean isStarted() {
        return progress.isStarted();
    }

    @Override
    public boolean isFinished() {
        return progress.isFinished();
    }

    @Override
    public boolean isRunning() {
        return progress.isRunning();
    }

    @Override
    public long getStartTime() {
        return progress.getStartTime();
    }

    @Override
    public long getEndTime() {
        return progress.getEndTime();
    }

    @Override
    public void start() {
        progress.start();
    }

    @Override
    public void finish() {
        progress.finish();
    }

    @Override
    public int getProgress() {
        return progress.get();
    }

    @Override
    public void setProgress(int value) {
        progress.set(value);
    }

    @Override
    public void incrementProgress(int value) {
        progress.increment(value);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void error(String errorMessage) {
        this.hasError = true;
        this.errorMessage = errorMessage;
    }

    @Override
    public void error(Throwable throwable) {
        this.hasError = true;
        this.throwable = throwable;
    }

    @Override
    public void error(String errorMessage, Throwable throwable) {
        this.hasError = true;
        this.errorMessage = errorMessage;
        this.throwable = throwable;
    }

    @Override
    public boolean hasError() {
        return hasError;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    public long getWeight() {
        return weight;
    }

    public DefaultPamirsTask setWeight(long weight) {
        if (!isStarted()) {
            this.weight = weight;
        }
        return this;
    }

    protected PamirsProgress generatorProgress() {
        return new DefaultPamirsProgress();
    }
}
