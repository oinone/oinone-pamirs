package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.eip.api.enmu.ComponentTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * 组件定义
 *
 * @author Adamancy Zhang at 19:17 on 2021-06-09
 */
@Base
@Model.model(EipComponentDefinition.MODEL_MODEL)
@Model(displayName = "组件定义", labelFields = "name")
public class EipComponentDefinition extends TransientModel {

    private static final long serialVersionUID = 1979956715926267603L;

    public static final String MODEL_MODEL = "pamirs.eip.EipComponentDefinition";

    @Base
    @Field.String
    @Field(displayName = "组件类型", defaultValue = "NORMAL", required = true)
    private ComponentTypeEnum type;

    @Base
    @Field.Boolean
    @Field(displayName = "是否总是使用调用参数作为请求参数", defaultValue = "false", summary = "应用于组件为: 集成接口 的场景")
    private Boolean alwaysUsingRequestParams;

    //    filter相关
    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"filterNamespace", "filterFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "过滤函数", summary = "当组件类型为「FILTER」时必填")
    private FunctionDefinition filterFunction;

    @Base
    @Field.String
    @Field(displayName = "过滤函数命名空间")
    private String filterNamespace;

    @Base
    @Field.String
    @Field(displayName = "过滤函数名称")
    private String filterFun;

    @Base
    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "过滤条件对应的组件列表", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON, summary = "当组件类型为「FILTER」时必填")
    @Field.Advanced(columnDefinition = "text")
    private List<EipComponentDefinition> filterComponentDefinitions;

    //    集成接口相关
    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"interfaceName"}, referenceFields = {"interfaceName"})
    @Field(displayName = "集成接口", summary = "当组件类型为「NORMAL」时必填")
    private EipIntegrationInterface integrationInterface;

    @Base
    @Field.String
    @Field(displayName = "接口名称列表")
    private String interfaceName;

    //    处理函数相关
    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"convertNamespace", "convertFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "处理函数", summary = "当组件类型为「FILTER」时必填")
    private FunctionDefinition convertFunction;

    @Base
    @Field.String
    @Field(displayName = "处理函数命名空间")
    private String convertNamespace;

    @Base
    @Field.String
    @Field(displayName = "处理函数名称")
    private String convertFun;

    //    参数转换相关
    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "参数处理器", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipParamProcessor paramProcessor;

}
