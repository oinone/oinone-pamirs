package pro.shushi.pamirs.meta.api.core.orm.template.context;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.orm.path.ClientExecutionPath;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.io.Serializable;

/**
 * 模型计算上下文
 * 2021/1/6 11:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ModelComputeContext implements Serializable {

    private static final long serialVersionUID = 433094202829945953L;

    private String fun;

    private ClientExecutionPath path;

    public ModelComputeContext initPath() {
        this.setPath(PamirsSession.getMessageHub().getPath());
        return this;
    }

    public ModelComputeContext segment(int segment) {
        if (null == this.getPath()) {
            return this;
        }
        this.setPath(this.getPath().segment(segment));
        PamirsSession.getMessageHub().setPath(this.getPath());
        return this;
    }

    public ModelComputeContext segment(String segment) {
        if (null == this.getPath()) {
            return this;
        }
        this.setPath(this.getPath().segment(segment));
        PamirsSession.getMessageHub().setPath(this.getPath());
        return this;
    }

    public ModelComputeContext dropSegment() {
        if (null == this.getPath()) {
            return this;
        }
        PamirsSession.getMessageHub().setPath(this.getPath().dropSegment());
        this.setPath(PamirsSession.getMessageHub().getPath());
        return this;
    }

}
