package pro.shushi.pamirs.core.common.function.context;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2021-01-22 11:22
 */
public class FunctionContext implements Serializable {

    private static final long serialVersionUID = 7922696737268207191L;

    private String namespace;

    private String fun;

    private String group;

    private String version;

    private Integer timeout;

    private Long directive;

    private List<ArgumentContext> argumentList;

    private Map<String, String> sessionContext;

    public String getNamespace() {
        return namespace;
    }

    public FunctionContext setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getFun() {
        return fun;
    }

    public FunctionContext setFun(String fun) {
        this.fun = fun;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public FunctionContext setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public FunctionContext setVersion(String version) {
        this.version = version;
        return this;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public FunctionContext setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    public Long getDirective() {
        return directive;
    }

    public FunctionContext setDirective(Long directive) {
        this.directive = directive;
        return this;
    }

    public List<ArgumentContext> getArgumentList() {
        return argumentList;
    }

    public FunctionContext setArgumentList(List<ArgumentContext> argumentList) {
        this.argumentList = argumentList;
        return this;
    }

    public Map<String, String> getSessionContext() {
        return sessionContext;
    }

    public FunctionContext setSessionContext(Map<String, String> sessionContext) {
        this.sessionContext = sessionContext;
        return this;
    }
}
