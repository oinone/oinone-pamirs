package pro.shushi.pamirs.middleware.schedule.core.function.model;

import pro.shushi.pamirs.middleware.schedule.core.function.FunctionReturnResultConverter;

/**
 * function definition
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 09:52
 */
public class FunctionDefinition<T> {

    private final String interfaceName;

    private final String methodName;

    private final String[] parameterTypes;

    private String group;

    private String version;

    private Integer timeout = 5000;

    private FunctionReturnResultConverter<T> returnResultConverter;

    public FunctionDefinition(String interfaceName, String methodName, String[] parameterTypes) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public FunctionReturnResultConverter<T> getReturnResultConverter() {
        return returnResultConverter;
    }

    public FunctionDefinition<T> setReturnResultConverter(FunctionReturnResultConverter<T> returnResultConverter) {
        this.returnResultConverter = returnResultConverter;
        return this;
    }
}
