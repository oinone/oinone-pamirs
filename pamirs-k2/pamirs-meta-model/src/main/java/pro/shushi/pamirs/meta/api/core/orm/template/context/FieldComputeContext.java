package pro.shushi.pamirs.meta.api.core.orm.template.context;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 字段计算上下文
 * 2021/1/6 11:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class FieldComputeContext {

    private FieldComputeOp op;

    private ModelComputeContext totalContext;

    public FieldComputeContext initPath() {
        if (null == this.getTotalContext()) {
            return this;
        }
        this.getTotalContext().initPath();
        return this;
    }

    public FieldComputeContext segment(int segment) {
        if (null == this.getTotalContext()) {
            return this;
        }
        this.getTotalContext().segment(segment);
        return this;
    }

    public FieldComputeContext segment(String segment) {
        if (null == this.getTotalContext()) {
            return this;
        }
        this.getTotalContext().segment(segment);
        return this;
    }

    public FieldComputeContext dropSegment() {
        if (null == this.getTotalContext()) {
            return this;
        }
        this.getTotalContext().dropSegment();
        return this;
    }

}
