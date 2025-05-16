package pro.shushi.pamirs.meta.dsl.model;

public class TxConfig {

    private String moduleName;

    private Integer isolation;

    private Integer propagation;

    private Integer timeout;

    private Boolean readOnly;

    private String rollbackFor;

    private String noRollbackFor;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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
}
