package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

/**
 * 列定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base
@Model.Static
@Model.Ds(ModuleConstants.MODULE_BASE)
@Model.Advanced(table = "PAMIRS_LOGIC_COLUMNS", unique = "model, field")
@Model.Persistence(capitalMode = true)
@Model.model(TestColumn.MODEL_MODEL)
public class TestColumn extends IdModel {

    public final static String MODEL_MODEL = "system.TestColumn";
    private static final long serialVersionUID = -2982051808077803560L;

    @Field(index = true)
    private String module;

    @Field
    private String model;

    @Field
    private String field;

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String columnName;

    @Field
    private String columnType;

    @Field
    private Boolean isNullable;

    @Field
    private String columnDefault;

    @Field
    private String extra;

    @Field
    private Long ordinalPosition;

    @Field
    private String columnComment;

}
