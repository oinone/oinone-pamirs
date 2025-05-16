package pro.shushi.pamirs.framework.connectors.data.api.domain.model.system;

import org.apache.commons.collections4.MapUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 表结构（含模型编码）
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Static
@Model.Advanced(table = ModelTable.TABLE_NAME,
        unique = "tableName,tableSchema,dsKey",
        index = {"module", "tableSchema,isDeleted"}
)
@Model.Persistence(capitalMode = true)
@Model.model(ModelTable.MODEL_MODEL)
@Model
public class ModelTable extends IdModel {

    public final static String MODEL_MODEL = "system.ModelTable";
    public final static String TABLE_NAME = "pamirs_model_table";
    private static final long serialVersionUID = -4512108842811818927L;

    @Field
    private String dsKey;

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String logicTableName;

    @Field
    private String sharding;

    @Field
    private String module;

    @Field
    private String model;

    private Map<String/*field | columnName*/, FieldColumn> fieldColumnMap;

    private Map<String/*indexName*/, ModuleIndex> moduleIndexMap;

    private Boolean changed;

    private Boolean using;

    private Boolean supportDrop;

    public ModelTable() {
        this.setFieldColumnMap(new HashMap<>());
        this.setModuleIndexMap(new HashMap<>());
    }

    public boolean isEmptyObj() {
        return MapUtils.isEmpty(this.get_d());
    }

}
