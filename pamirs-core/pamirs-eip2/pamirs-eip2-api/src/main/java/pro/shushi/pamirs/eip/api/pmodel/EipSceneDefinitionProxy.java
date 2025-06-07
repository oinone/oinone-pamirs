package pro.shushi.pamirs.eip.api.pmodel;

import pro.shushi.pamirs.core.common.enmu.TimeUnitEnum;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneDefinition;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneNodeDefinition;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.Date;

/**
 * @author drome
 * @date 2021/7/307:13 下午
 */
@Model.model(EipSceneDefinitionProxy.MODEL_MODEL)
@Model(displayName = "场景定义代理模型")
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class EipSceneDefinitionProxy extends EipSceneDefinition {

    public static final String MODEL_MODEL = "pamirs.eip.EipSceneDefinitionProxy";

    private static final long serialVersionUID = 1729045708211917815L;

    //    列表展示
    @Field.String
    @Field(displayName = "数据来源系统列表文字")
    private String dataSourceStr;

    @Field.String
    @Field(displayName = "数据接收系统列表文字")
    private String dataTargetStr;

    @Field.Integer
    @Field(displayName = "实例数量")
    private Integer instanceNum;

    //    生成实例
    @Field.many2one
    @Field(displayName = "数据来源节点")
    private EipSceneNodeDefinition dataSourceNode;

    @Field.many2one
    @Field(displayName = "数据接收节点")
    private EipSceneNodeDefinition dataTargetNode;

    //实例定时任务相关
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
}
