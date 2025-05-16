package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.ParamProcessorTypeEnum;
import pro.shushi.pamirs.eip.api.processor.DefaultRequestProcessor;
import pro.shushi.pamirs.eip.api.processor.DefaultResponseProcessor;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

/**
 * 参数处理器
 *
 * @author Adamancy Zhang at 19:23 on 2021-06-09
 */
@Base
@Model.model(EipParamProcessor.MODEL_MODEL)
@Model(displayName = "参数处理器", labelFields = "name")
public class EipParamProcessor extends AbstractEipParamProcessor implements IEipParamProcessor<SuperMap> {

    private static final long serialVersionUID = 6396012234315647984L;

    public static final String MODEL_MODEL = "pamirs.eip.EipParamProcessor";

    @JSONField(serialize = false)
    @Field.one2one
    @Field.Relation(store = false)
    @Field(displayName = "集成接口", store = NullableBoolEnum.FALSE)
    private EipIntegrationInterface integrationInterface;

    @Field.Enum
    @Field(displayName = "参数处理器类型", required = true)
    private ParamProcessorTypeEnum type;

    @JSONField(serialize = false)
    @Override
    public IEipProcessor<IEipIntegrationInterface<SuperMap>> getProcessor() {
        EipIntegrationInterface integrationInterface = getIntegrationInterface();
        ParamProcessorTypeEnum type = getType();
        switch (type) {
            case REQUEST:
                return new DefaultRequestProcessor(integrationInterface);
            case RESPONSE:
                return new DefaultResponseProcessor(integrationInterface);
            default:
                throw PamirsException.construct(EipExpEnumerate.EIP_PROCESSOR_TYPE_INVALID).errThrow();
        }
    }

    @Override
    public IEipParamProcessor<SuperMap> afterProperty() {
        return this;
    }

    @JSONField(serialize = false)
    @Override
    protected IEipSerializable<SuperMap> getDefaultSerializable() {
        return EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE;
    }

    @JSONField(serialize = false)
    @Override
    protected IEipDeserialization<SuperMap> getDefaultDeserialization() {
        return EipFunctionConstant.DEFAULT_JSON_SERIALIZABLE;
    }

    @JSONField(serialize = false)
    @Override
    protected IEipInOutConverter getDefaultInOutConverter() {
        return EipFunctionConstant.DEFAULT_IN_OUT_CONVERTER;
    }
}
