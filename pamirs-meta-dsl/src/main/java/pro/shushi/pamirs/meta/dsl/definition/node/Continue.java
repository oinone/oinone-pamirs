package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("continue")
public class Continue extends Code {
    public static final Integer INDEX_OPT_TYPE = 1;

    public Continue(String id) {
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
        return "Continue [id=" + this.getId() + ",foreachId=" + this.getForeachId() + "]";
    }

}
