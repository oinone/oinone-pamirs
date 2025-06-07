package pro.shushi.pamirs.eip.api.model.scene;

import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.CodeModel;

import java.util.Date;

/**
 * @author drome
 * @date 2021/7/3011:54 上午
 */
@Base
@Model.model(EipSceneInstance.MODEL_MODEL)
@Model(displayName = "场景实例", labelFields = "name")
@Model.Code(sequence = "SEQ", prefix = "SI", size = 8)
public class EipSceneInstance extends CodeModel implements IDataStatus {

    public static final String MODEL_MODEL = "pamirs.eip.EipSceneInstance";
    private static final long serialVersionUID = -6378430362759147802L;


    @Base
    @Field.String
    @Field(displayName = "场景实例名称", required = true)
    private String name;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"sceneName"}, referenceFields = {"sceneName"})
    @Field(displayName = "场景定义", required = true)
    private EipSceneDefinition sceneDefinition;

    @Base
    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED")
    private DataStatusEnum dataStatus;

    @Base
    @Field.many2one
    @Field(displayName = "数据来源节点")
    @Field.Relation(relationFields = {"dataSourceNodeDefinitionCode"}, referenceFields = {"code"})
    private EipSceneNodeDefinition dataSourceNodeDefinition;

    @Base
    @Field.many2one
    @Field(displayName = "数据接收节点")
    @Field.Relation(relationFields = {"dataTargetNodeDefinitionCode"}, referenceFields = {"code"})
    private EipSceneNodeDefinition dataTargetNodeDefinition;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"routeInterfaceName"}, referenceFields = {"interfaceName"})
    @Field(displayName = "路由定义", summary = "根据场景实例自动注册")
    private EipRouteDefinition eipRouteDefinition;

    @Base
    @Field.String
    @Field(displayName = "路由定义技术名称")
    private String routeInterfaceName;


    //    定时任务相关
    @Base
    @Field.Boolean
    @Field(displayName = "是否初始化定时任务")
    private Boolean initTask;

    @Base
    @Field.Text
    @Field(displayName = "场景实例入参")
    private String instanceBody;

    @Base
    @Field.Integer
    @Field(displayName = "执行周期时间", summary = "根据下次执行时间向后计算的时间，如MINUTES单位下，该值设为1，则表示在执行指定函数1分钟后再次执行函数")
    private Integer periodTimeValue;

    @Base
    @Field.Enum
    @Field(displayName = "执行周期时间单位", summary = "根据下次执行时间向后计算的时间单位")
    private TimeUnitEnum periodTimeUnit;

    @Base
    @Field.Integer
    @Field(displayName = "下次重试执行时间", summary = "根据执行时间向后延时的时间，如MINUTES单位下，该值设为1，则表示在执行函数1分钟后重新执行执行函数")
    private Integer nextRetryTimeValue;

    @Base
    @Field.Enum
    @Field(displayName = "下次重试时间单位", summary = "根据执行时间向后延时的时间单位")
    private TimeUnitEnum nextRetryTimeUnit;

    @Base
    @Field.Integer
    @Field(displayName = "最大重试次数")
    private Integer limitRetryNumber;

    //    增量日志相关
    @Base
    @Field.Boolean
    @Field(displayName = "启用增量日志")
    private Boolean useIncUpdateLog;

    @Base
    @Field.Date
    @Field(displayName = "默认增量开始时间")
    private Date defaultStartTime;

    // FIXME: 2021/8/5 现在1个实例,1个任务,所以拼接实例编码就保证了唯一. 应该是1对多.
    public String getScheduleTechnicalName() {
        return getRouteInterfaceName() + getCode();
    }

    public String getIncUpdateLogInterfaceName() {
        return getRouteInterfaceName() + getCode();
    }
}
