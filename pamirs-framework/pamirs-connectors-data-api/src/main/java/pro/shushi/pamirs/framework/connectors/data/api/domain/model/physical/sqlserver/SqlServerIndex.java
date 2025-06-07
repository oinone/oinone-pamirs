package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.sqlserver;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * SqlServer索引定义
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 53)
@Model.model(SqlServerIndex.MODEL_MODEL)
@Model("SqlServer索引定义")
public class SqlServerIndex extends TransientModel {

    private static final long serialVersionUID = 5286241207959188957L;

    public static final String MODEL_MODEL = "system.SqlServerIndex";

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String indexName;

    @Field
    private String columnName;

    @Field
    private Integer columnPosition;

    @Field
    private String uniqueness;

    @Field
    private String constraintName;

}
