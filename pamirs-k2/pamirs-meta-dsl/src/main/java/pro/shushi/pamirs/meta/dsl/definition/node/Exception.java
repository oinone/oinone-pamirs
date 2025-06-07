package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("exception")
public class Exception extends Code {

    @XStreamImplicit(itemFieldName = "catch")
    private List<To> tos;

    public Exception(String id) {
        super();
        this.setId(id);
    }

    public List<To> getTos() {
        return this.tos;
    }

    @Override
    public void setTos(List<To> tos) {
        this.tos = tos;
    }

    @Override
    public String toString() {
        return "Exception [id=" + this.getId() + ", catch=" + tos + "]";
    }

}
