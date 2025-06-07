package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

public class Code {

    @XStreamAsAttribute
    private String id;

    @XStreamAsAttribute
    private String desc;

    @XStreamAsAttribute
    private To ex;

    @XStreamImplicit(itemFieldName = "to")
    private List<To> tos;

    @XStreamAsAttribute
    private String module;

    @XStreamAsAttribute
    private Integer isolation;

    @XStreamAsAttribute
    private Integer propagation;

    @XStreamAsAttribute
    private Integer timeout;

    @XStreamAsAttribute
    private Boolean readOnly;

    @XStreamAsAttribute
    private String rollbackFor;

    @XStreamAsAttribute
    private String noRollbackFor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public To getEx() {
        return ex;
    }

    public void setEx(To ex) {
        this.ex = ex;
    }

    public List<To> getTos() {
        return tos;
    }

    public void setTos(List<To> tos) {
        this.tos = tos;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getIsolation() {
        return isolation;
    }

    public void setIsolation(Integer isolation) {
        this.isolation = isolation;
    }

    public Integer getPropagation() {
        return propagation;
    }

    public void setPropagation(Integer propagation) {
        this.propagation = propagation;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getRollbackFor() {
        return rollbackFor;
    }

    public void setRollbackFor(String rollbackFor) {
        this.rollbackFor = rollbackFor;
    }

    public String getNoRollbackFor() {
        return noRollbackFor;
    }

    public void setNoRollbackFor(String noRollbackFor) {
        this.noRollbackFor = noRollbackFor;
    }

    @Override
    public String toString() {
        return "Code [id=" + id + ", description=" + desc + ", tos=" + tos + ", ex=" + ex + "]";
    }

}
