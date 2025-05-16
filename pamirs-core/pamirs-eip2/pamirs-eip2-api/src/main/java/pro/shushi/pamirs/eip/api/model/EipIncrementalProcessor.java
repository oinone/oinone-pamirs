package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.pamirs.DefaultConverterFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultIncrementalParamConverterCallbackFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultIncrementalParamConverterFunction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 增量处理器
 *
 * @author Adamancy Zhang at 19:18 on 2021-06-09
 */
@Base
@Model.model(EipIncrementalProcessor.MODEL_MODEL)
@Model(displayName = "增量处理器", labelFields = "name")
public class EipIncrementalProcessor extends TransientModel implements IEipIncrementalProcessor<SuperMap> {

    private static final long serialVersionUID = -2078651681557751494L;

    public static final String MODEL_MODEL = "pamirs.eip.EipIncrementalProcessor";

    @JSONField(serialize = false)
    @Field.one2one
    @Field.Relation(store = false)
    @Field(displayName = "集成接口", store = NullableBoolEnum.FALSE)
    private EipIntegrationInterface integrationInterface;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"converterNamespace", "converterFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "自定义转换器函数")
    private FunctionDefinition converterFunction;

    @Base
    @Field.String
    @Field(displayName = "自定义转换器函数命名空间")
    private String converterNamespace;

    @Base
    @Field.String
    @Field(displayName = "自定义转换器函数名称")
    private String converterFun;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"incrementalParamConverterNamespace", "incrementalParamConverterFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "增量参数回调函数")
    private FunctionDefinition incrementalParamConverterFunction;

    @Base
    @Field.String
    @Field(displayName = "增量参数回调函数命名空间")
    private String incrementalParamConverterNamespace;

    @Base
    @Field.String
    @Field(displayName = "增量参数回调函数名称")
    private String incrementalParamConverterFun;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"incrementalParamConverterCallbackNamespace", "incrementalParamConverterCallbackFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "参数转换回调函数")
    private FunctionDefinition incrementalParamConverterCallbackFunction;

    @Base
    @Field.String
    @Field(displayName = "参数转换回调函数命名空间")
    private String incrementalParamConverterCallbackNamespace;

    @Base
    @Field.String
    @Field(displayName = "参数转换回调函数名称")
    private String incrementalParamConverterCallbackFun;

    @JSONField(serialize = false)
    @Override
    public IEipConverter<SuperMap> getConverter() {
        String namespace = getConverterNamespace();
        String fun = getConverterFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun)) {
            return new DefaultConverterFunction<>(namespace, fun);
        }
        return null;
    }

    @JSONField(serialize = false)
    @Override
    public IEipIncrementalParamConverter<SuperMap> getIncrementalParamConverter() {
        String namespace = getIncrementalParamConverterNamespace();
        String fun = getIncrementalParamConverterFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun)) {
            return new DefaultIncrementalParamConverterFunction<>(namespace, fun);
        }
        return EipFunctionConstant.DEFAULT_INCREMENTAL_PARAM_CONVERTER;
    }

    @JSONField(serialize = false)
    @Override
    public IEipIncrementalParamConverterCallback<SuperMap> getIncrementalParamConverterCallback() {
        String namespace = getIncrementalParamConverterNamespace();
        String fun = getIncrementalParamConverterFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun)) {
            return new DefaultIncrementalParamConverterCallbackFunction<>(namespace, fun);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @JSONField(serialize = false)
    @Override
    public List<IEipIncrementalParam> getIncrementalParamList(String tags) {
        List<IEipIncrementalParam> incrementalParamList = (List<IEipIncrementalParam>) (Object) getIntegrationInterface().getIncrementalList();
        if (StringUtils.isBlank(tags)) {
            return incrementalParamList;
        }
        List<IEipIncrementalParam> list = new ArrayList<>();
        if (incrementalParamList == null) {
            return list;
        }
        for (IEipIncrementalParam incrementalParam : incrementalParamList) {
            if (tags.equals(incrementalParam.getTags())) {
                list.add(incrementalParam);
            }
        }
        return list;
    }

    @Override
    public void commit(IEipContext<SuperMap> context) {
        Models.data().createOrUpdate(getIncrementalParamList((String) context.getExecutorContextValue(EipContextConstant.INCREMENTAL_TAGS_KEY)));
    }
}
