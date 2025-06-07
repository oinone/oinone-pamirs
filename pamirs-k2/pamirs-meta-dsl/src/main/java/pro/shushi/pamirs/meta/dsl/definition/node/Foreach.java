package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("foreach")
public class Foreach extends Code {

    public Foreach(String id) {
        super();
        this.setId(id);
    }

    @XStreamAsAttribute
    private String list;

    @XStreamAsAttribute
    private String start;

    @XStreamAsAttribute
    private String end;

    private To each;

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public To getEach() {
        return each;
    }

    public void setEach(To each) {
        this.each = each;
    }

    @Override
    public String toString() {
        return "foreach [id=" + this.getId() + ", list=" + list + ", start=" + start + ", end=" + end + ", each=" + each + "]";
    }

}
