package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.entity.impl.DefaultEipParamConverter;
import pro.shushi.pamirs.eip.api.pamirs.DefaultConverterFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultParamConverterCallbackFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultParamConverterFunction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数转换处理器的抽象基类
 *
 * @author Adamancy Zhang at 19:16 on 2021-06-09
 */
@Base
@Model.model("pamirs.eip.AbstractEipParamConverterProcessor")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "参数转换处理器的抽象基类", labelFields = "name")
public abstract class AbstractEipParamConverterProcessor extends TransientModel implements IEipParamConverterProcessor<SuperMap> {

    private static final long serialVersionUID = 5944426156897511302L;

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
    @Field.Relation(relationFields = {"paramConverterNamespace", "paramConverterFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "参数转换函数")
    private FunctionDefinition paramConverterFunction;

    @Base
    @Field.String
    @Field(displayName = "参数转换函数命名空间")
    private String paramConverterNamespace;

    @Base
    @Field.String
    @Field(displayName = "参数转换函数名称")
    private String paramConverterFun;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"paramConverterCallbackNamespace", "paramConverterCallbackFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "参数转换回调函数")
    private FunctionDefinition paramConverterCallbackFunction;

    @Base
    @Field.String
    @Field(displayName = "参数转换回调函数命名空间")
    private String paramConverterCallbackNamespace;

    @Base
    @Field.String
    @Field(displayName = "参数转换回调函数名称")
    private String paramConverterCallbackFun;

    @Base
    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "转换参数列表", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private List<EipConvertParam> convertParamList;

    @SuppressWarnings("unchecked")
    @Override
    public List<IEipConvertParam<SuperMap>> getConvertParamList() {
        return (List<IEipConvertParam<SuperMap>>) super.get_d().get("convertParamList");
    }

    @SuppressWarnings("unchecked")
    public <P extends AbstractEipParamConverterProcessor> P addConvertParam(EipConvertParam convertParam) {
        List<EipConvertParam> convertParamList = (List<EipConvertParam>) (Object) getConvertParamList();
        if (getConvertParamList() == null) {
            convertParamList = new ArrayList<>();
            setConvertParamList(convertParamList);
        }
        convertParamList.add(convertParam);
        return (P) this;
    }

    @Override
    public IEipConverter<SuperMap> getConverter() {
        String namespace = getConverterNamespace();
        String fun = getConverterFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun)) {
            return new DefaultConverterFunction<>(namespace, fun);
        }
        return null;
    }

    @Override
    public IEipParamConverter<SuperMap> getParamConverter() {
        String namespace = getParamConverterNamespace();
        String fun = getParamConverterFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun)) {
            return new DefaultParamConverterFunction<>(namespace, fun);
        }
        return new DefaultEipParamConverter<>();
    }

    @Override
    public IEipParamConverterCallback<SuperMap> getParamConverterCallback() {
        String namespace = getParamConverterCallbackNamespace();
        String fun = getParamConverterCallbackFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun)) {
            return new DefaultParamConverterCallbackFunction<>(namespace, fun);
        }
        return null;
    }
}
