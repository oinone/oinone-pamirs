package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.dsl.model.TxConfig;
import pro.shushi.pamirs.meta.dsl.utils.StringUtils;

public class Tx {

    private String module;

    private Integer isolation;

    private Integer propagation;

    private Integer timeout;

    private Boolean readOnly;

    private String rollbackFor;

    private String noRollbackFor;

    public TxConfig tx() {
        TxConfig txConfig = null;
        if(!StringUtils.isBlank(module)){
            if(null != isolation || null != propagation || null != timeout || null != readOnly || null != rollbackFor || null != noRollbackFor){
                txConfig = new TxConfig();
                txConfig.setModuleName(module);
                txConfig.setIsolation(isolation);
                txConfig.setPropagation(propagation);
                txConfig.setTimeout(timeout);
                txConfig.setReadOnly(readOnly);
                txConfig.setRollbackFor(rollbackFor);
                txConfig.setNoRollbackFor(noRollbackFor);
            }
        }
        return txConfig;
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
}
