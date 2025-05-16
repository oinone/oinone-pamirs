package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * Excel导入结果
 *
 * @author Adamancy Zhang at 22:02 on 2021-08-17
 */
@Base
@Model.model(ExcelImportResult.MODEL_MODEL)
@Model(displayName = "Excel导入结果")
public class ExcelImportResult extends TransientModel {

    private static final long serialVersionUID = 7810566938778362930L;

    public static final String MODEL_MODEL = "file.ExcelImportResult";

    public ExcelImportResult() {
    }

    public ExcelImportResult(ExcelImportTask importTask, Object data) {
        setImportTask(importTask);
        setData(data);
    }

    @Field(displayName = "导入任务")
    private ExcelImportTask importTask;

    @Field(displayName = "数据")
    private Object data;
}
