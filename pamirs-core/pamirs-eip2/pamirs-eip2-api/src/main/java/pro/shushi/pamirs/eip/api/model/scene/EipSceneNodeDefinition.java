package pro.shushi.pamirs.eip.api.model.scene;

import pro.shushi.pamirs.eip.api.enmu.EipSceneNodeTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipConvertParam;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * @author drome
 * @date 2021/7/3011:54 上午
 */
@Base
@Model.model(EipSceneNodeDefinition.MODEL_MODEL)
@Model(displayName = "场景节点定义", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "SN", size = 8)
public class EipSceneNodeDefinition extends CodeModel {

    public static final String MODEL_MODEL = "pamirs.eip.EipSceneNodeDefinition";
    private static final long serialVersionUID = -295026569206771582L;

    @Base
    @Field.String
    @Field(displayName = "节点名称", required = true)
    private String name;

    @Field.String
    @Field(displayName = "场景节点类型", summary = "区分是数据提供系统的节点,还是数据处理系统的节点")
    private String type;

    @Field.many2one
    @Field(displayName = "数据系统")
    @Field.Relation(relationFields = {"dataModuleModule"}, referenceFields = {"module"})
    private ModuleDefinition dataModule;

    @Field.String
    @Field(displayName = "数据系统标识")
    private String dataModuleModule;

    //固定的流程.  入参转换 -> 调用 -> 出参转换
    //    入参转换
    @Field.many2one
    @Field(displayName = "场景入参转换函数")
    @Field.Relation(relationFields = {"inConverterNamespace", "inConverterFun"}, referenceFields = {"namespace", "fun"})
    private FunctionDefinition inConverterFunction;

    @Field.one2many
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "场景入参转换参数列表", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private List<EipConvertParam> inConvertParamList;

    @Field.String
    @Field(displayName = "场景入参转换最终结果键值")
    private String inFinalResultKey;


    //    数据系统调用
    @Field.Enum
    @Field(displayName = "节点处理器类型", summary = "调用接口还是调用函数")
    private EipSceneNodeTypeEnum nodeTypeEnum;

    @Base
    @Field.many2one
    @Field(displayName = "调用函数")
    @Field.Relation(relationFields = {"functionNamespace", "functionFun"}, referenceFields = {"namespace", "fun"})
    private FunctionDefinition function;

    @Base
    @Field.many2one
    @Field(displayName = "调用集成接口")
    @Field.Relation(relationFields = {"eipIntegrationInterfaceName"}, referenceFields = {"interfaceName"})
    private EipIntegrationInterface eipIntegrationInterface;

    @Base
    @Field.many2one
    @Field(displayName = "调用组合接口")
    @Field.Relation(relationFields = {"eipRouteInterfaceName"}, referenceFields = {"interfaceName"})
    private EipRouteDefinition eipRouteDefinition;

    //    出参转换
    @Field.many2one
    @Field(displayName = "场景出参转换函数")
    @Field.Relation(relationFields = {"outConverterNamespace", "outConverterFun"}, referenceFields = {"namespace", "fun"})
    private FunctionDefinition outConverterFunction;

    @Field.one2many
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "场景出参转换参数列表", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private List<EipConvertParam> outConvertParamList;

    @Field.String
    @Field(displayName = "场景出参转换最终结果键值")
    private String outFinalResultKey;
}
