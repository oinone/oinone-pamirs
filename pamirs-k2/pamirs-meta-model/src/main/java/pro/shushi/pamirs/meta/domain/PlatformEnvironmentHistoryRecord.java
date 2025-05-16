package pro.shushi.pamirs.meta.domain;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.enmu.PlatformEnvironmentTypeEnum;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * Pamirs平台环境变更日志记录
 *
 * @author Gesi at 15:00 on 2024-11-21
 */
@Base
@MetaSimulator(onlyBasicTypeField = false)
@Model.model(PlatformEnvironmentHistoryRecord.MODEL_MODEL)
@Model(displayName = "平台环境变更日志记录", summary = "平台环境变更日志记录")
@Model.Advanced(index = {"startupCode,environmentType,environmentCode,createDate", "createDate,startupCode"})
public class PlatformEnvironmentHistoryRecord extends IdModel {

    private static final long serialVersionUID = -8239672649199985976L;

    public static final String MODEL_MODEL = "base.PlatformEnvironmentHistoryRecord";

    @Field.String(size = 256)
    @Field(displayName = "启动编码", summary = "每一个jvm实例唯一")
    private String startupCode;

    @Field.Enum
    @Field(displayName = "变更类型", summary = "创建/修改/删除")
    private PlatformEnvironmentTypeEnum alterType;

    @Field.String(size = 64)
    @Field(displayName = "环境类型")
    private String environmentType;

    @Field.String(size = 64)
    @Field(displayName = "环境唯一键")
    private String environmentCode;

    @Field.Text
    @Field(displayName = "环境键")
    private String environmentKey;

    @Field.Text
    @Field(displayName = "历史值")
    private String historyValue;

    @Field.Text
    @Field(displayName = "当前值")
    private String currentValue;

}
