package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.Date;

@Base
@Model.model(TaskMessage.MODEL_MODEL)
@Model(displayName = "任务消息")
public class TaskMessage extends TransientModel {

    private static final long serialVersionUID = -7636192506367002833L;

    public static final String MODEL_MODEL = "file.TaskMessage";

    @Field(displayName = "ID", priority = 5)
    private Long id;

    @Field(displayName = "任务消息级别", translate = true)
    private TaskMessageLevelEnum level;

    @Field(displayName = "记录时间")
    private Date recordDate;

    @Field(displayName = "记录行号")
    private Integer rowIndex;

    @Field(displayName = "消息内容", translate = true)
    private String message;
}
