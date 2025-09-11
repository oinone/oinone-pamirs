package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.file.api.enmu.ExcelExportFileTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelExportMethodEnum;
import pro.shushi.pamirs.file.api.pmodel.ExcelModelField;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.resource.api.tmodel.ConditionWrapper;

import java.util.List;
import java.util.function.Supplier;

@Model.model(ExcelExportTask.MODEL_MODEL)
@Model(displayName = "Excel导出任务")
public class ExcelExportTask extends AbstractExcelTask {

    private static final long serialVersionUID = 7576572133419260665L;

    public static final String MODEL_MODEL = "file.ExcelExportTask";

    @Field.many2one
    @Field(displayName = "导出文件")
    private PamirsFile file;

    @Field.Enum
    @Field(displayName = "导出文件类型")
    private ExcelExportFileTypeEnum fileType;

    @Field.many2one
    @Field.Advanced(columnDefinition = "longtext")
    @Field.Relation(store = false)
    @Field(displayName = "查询条件", store = NullableBoolEnum.TRUE)
    private ConditionWrapper conditionWrapper;

    @Field.String
    @Field.Related(related = {"conditionWrapper", "rsql"})
    @Field(displayName = "RSQL过滤条件")
    private String rsql;

    @Field(displayName = "同步")
    @Field.Boolean
    private Boolean sync;

    @Field.Enum
    @Field(displayName = "导出选项", defaultValue = "TEMPLATE", store = NullableBoolEnum.FALSE)
    private ExcelExportMethodEnum exportMethod;

    @Field.one2many
    @Field.Relation(relationFields = {"model"}, referenceFields = {"model"}, store = false)
    @Field(displayName = "选择字段")
    private List<ExcelModelField> selectedFields;

    @Field.String
    @Field(displayName = "单次请求ID", summary = "同步下载时，先提供完整数据存储在redis，再通过对应ID获取数据进行下载")
    private String requestId;

    public <T> T temporaryRsql(String extend, Supplier<T> supplier) {
        ConditionWrapper conditionWrapper = this.getConditionWrapper();
        boolean isNull = false;
        if (conditionWrapper == null) {
            isNull = true;
            conditionWrapper = new ConditionWrapper();
            this.setConditionWrapper(conditionWrapper);
        }
        String originRsql = conditionWrapper.getRsql();
        String rsql = conditionWrapper.and(extend);
        conditionWrapper.setRsql(rsql);
        T result = supplier.get();
        if (isNull) {
            this.setConditionWrapper(null);
        } else {
            conditionWrapper.setRsql(originRsql);
        }
        return result;
    }
}
