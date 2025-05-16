package pro.shushi.pamirs.meta.api.dto.fun;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.domain.fun.Argument;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.FunctionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 函数方法
 *
 * @author d
 * @version 2019-04-26
 */
@Data
public class Function implements Serializable {

    private static final long serialVersionUID = 532251020363685453L;

    private FunctionDefinition functionDefinition;

    private transient ScriptType scriptType;

    public Function() {
        super();
        this.setFunctionDefinition(new FunctionDefinition());
    }

    public Function(FunctionDefinition functionDefinition) {
        super();
        this.setFunctionDefinition(functionDefinition);
    }

    public String fetchDslKey() {
        return getFunctionDefinition().getNamespace() + CharacterConstants.SEPARATOR_DOT + getFunctionDefinition().getFun();
    }

    public Function setScriptType(ScriptType scriptType) {
        this.scriptType = scriptType;
        return this;
    }

    public ScriptType getScriptType() {
        if (null != scriptType) {
            return scriptType;
        }
        switch (functionDefinition.getLanguage()) {
            case JAVA: {
                return null;
            }
            case DSL: {
                return ScriptType.DSL;
            }
            case MVEL: {
                return ScriptType.EL;
            }
            case EXPRESSION: {
                return ScriptType.SCRIPT;
            }
            case JS: {
                return ScriptType.JS;
            }
            case GROOVY: {
                return ScriptType.GROOVY;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return getFunctionDefinition().getDisplayName();
    }

    public Function setDisplayName(String displayName) {
        getFunctionDefinition().setDisplayName(displayName);
        return this;
    }

    public String getModule() {
        return getFunctionDefinition().getModule();
    }

    public Function setModule(String name) {
        getFunctionDefinition().setModule(name);
        return this;
    }

    public String getName() {
        return getFunctionDefinition().getName();
    }

    public Function setName(String name) {
        getFunctionDefinition().setName(name);
        return this;
    }

    public String getNamespace() {
        return StringUtils.isBlank(functionDefinition.getNamespace()) ? NamespaceConstants.pamirs : functionDefinition.getNamespace();
    }

    public Function setNamespace(String namespace) {
        getFunctionDefinition().setNamespace(namespace);
        return this;
    }

    public String getFun() {
        return getFunctionDefinition().getFun();
    }

    public Function setFun(String fun) {
        getFunctionDefinition().setFun(fun);
        return this;
    }

    public String getSummary() {
        return getFunctionDefinition().getDescription();
    }

    public Function setSummary(String summary) {
        getFunctionDefinition().setDescription(summary);
        return this;
    }

    public String getClazz() {
        return getFunctionDefinition().getClazz();
    }

    public Function setClazz(String clazz) {
        getFunctionDefinition().setClazz(clazz);
        return this;
    }

    public String getMethod() {
        return getFunctionDefinition().getMethod();
    }

    public Function setMethod(String method) {
        getFunctionDefinition().setMethod(method);
        return this;
    }

    public List<Arg> getArguments() {
        return FunctionUtils.fetchArgumentList(functionDefinition.getArgumentList());
    }

    public Function setArguments(List<Argument> arguments) {
        getFunctionDefinition().setArgumentList(arguments);
        return this;
    }

    public VarType getReturnType() {
        return FunctionUtils.fetchReturnType(functionDefinition.getReturnType());
    }

    public String getCodes() {
        return getFunctionDefinition().getCodes();
    }

    public Function setCodes(String codes) {
        getFunctionDefinition().setCodes(codes);
        return this;
    }

    public String getBeanName() {
        return getFunctionDefinition().getBeanName();
    }

    public Function setBeanName(String beanName) {
        getFunctionDefinition().setBeanName(beanName);
        return this;
    }

    public List<FunctionTypeEnum> getType() {
        return getFunctionDefinition().getType();
    }

    public Function setType(List<FunctionTypeEnum> type) {
        getFunctionDefinition().setType(type);
        return this;
    }

    public FunctionSourceEnum getSource() {
        return getFunctionDefinition().getSource();
    }

    public Function setSource(FunctionSourceEnum source) {
        getFunctionDefinition().setSource(source);
        return this;
    }

    public List<FunctionOpenEnum> getOpen() {
        return getFunctionDefinition().getOpenLevel();
    }

    public Function setOpen(List<FunctionOpenEnum> open) {
        getFunctionDefinition().setOpenLevel(open);
        return this;
    }

    public Boolean isDataManager() {
        return getFunctionDefinition().getDataManager();
    }

    public Function setDataManager(Boolean managed) {
        getFunctionDefinition().setDataManager(managed);
        return this;
    }

    public String getGroup() {
        return getFunctionDefinition().getGroup();
    }

    public Function setGroup(String group) {
        getFunctionDefinition().setGroup(group);
        return this;
    }

    public String getVersion() {
        return getFunctionDefinition().getVersion();
    }

    public Function setVersion(String version) {
        getFunctionDefinition().setVersion(version);
        return this;
    }

    public Integer getTimeout() {
        return getFunctionDefinition().getTimeout();
    }

    public Function setTimeout(Integer timeout) {
        getFunctionDefinition().setTimeout(timeout);
        return this;
    }

    public Integer getRetries() {
        return getFunctionDefinition().getRetries();
    }

    public Function setRetries(Integer retries) {
        getFunctionDefinition().setRetries(retries);
        return this;
    }

    public Boolean getLongPolling() {
        return getFunctionDefinition().getIsLongPolling();
    }

    public Function setLongPolling(Boolean longPolling) {
        getFunctionDefinition().setIsLongPolling(longPolling);
        return this;
    }

    public String getLongPollingKey() {
        return getFunctionDefinition().getLongPollingKey();
    }

    public Function setLongPollingKey(String longPollingKey) {
        getFunctionDefinition().setLongPollingKey(longPollingKey);
        return this;
    }

    public Integer getLongPollingTimeout() {
        return getFunctionDefinition().getLongPollingTimeout();
    }

    public Function setLongPollingTimeout(Integer longPollingTimeout) {
        getFunctionDefinition().setLongPollingTimeout(longPollingTimeout);
        return this;
    }

    public Long getBitOptions() {
        return getFunctionDefinition().getBitOptions();
    }

    public Function setBitOptions(Long bitOptions) {
        getFunctionDefinition().setBitOptions(bitOptions);
        return this;
    }

}
