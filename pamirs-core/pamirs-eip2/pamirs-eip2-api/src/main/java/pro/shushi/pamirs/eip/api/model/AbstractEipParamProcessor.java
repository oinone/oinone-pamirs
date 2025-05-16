package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.pamirs.DefaultAuthenticationProcessorFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultDeserializationFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultInOutConverterFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultSerializableFunction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 参数处理器的抽象基类
 *
 * @author Adamancy Zhang at 19:16 on 2021-06-09
 */
@Base
@Model.model("pamirs.eip.AbstractEipParamProcessor")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "参数处理器的抽象基类", labelFields = "name")
public abstract class AbstractEipParamProcessor extends AbstractEipParamConverterProcessor implements IEipParamProcessor<SuperMap> {

    private static final long serialVersionUID = 136865480985182430L;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"authenticationProcessorNamespace", "authenticationProcessorFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "认证处理器函数")
    private FunctionDefinition authenticationProcessorFunction;

    @Base
    @Field.String
    @Field(displayName = "认证处理器函数命名空间")
    private String authenticationProcessorNamespace;

    @Base
    @Field.String
    @Field(displayName = "认证处理器函数名称")
    private String authenticationProcessorFun;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"signatureNamespace", "signatureFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "验签处理器函数")
    private FunctionDefinition signatureFunction;

    @Base
    @Field.String
    @Field(displayName = "验签函数命名空间")
    private String signatureNamespace;

    @Base
    @Field.String
    @Field(displayName = "验签函数名称")
    private String signatureFun;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"serializableNamespace", "serializableFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "序列化函数")
    private FunctionDefinition serializableFunction;

    @Base
    @Field.String
    @Field(displayName = "序列化函数命名空间")
    private String serializableNamespace;

    @Base
    @Field.String
    @Field(displayName = "序列化函数名称")
    private String serializableFun;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"deserializationNamespace", "deserializationFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "反序列化函数")
    private FunctionDefinition deserializationFunction;

    @Base
    @Field.String
    @Field(displayName = "反序列化函数命名空间")
    private String deserializationNamespace;

    @Base
    @Field.String
    @Field(displayName = "反序列化函数名称")
    private String deserializationFun;

    @Base
    @Field.String
    @Field(displayName = "最终结果键值")
    private String finalResultKey;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"inOutConverterNamespace", "inOutConverterFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "输入输出转换器函数")
    private FunctionDefinition inOutConverterFunction;

    @Base
    @Field.String
    @Field(displayName = "输入输出转换器函数命名空间")
    private String inOutConverterNamespace;

    @Base
    @Field.String
    @Field(displayName = "输入输出转换器函数名称")
    private String inOutConverterFun;

    @JSONField(serialize = false)
    @Override
    public IEipAuthenticationProcessor<SuperMap> getAuthenticationProcessor() {
        return new DefaultAuthenticationProcessorFunction(getAuthenticationProcessorNamespace(), getAuthenticationProcessorFun(),
                getSignatureNamespace(), getSignatureFun());
    }

    @JSONField(serialize = false)
    @Override
    public IEipSerializable<SuperMap> getSerializable() {
        String namespace = getSerializableNamespace();
        String fun = getSerializableFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun))
            return new DefaultSerializableFunction<>(namespace, fun);
        return getDefaultSerializable();
    }

    @JSONField(serialize = false)
    @Override
    public IEipDeserialization<SuperMap> getDeserialization() {
        String namespace = getDeserializationNamespace();
        String fun = getDeserializationFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun))
            return new DefaultDeserializationFunction<>(namespace, fun);
        return getDefaultDeserialization();
    }

    @JSONField(serialize = false)
    @Override
    public IEipInOutConverter getInOutConverter() {
        String namespace = getInOutConverterNamespace();
        String fun = getInOutConverterFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun))
            return new DefaultInOutConverterFunction(namespace, fun);
        return getDefaultInOutConverter();
    }

    protected abstract IEipSerializable<SuperMap> getDefaultSerializable();

    protected abstract IEipDeserialization<SuperMap> getDefaultDeserialization();

    protected abstract IEipInOutConverter getDefaultInOutConverter();
}
