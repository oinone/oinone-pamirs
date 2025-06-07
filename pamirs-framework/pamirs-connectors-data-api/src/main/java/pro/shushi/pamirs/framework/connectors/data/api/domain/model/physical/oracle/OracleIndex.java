package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.oracle;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * ORACLE索引定义
 *
 * @author Adamancy Zhang at 10:42 on 2023-06-25
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 53)
@Model.model(OracleIndex.MODEL_MODEL)
@Model("ORACLE索引定义")
public class OracleIndex extends TransientModel {

    private static final long serialVersionUID = 5286240907959188957L;

    public static final String MODEL_MODEL = "system.OracleIndex";

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
