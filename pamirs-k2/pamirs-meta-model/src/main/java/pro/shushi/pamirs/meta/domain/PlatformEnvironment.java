package pro.shushi.pamirs.meta.domain;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * Pamirs平台环境
 *
 * @author Adamancy Zhang at 11:00 on 2024-08-01
 */
@Base
@MetaSimulator(onlyBasicTypeField = false)
@Model.Advanced(unique = {"code"})
@Model.model(PlatformEnvironment.MODEL_MODEL)
@Model(displayName = "平台环境", summary = "平台环境")
public class PlatformEnvironment extends IdModel {

    private static final long serialVersionUID = 196689603641213210L;

    public static final String MODEL_MODEL = "base.PlatformEnvironment";

    @Field.String(size = 64)
    @Field(displayName = "唯一键")
    private String code;

    @Field.String(size = 64)
    @Field(displayName = "类型")
    private String type;

    @Field.Text
    @Field(displayName = "键")
    private String key;

    @Field.Text
    @Field(displayName = "值")
    private String value;
}
