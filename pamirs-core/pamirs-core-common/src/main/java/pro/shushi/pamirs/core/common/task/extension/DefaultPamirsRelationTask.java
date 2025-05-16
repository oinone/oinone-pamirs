package pro.shushi.pamirs.core.common.task.extension;

import pro.shushi.pamirs.core.common.task.PamirsChainTask;
import pro.shushi.pamirs.core.common.task.PamirsRelationTask;
import pro.shushi.pamirs.core.common.task.PamirsTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 默认关联任务实现
 *
 * @author Adamancy Zhang on 2021-05-09 14:28
 */
public class DefaultPamirsRelationTask extends DefaultPamirsTask implements PamirsRelationTask {

    private PamirsTask parent;

    private final List<PamirsTask> children = new ArrayList<>();

    public DefaultPamirsRelationTask(String signature) {
        this(signature, null);
    }

    public DefaultPamirsRelationTask(String signature, PamirsTask parent) {
        super(signature);
        setParent(parent);
    }

    @Override
    public PamirsTask getParent() {
        return parent;
    }

    @Override
    public void setParent(PamirsTask task) {
        if (task == null) {
            if (this.parent == null) {
                return;
            } else {
                removeChildByType(this.parent);
            }
        } else {
            if (this.parent != null) {
                if (task.equals(this.parent)) {
                    return;
                }
                removeChildByType(this.parent);
            }
            addChildByType(task);
        }
        this.parent = task;
    }

    @Override
    public void setRawParent(PamirsTask rawTask) {
        if (this.parent != null) {
            removeChildByType(rawTask);
        }
        this.parent = rawTask;
    }

    @Override
    public List<PamirsTask> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void addChild(PamirsTask task) {
        setParentByType(task);
        children.add(task);
    }

    @Override
    public void addRawChild(PamirsTask task) {
        children.add(task);
    }

    @Override
    public void removeChild(PamirsTask task) {
        unsetParentByType(task);
        children.remove(task);
    }

    @Override
    public void removeRawChild(PamirsTask task) {
        children.remove(task);
    }

    protected void addChildByType(PamirsTask parent) {
        if (parent instanceof PamirsRelationTask) {
            ((PamirsRelationTask) parent).addRawChild(this);
        }
        if (parent instanceof PamirsChainTask) {
            ((PamirsChainTask) parent).setRawNext(this);
        }
    }

    protected void removeChildByType(PamirsTask parent) {
        if (parent instanceof PamirsRelationTask) {
            ((PamirsRelationTask) parent).removeRawChild(this);
        }
        if (parent instanceof PamirsChainTask) {
            ((PamirsChainTask) parent).setRawNext(null);
        }
    }

    protected void setParentByType(PamirsTask child) {
        if (child instanceof PamirsRelationTask) {
            ((PamirsRelationTask) child).setRawParent(this);
        }
        if (child instanceof PamirsChainTask) {
            ((PamirsChainTask) child).setRawPrevious(this);
        }
    }

    protected void unsetParentByType(PamirsTask child) {
        if (child instanceof PamirsRelationTask) {
            ((PamirsRelationTask) child).setRawParent(null);
        }
        if (child instanceof PamirsChainTask) {
            ((PamirsChainTask) child).setRawPrevious(null);
        }
    }
}
