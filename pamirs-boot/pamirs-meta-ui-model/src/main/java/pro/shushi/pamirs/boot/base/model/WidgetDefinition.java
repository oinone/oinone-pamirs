package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.boot.base.enmu.WidgetBizTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.WidgetScopeEnum;
import pro.shushi.pamirs.boot.base.enmu.WidgetTechTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.List;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;

/**
 * 客户端组件定义
 *
 * @author d@shushi.pro
 * @version 2.2.0
 * date 2021/5/8 1:11 下午
 */
@Base
@Model.Advanced(priority = 29, unique = "name", index = {"group", "systemSource"})
@Model.model(WidgetDefinition.MODEL_MODEL)
@Model(displayName = "组件", summary = "组件", labelFields = {"displayName"})
public class WidgetDefinition extends IdModel {

    private static final long serialVersionUID = -8468100988559703168L;

    public static final String MODEL_MODEL = "base.WidgetDefinition";

    @Base
    @Field.String(size = 64)
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field(displayName = "api名称", required = true)
    private String name;

    @Base
    @Field.Text
    @Field(displayName = "帮助文案")
    private String help;

    @Base
    @Field.Enum
    @Field(displayName = "组件类型", required = true)
    private WidgetBizTypeEnum bizType;

    @Base
    @Field.Enum
    @Field(displayName = "技术类型", required = true)
    private WidgetTechTypeEnum techType;

    @Base
    @Field(displayName = "组件筛选", required = true)
    private String filter;

    @Base
    @Field(displayName = "动作类型", invisible = true)
    private String clazz;

    @Base
    @Field(displayName = "图标")
    private String icon;

    @Base
    @Field(displayName = "菜单说明")
    private String description;

    @Base
    @Field(displayName = "优先级")
    private Long priority;

    @Base
    @Field(displayName = "分组")
    @Field.many2one
    @Field.Relation(relationFields = "group", referenceFields = "name")
    private WidgetGroup widgetGroup;

    @Base
    @Field(displayName = "分组名称")
    private String category;

    @Base
    @Field(displayName = "分组")
    private String group;

    @Base
    @Field.Enum
    @Field(displayName = "适用范围", summary = "适用范围", multi = true)
    private List<WidgetScopeEnum> scope;

    @Base
    @Field.Text
    @Field(displayName = "样式文件", summary = "样式文件地址列表", invisible = true, serialize = COMMA)
    private List<String> css;

    @Base
    @Field.Text
    @Field(displayName = "代码文件", summary = "代码文件地址列表", invisible = true, serialize = COMMA)
    private List<String> javascript;

    @Field.Boolean
    @Field(displayName = "组件是否展示", defaultValue = "true")
    private Boolean show;

    @Field.Boolean
    @Field(displayName = "系统数据")
    private Boolean sys;

    @Field.Enum
    @Field(displayName = "来源")
    private SystemSourceEnum systemSource;

}
