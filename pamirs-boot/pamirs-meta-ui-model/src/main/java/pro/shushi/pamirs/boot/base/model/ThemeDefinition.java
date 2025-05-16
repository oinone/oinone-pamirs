package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;

/**
 * 主题
 * <p>
 * 2021/5/26 12:22 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@MetaSimulator(onlyBasicTypeField = false)
@Model.Advanced(unique = "name")
@Model.model(ThemeDefinition.MODEL_MODEL)
@Model(displayName = "主题", labelFields = {"title"}, summary = "主题")
public class ThemeDefinition extends IdModel {

    private static final long serialVersionUID = 8321976407263291584L;

    public static final String MODEL_MODEL = "base.ThemeDefinition";

    @Base
    @Field(displayName = "主题名称", required = true)
    private String name;

    @Base
    @Field(displayName = "主题标题")
    private String title;

    @Base
    @Field.String
    @Field(displayName = "简介", summary = "描述摘要")
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "描述", summary = "描述详情")
    private String description;

    @Base
    @Field(displayName = "优先级", required = true)
    private Integer priority;

    @Base
    @Field(displayName = "是否生效", defaultValue = "true", required = true)
    private ActiveEnum active;

}
