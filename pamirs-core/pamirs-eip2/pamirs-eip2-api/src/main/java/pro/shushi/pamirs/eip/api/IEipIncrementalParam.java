package pro.shushi.pamirs.eip.api;

import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;

public interface IEipIncrementalParam {

    String getInterfaceName();

    String getTags();

    void setTags(String tags);

    String getInParam();

    String getOutParam();

    ContextTypeEnum getOriginContextType();

    ContextTypeEnum getTargetContextType();

    Object getInitializationValue();

    Object getCurrentValue();

    void setCurrentValue(Object value);
}
