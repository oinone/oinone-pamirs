package pro.shushi.pamirs.eip.api;

import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;

import java.io.Serializable;
import java.util.Map;

public interface IEipConvertParam<T> extends Serializable {

    String getInParam();

    String getOutParam();

    Object getDefaultValue();

    Boolean getRequired();

    Boolean getIsKeepNull();

    Integer getSize();

    Map<String, String> getConvertMap();

    String getConvertMapValue(String key);

    ParamTypeEnum getInParamType();

    ParamTypeEnum getOutParamType();

    ContextTypeEnum getOriginContextType();

    ContextTypeEnum getTargetContextType();

    IEipParamConverterCallback<T> getParamConverterCallback();

    IEipConvertParam<T> clone(String inParam, String outParam);
}
