package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.SystemModel;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 库结构回放
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Static
@Model.Advanced(table = "PAMIRS_SCHEMA_PLAY_BACK")
@Model.Persistence(capitalMode = true)
@Model.model(SchemaPlayBack.MODEL_MODEL)
@Model
public class SchemaPlayBack extends SystemModel {

    public final static String MODEL_MODEL = "system.SchemaPlayBack";
    private static final long serialVersionUID = -5624760950645375839L;

    @Base
    @Field.PrimaryKey
    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    private Long id;

    @Field(unique = true)
    private String dsKey;

    @Field(index = true)
    private String module;

    @Field.Advanced(columnDefinition = "longtext")
    @Field
    private String ddl;

    @Field.Advanced(columnDefinition = "text")
    @Field
    private String error;

    @Field.Advanced(updateStrategy = FieldStrategyEnum.IGNORED)
    @Field.Integer
    @Field
    private Integer line;

    @Field.Advanced(updateStrategy = FieldStrategyEnum.IGNORED)
    @Field
    private String prefixCommand;

    @Field.Version
    @Field.Integer
    @Field(defaultValue = "0")
    private Long optVersion;

}
