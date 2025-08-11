package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.IEipParamConverterCallback;
import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.HttpParamTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.api.pamirs.DefaultParamConverterCallbackFunction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数定义
 *
 * @author Adamancy Zhang at 19:17 on 2021-06-09
 */
@Base
@Model.model(EipConvertParam.MODEL_MODEL)
@Model(displayName = "参数定义", labelFields = "name")
public class EipConvertParam extends TransientModel implements IEipConvertParam<SuperMap> {

    private static final long serialVersionUID = 7281795820119917205L;

    public static final String MODEL_MODEL = "pamirs.eip.EipConvertParam";

    public EipConvertParam() {
    }

    public EipConvertParam(String inParam, String outParam) {
        this.setInParam(inParam);
        this.setOutParam(outParam);
    }

    @Base
    @Field.String
    @Field(displayName = "入参", required = true)
    private String inParam;

    @Base
    @Field.String
    @Field(displayName = "出参", required = true)
    private String outParam;

    @Base
    @Field.String
    @Field(displayName = "备注信息", required = true)
    private String desc;

    @Base
    @Field.String
    @Field(displayName = "默认值")
    private String defaultValue;

    @Base
    @Field.Boolean
    @Field(displayName = "是否必填", defaultValue = "false", required = true)
    private Boolean required;

    @Base
    @Field.Boolean
    @Field(displayName = "是否保留空值", defaultValue = "false", required = true)
    private Boolean isKeepNull;

    @Base
    @Field.Integer
    @Field(displayName = "字段长度")
    private Integer size;

    @Base
    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "映射列表", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private List<EipMappingParameter> mappingParameterList;

    @Base
    @Field.Enum
    @Field(displayName = "入参类型", defaultValue = "OBJECT", required = true)
    private ParamTypeEnum inParamType;

    @Base
    @Field.Enum
    @Field(displayName = "出参类型", defaultValue = "OBJECT", required = true)
    private ParamTypeEnum outParamType;

    @Base
    @Field.Enum
    @Field(displayName = "入参来源类型", defaultValue = "INTERFACE", required = true)
    private ContextTypeEnum originContextType;

    @Base
    @Field.Enum
    @Field(displayName = "出参来源类型", defaultValue = "INTERFACE", required = true)
    private ContextTypeEnum targetContextType;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"callbackNamespace", "callbackFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "参数转换回调函数")
    private FunctionDefinition callbackFunction;

    @Base
    @Field.String
    @Field(displayName = "参数转换回调函数命名空间")
    private String callbackNamespace;

    @Base
    @Field.String
    @Field(displayName = "参数转换回调函数名称")
    private String callbackFun;

    /**
     * eip参数转换不关心参数需要从哪放在哪，inParam和outParam的结构化处理要在view中进行识别和处理
     *
     * @deprecated 2.3.0
     */
    @Deprecated
    @Base
    @Field.Enum
    @Field(displayName = "请求参数类型")
    private HttpParamTypeEnum httpParamTypeEnum;

    @JSONField(serialize = false)
    @Override
    public Map<String, String> getConvertMap() {
        Map<String, String> mapping = new HashMap<>(16);
        List<EipMappingParameter> mappingParameters = getMappingParameterList();
        if (CollectionUtils.isEmpty(mappingParameters)) {
            return mapping;
        }
        for (EipMappingParameter mappingParameter : mappingParameters) {
            mapping.put(mappingParameter.getFrom(), mappingParameter.getTo());
        }
        return mapping;
    }

    @Override
    public String getConvertMapValue(String key) {
        List<EipMappingParameter> mappingParameters = getMappingParameterList();
        if (CollectionUtils.isEmpty(mappingParameters)) {
            return null;
        }
        for (EipMappingParameter mappingParameter : mappingParameters) {
            if (key.equals(mappingParameter.getFrom())) {
                return mappingParameter.getTo();
            }
        }
        return null;
    }

    @JSONField(serialize = false)
    @Override
    public IEipParamConverterCallback<SuperMap> getParamConverterCallback() {
        String namespace = getCallbackNamespace();
        String fun = getCallbackFun();
        if (StringUtils.isNoneBlank(namespace, fun)) {
            return new DefaultParamConverterCallbackFunction<>(namespace, fun);
        }
        return null;
    }

    @Override
    public IEipConvertParam<SuperMap> clone(String inParam, String outParam) {
        List<EipMappingParameter> mappingParameters = getMappingParameterList();
        List<EipMappingParameter> cloneMappingParameters = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mappingParameters)) {
            for (EipMappingParameter mappingParameter : mappingParameters) {
                cloneMappingParameters.add(mappingParameter.clone());
            }
        }
        return new EipConvertParam()
                .setInParam(inParam)
                .setOutParam(outParam)
                .setDefaultValue(getDefaultValue())
                .setRequired(getRequired())
                .setSize(getSize())
                .setMappingParameterList(cloneMappingParameters)
                .setOriginContextType(getOriginContextType())
                .setTargetContextType(getTargetContextType());
    }
}
