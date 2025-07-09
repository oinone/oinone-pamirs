package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.oracle;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * ORACLE列定义
 *
 * @author Adamancy Zhang at 09:26 on 2023-06-25
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 52)
@Model.model(OracleColumn.MODEL_MODEL)
@Model("ORACLE数据表列定义")
public class OracleColumn extends TransientModel {

    private static final long serialVersionUID = -312176176722103879L;

    public static final String MODEL_MODEL = "system.OracleColumn";

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
    private String aiSequenceName;

}
