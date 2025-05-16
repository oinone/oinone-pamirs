package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.sqlserver;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * SqlServer列定义
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 52)
@Model.model(SqlServerColumn.MODEL_MODEL)
@Model("SqlServer数据表列定义")
public class SqlServerColumn extends TransientModel {

    private static final long serialVersionUID = -312176176712103879L;

    public static final String MODEL_MODEL = "system.SqlServerColumn";

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String columnName;

    @Field
    private String dataType;

    @Field
    private Integer dataLength;

    @Field
    private Integer dataTimePrecision;

    @Field
    private Integer dataScale;

    @Field
    private Integer numericPrecision;

    @Field
    private Integer numericScale;

    @Field
    private Long charLength;

    @Field
    private Long charColDeclLength;

    @Field
    private String nullable;

    @Field
    private String defaultValue;

    @Field
    private Long columnId;

    @Field
    private String columnComment;

    @Field
    private String characterSetName;

    //TODO sqlServer 的自增规则
    //自增列
    @Field
    private Integer isIdentity;
    //种子值
    @Field
    private Integer seedValue;
    //递增值
    @Field
    private Integer incrementValue;
}
