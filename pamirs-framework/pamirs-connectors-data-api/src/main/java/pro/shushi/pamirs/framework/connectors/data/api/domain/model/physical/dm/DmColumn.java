package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.dm;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.oracle.OracleColumn;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * DM列定义
 *
 * @author Adamancy Zhang at 20:52 on 2023-06-30
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 52)
@Model.model(DmColumn.MODEL_MODEL)
@Model("DM数据表列定义")
public class DmColumn extends TransientModel {

    private static final long serialVersionUID = -4322866820023447268L;

    public static final String MODEL_MODEL = "system.DmColumn";

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
    private Integer dataPrecision;

    @Field
    private Integer dataScale;

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

    @Field
    private Integer isIdentity;
}
