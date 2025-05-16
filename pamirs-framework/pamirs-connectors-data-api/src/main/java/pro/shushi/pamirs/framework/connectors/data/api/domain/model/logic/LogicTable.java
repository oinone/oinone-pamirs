package pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 表结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 51)
@Model.model(LogicTable.MODEL_MODEL)
@Model("逻辑表定义")
public class LogicTable extends AbstractModel {

    public final static String MODEL_MODEL = "logic.LogicTable";
    private static final long serialVersionUID = -6388657208996560063L;

    @Field
    private String dsKey;

    @Field
    private String module;

    @Field
    private String model;

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String sharding;

    @Field
    private String tableComment;

    @Field
    private String tableCollation;

    @Field
    private String characterSetName;

    private Map<String/*field | columnName*/, Column> columnMap;

    private Map<String/*indexName*/, LogicIndex> indexMap;

    private ModelTable modelTable;

    // 是否支持删除表或字段
    // 用于传递给modelTable字段中supportDrop字段
    private boolean supportDrop;

    public LogicTable() {
        this.setColumnMap(new HashMap<>());
        this.setIndexMap(new HashMap<>());
    }

    public ModelTable useModelTable() {
        if (null == getModelTable()) {
            this.setModelTable(new ModelTable().setDsKey(getDsKey())
                    .setModule(getModule()).setModel(getModel()).setTableSchema(getTableSchema()).setTableName(getTableName())
                    .setSupportDrop(isSupportDrop())
                    .setChanged(true));
        }
        return getModelTable();
    }

    public void setSchemaAndTable(String tableSchema, String tableName) {
        if (StringUtils.isBlank(this.getTableSchema())) {
            this.setTableSchema(tableSchema);
        }
        if (StringUtils.isBlank(this.getTableName())) {
            this.setTableName(tableName);
        }
    }

}
