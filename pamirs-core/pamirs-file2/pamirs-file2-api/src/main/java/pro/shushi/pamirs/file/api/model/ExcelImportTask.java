package pro.shushi.pamirs.file.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

@Model.model(ExcelImportTask.MODEL_MODEL)
@Model(displayName = "Excel导入任务")
public class ExcelImportTask extends AbstractExcelTask {

    private static final long serialVersionUID = 4504966132482062956L;

    public static final String MODEL_MODEL = "file.ExcelImportTask";

    @Field.many2one
    @Field(displayName = "导入文件", required = true)
    private PamirsFile file;

    @Field.Boolean
    @Field(displayName = "逐行导入")
    private Boolean eachImport;

    @Field.Boolean
    @Field(displayName = "出现错误进行回滚")
    private Boolean hasErrorRollback;

    @Field.Integer
    @Field(displayName = "最大错误数")
    private Integer maxErrorLength;

    @Field.many2one
    @Field(displayName = "导入失败文件", summary = "当使用逐行导入时，将生成导入失败的文件")
    private PamirsFile errorFile;

    @Field.String
    @Field(displayName = "导入数据", multi = true, store = NullableBoolEnum.FALSE)
    private List<String> importDataList;

    @JSONField(serialize = false)
    private transient List<ExcelReadCallback> readCallbackList;
}
