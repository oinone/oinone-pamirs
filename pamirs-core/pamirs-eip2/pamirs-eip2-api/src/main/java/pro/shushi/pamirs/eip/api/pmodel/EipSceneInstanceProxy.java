package pro.shushi.pamirs.eip.api.pmodel;

import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.Date;

/**
 * @author drome
 * @date 2021/8/511:09 上午
 */
@Model.model(EipSceneInstanceProxy.MODEL_MODEL)
@Model(displayName = "场景实例代理模型")
@Model.Advanced(type = ModelTypeEnum.PROXY)
public class EipSceneInstanceProxy extends EipSceneInstance {

    public static final String MODEL_MODEL = "pamirs.eip.EipSceneInstanceProxy";

    // 定时任务执行相关
    @Field.Date
    @Field(displayName = "上次执行时间")
    private Date scheduleLastExecuteTime;

    @Field.Date
    @Field(displayName = "下次执行时间")
    private Date scheduleNextExecuteTime;

    @Field.Integer
    @Field(displayName = "累计执行次数")
    private Integer scheduleExecuteNumber;


    //    增量日志执行相关
    @Field.Text
    @Field(displayName = "请求参数")
    private String incUpdateLogParams;

    @Field.Date
    @Field(displayName = "开始时间")
    private Date incUpdateLogStartTime;

    @Field.Date
    @Field(displayName = "结束时间")
    private Date incUpdateLogEndTime;

    @Field.Date
    @Field(displayName = "上次增量完成时间")
    private Date incUpdateLogLastUpdateTime;
}
