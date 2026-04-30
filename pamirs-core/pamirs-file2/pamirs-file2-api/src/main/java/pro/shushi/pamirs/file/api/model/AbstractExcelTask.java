package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.core.common.behavior.IUserNameModel;
import pro.shushi.pamirs.file.api.enmu.ExcelTaskStateEnum;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Adamancy Zhang
 * @date 2021-01-15 12:45
 */
@Base
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model.model(AbstractExcelTask.MODEL_MODEL)
@Model(displayName = "Excel任务抽象基类")
public abstract class AbstractExcelTask extends IdModel implements IUserNameModel {

    private static final long serialVersionUID = 2572781118514043363L;

    public static final String MODEL_MODEL = "file.AbstractExcelTask";

    @Base
    @Field.String
    @Field(displayName = "任务名称", required = true)
    private String name;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"workbookDefinitionId"}, referenceFields = {"id"})
    @Field(displayName = "Excel工作簿定义", required = true)
    private ExcelWorkbookDefinition workbookDefinition;

    @Base
    @Field.Integer
    @Field(displayName = "Excel工作簿定义ID", invisible = true)
    private Long workbookDefinitionId;

    @Base
    @Field.String
    @Field(displayName = "Excel工作簿名称")
    private String workbookName;

    @Base
    @Field.Enum
    @Field(displayName = "任务状态", required = true)
    private ExcelTaskStateEnum state;

    @Base
    @Field.one2many
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field.Relation(store = false)
    @Field(displayName = "任务信息列表", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private List<TaskMessage> messages;

    @Base
    @Field.String
    @Field(displayName = "模块编码", summary = "操作导入导出时的模块")
    private String module;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "所属应用")
    private ModuleDefinition moduleDefinition;

    @Base
    @Field.String
    @Field(displayName = "创建人名称", store = NullableBoolEnum.FALSE)
    private String createUserName;

    @Base
    @Field.String
    @Field(displayName = "修改人名称", store = NullableBoolEnum.FALSE)
    private String writeUserName;

    @Base
    @Field.String
    @Field(displayName = "模型编码", summary = "操作导入导出的模型", store = NullableBoolEnum.FALSE)
    private String model;

    private Integer lastedRowIndex;

    private Integer rowIndex;

    public void addTaskMessage(TaskMessageLevelEnum level, String message) {
        addTaskMessage(level, message, null);
    }

    public void addTaskMessage(TaskMessageLevelEnum level, String message, Boolean sys) {
        List<TaskMessage> taskMessages = getMessages();
        if (taskMessages == null) {
            taskMessages = new ArrayList<>();
            setMessages(taskMessages);
        }
        TaskMessage taskMessage = new TaskMessage().setLevel(level).setMessage(message).setSys(sys).setRecordDate(new Date());
        Integer rowIndex = getRowIndex();
        if (rowIndex != null) {
            taskMessage.setRowIndex(rowIndex + 1);
        }
        taskMessages.add(taskMessage);
    }
}
