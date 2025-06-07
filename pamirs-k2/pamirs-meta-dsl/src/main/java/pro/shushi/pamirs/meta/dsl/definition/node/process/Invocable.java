package pro.shushi.pamirs.meta.dsl.definition.node.process;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Invocable {

    @XStreamAlias("method")
    @XStreamAsAttribute
    public String method;

    @XStreamAlias("args")
    @XStreamAsAttribute
    public String args;

    @XStreamAlias("result")
    @XStreamAsAttribute
    public String callback;

    public Invocable(String method, String args, String callback) {
        super();
        this.method = method;
        this.args = args;
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "Invokable [method=" + method + ", args=" + args + ", callback="
                + callback + "]";
    }

}
