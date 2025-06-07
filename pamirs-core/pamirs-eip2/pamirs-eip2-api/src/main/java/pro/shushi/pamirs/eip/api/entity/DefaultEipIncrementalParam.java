package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.IEipIncrementalParam;
import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;

/**
 * @author Adamancy Zhang at 20:41 on 2021-02-27
 */
public class DefaultEipIncrementalParam implements IEipIncrementalParam {

    private final String interfaceName;

    private String tags;

    private final String inParam;

    private final String outParam;

    private final ContextTypeEnum originContextType;

    private ContextTypeEnum targetContextType;

    private final Object initializationValue;

    private Object currentValue;

    public DefaultEipIncrementalParam(String interfaceName, String inParam, String outParam, Object initializationValue) {
        this(interfaceName, inParam, outParam, initializationValue, ContextTypeEnum.EXECUTOR, ContextTypeEnum.INTERFACE);
    }

    public DefaultEipIncrementalParam(String interfaceName, String inParam, String outParam, Object initializationValue, ContextTypeEnum originContextType) {
        this(interfaceName, inParam, outParam, initializationValue, originContextType, ContextTypeEnum.INTERFACE);
    }

    public DefaultEipIncrementalParam(String interfaceName, String inParam, String outParam, Object initializationValue, ContextTypeEnum originContextType, ContextTypeEnum targetContextType) {
        this.interfaceName = interfaceName;
        this.inParam = inParam;
        this.outParam = outParam;
        this.initializationValue = initializationValue;
        this.originContextType = originContextType;
        this.targetContextType = targetContextType;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public String getTags() {
        return tags;
    }

    @Override
    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String getInParam() {
        return inParam;
    }

    @Override
    public String getOutParam() {
        return outParam;
    }

    @Override
    public ContextTypeEnum getOriginContextType() {
        return originContextType;
    }

    @Override
    public ContextTypeEnum getTargetContextType() {
        return targetContextType;
    }

    @Override
    public Object getInitializationValue() {
        return initializationValue;
    }

    @Override
    public Object getCurrentValue() {
        return currentValue;
    }

    @Override
    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }
}
