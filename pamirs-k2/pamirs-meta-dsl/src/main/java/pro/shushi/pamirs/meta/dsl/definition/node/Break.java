package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("break")
public class Break extends Code {
    public static final Integer INDEX_OPT_TYPE = 0;

    public Break(String id) {
        super();
        this.setId(id);
    }

    @XStreamOmitField
    private String foreachId;

    public String getForeachId() {
        return foreachId;
    }

    public void setForeachId(String foreachId) {
        this.foreachId = foreachId;
    }

    @Override
    public String toString() {
        return "Break [id=" + this.getId() + ",foreachId=" + this.getForeachId() + "]";
    }

}
