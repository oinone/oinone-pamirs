package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.IEipExceptionPredict;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipProcessor;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.pamirs.DefaultExceptionPredictFunction;
import pro.shushi.pamirs.eip.api.processor.DefaultExceptionProcessor;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 异常参数处理器
 *
 * @author Adamancy Zhang at 19:18 on 2021-06-09
 */
@Base
@Model.model(EipExceptionParamProcessor.MODEL_MODEL)
@Model(displayName = "异常参数处理器", labelFields = "name")
public class EipExceptionParamProcessor extends AbstractEipParamConverterProcessor implements IEipExceptionParamProcessor<SuperMap> {

    private static final long serialVersionUID = 9078678674517849764L;

    public static final String MODEL_MODEL = "pamirs.eip.EipExceptionParamProcessor";

    @JSONField(serialize = false)
    @Field.one2one
    @Field.Relation(store = false)
    @Field(displayName = "集成接口", store = NullableBoolEnum.FALSE)
    private EipIntegrationInterface integrationInterface;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"exceptionPredictNamespace", "exceptionPredictFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "异常判定函数")
    private FunctionDefinition exceptionPredictFunction;

    @Base
    @Field.String
    @Field(displayName = "异常判定函数命名空间")
    private String exceptionPredictNamespace;

    @Base
    @Field.String
    @Field(displayName = "异常判定函数名称")
    private String exceptionPredictFun;

    @JSONField(serialize = false)
    @Override
    public IEipProcessor<IEipIntegrationInterface<SuperMap>> getProcessor() {
        return new DefaultExceptionProcessor(getIntegrationInterface());
    }

    @JSONField(serialize = false)
    @Override
    public IEipExceptionPredict<SuperMap> getExceptionPredict() {
        String namespace = getExceptionPredictNamespace();
        String fun = getExceptionPredictFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun)) {
            return new DefaultExceptionPredictFunction<>(namespace, fun);
        }
        return EipFunctionConstant.DEFAULT_EXCEPTION_PREDICT;
    }

    @JSONField(serialize = false)
    @Override
    public IEipExceptionParamProcessor<SuperMap> afterProperty() {
        return this;
    }
}
