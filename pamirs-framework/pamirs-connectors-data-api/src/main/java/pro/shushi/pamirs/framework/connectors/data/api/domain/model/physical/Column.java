package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 列定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 52)
@Model.model(Column.MODEL_MODEL)
@Model("数据表列定义")
public class Column extends TransientModel {

    public final static String MODEL_MODEL = "system.Column";
    private static final long serialVersionUID = -2982051808077803560L;

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String columnName;

    @Field
    private String columnType;

    @Field
    private String dataType;

    @Field
    private Long characterMaximumLength;

    @Field
    private Long characterOctetLength;

    @Field
    private Integer numericPrecision;

    @Field
    private Integer numericScale;

    @Field
    private Integer datetimePrecision;

    @Field
    private String nullable;

    @Field
    private String defaultValue;

    @Field
    private String extra;

    @Field
    private Long ordinalPosition;

    @Field
    private String columnComment;

    @Field
    private String characterSetName;

    @Field
    private String collationName;

}
