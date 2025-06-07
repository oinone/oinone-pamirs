package pro.shushi.pamirs.core.common.task.extension;

import pro.shushi.pamirs.core.common.task.PamirsChainTask;
import pro.shushi.pamirs.core.common.task.PamirsRelationTask;
import pro.shushi.pamirs.core.common.task.PamirsTask;

/**
 * 默认链式任务实现
 *
 * @author Adamancy Zhang on 2021-05-09 14:28
 */
public class DefaultChainPamirsTask extends DefaultPamirsTask implements PamirsChainTask {

    private PamirsTask previous;

    private PamirsTask next;

    public DefaultChainPamirsTask(String signature) {
        super(signature);
    }

    public DefaultChainPamirsTask(String signature, PamirsChainTask previous, PamirsChainTask next) {
        super(signature);
        this.previous = previous;
        this.next = next;
    }

    @Override
    public PamirsTask getPrevious() {
        return previous;
    }

    @Override
    public void setPrevious(PamirsTask task) {
        if (task == null) {
            if (this.previous == null) {
                return;
            } else {
                unsetPreviousByType(this.previous);
            }
        } else {
            if (this.previous != null) {
                if (task.equals(this.previous)) {
                    return;
                }
                unsetPreviousByType(this.previous);
            }
            setPreviousByType(task);
        }
        this.previous = task;
    }

    @Override
    public void setRawPrevious(PamirsTask task) {
        if (this.previous != null) {
            unsetPreviousByType(this.previous);
        }
        this.previous = task;
    }

    @Override
    public PamirsTask getNext() {
        return next;
    }

    @Override
    public void setNext(PamirsTask task) {
        if (task == null) {
            if (this.next == null) {
                return;
            } else {
                unsetNextByType(this.next);
            }
        } else {
            if (this.next != null) {
                if (task.equals(this.next)) {
                    return;
                }
                unsetNextByType(this.next);
            }
            setNextByType(task);
        }
        this.next = task;
    }

    @Override
    public void setRawNext(PamirsTask task) {
        if (this.next != null) {
            unsetNextByType(this.next);
        }
        this.next = task;
    }

    protected void setPreviousByType(PamirsTask task) {
        if (task instanceof PamirsRelationTask) {
            ((PamirsRelationTask) task).addRawChild(this);
        }
        if (task instanceof PamirsChainTask) {
            ((PamirsChainTask) task).setRawNext(this);
        }
    }

    protected void unsetPreviousByType(PamirsTask task) {
        if (task instanceof PamirsRelationTask) {
            ((PamirsRelationTask) task).removeRawChild(this);
        }
        if (task instanceof PamirsChainTask) {
            ((PamirsChainTask) task).setRawNext(null);
        }
    }

    protected void setNextByType(PamirsTask task) {
        if (task instanceof PamirsRelationTask) {
            ((PamirsRelationTask) task).setRawParent(this);
        }
        if (task instanceof PamirsChainTask) {
            ((PamirsChainTask) task).setRawPrevious(this);
        }
    }

    protected void unsetNextByType(PamirsTask task) {
        if (task instanceof PamirsRelationTask) {
            ((PamirsRelationTask) task).setRawParent(null);
        }
        if (task instanceof PamirsChainTask) {
            ((PamirsChainTask) task).setRawPrevious(null);
        }
    }
}
