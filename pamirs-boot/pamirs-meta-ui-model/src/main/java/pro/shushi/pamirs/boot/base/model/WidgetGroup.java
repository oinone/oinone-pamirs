package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;

/**
 * 组件分组
 *
 * @author wangxian@shushi.pro
 * @version 1.0.0
 * date 2023/12/12
 */
@Base
@Model(displayName = "组件分组")
@Model.model(WidgetGroup.MODEL_MODEL)
@Model.Advanced(unique = {"name", "displayName"})
public class WidgetGroup extends IdModel {

    public final static String MODEL_MODEL = "base.WidgetGroup";

    @Base
    @Field.String(size = 64)
    @Field(displayName = "api名称", required = true, invisible = true)
    private String name;

    @Base
    @Field(displayName = "名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field(displayName = "可见性", summary = "显隐", defaultValue = "true")
    private ActiveEnum active;

    @Base
    @Field.Text
    @Field(displayName = "帮助文案")
    private String help;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = "0")
    private Long priority;

    @Base
    @Field.Advanced(columnDefinition = "TINYINT(1) NOT NULL DEFAULT '1'")
    @Field(displayName = "系统数据", defaultValue = "false", summary = "由系统产生的元数据")
    private Boolean sys;

}
