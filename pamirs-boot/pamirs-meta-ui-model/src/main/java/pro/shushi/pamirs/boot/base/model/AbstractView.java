package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.boot.base.enmu.ViewBizTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.DiffUtils;

@Base
@Model.model(AbstractView.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 28)
@Model(displayName = "抽象视图定义", labelFields = {"title"}, summary = "抽象视图定义")
public class AbstractView extends MetaBaseModel {

    private static final long serialVersionUID = -6599895712600963150L;

    public static final String MODEL_MODEL = "base.AbstractView";

    @Base
    @Field(displayName = "业务类型", required = true)
    private ViewBizTypeEnum bizType;

    @Base
    @Field(displayName = "视图类型", required = true)
    private ViewTypeEnum type;

    @Base
    @Field.Text
    @Field(displayName = "缩略图")
    private String thumbnail;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "MEDIUMTEXT")
    @Field(displayName = "模板")
    private String template;

    @Base
    @Field.Text
    @Field(displayName = "自定义布局")
    private String layout;

    @Base
    @Field(displayName = "基础布局名称")
    private String baseLayoutName;

    @Base
    @Field(displayName = "是否加载布局", defaultValue = "false", store = NullableBoolEnum.FALSE)
    private Boolean loadLayout;

    @Base
    @Field(displayName = "已编译", defaultValue = "false")
    private Boolean compiled;

    @Base
    @Field(displayName = "已授权", defaultValue = "false")
    private Boolean authed;

    @Base
    @Field(displayName = "已国际化", defaultValue = "false")
    private Boolean translated;

    @Base
    @Field(displayName = "优先级", required = true, summary = "数字越小，优先级越高")
    private Integer priority;

    @Base
    @Field(displayName = "显隐", defaultValue = "true", required = true)
    private ActiveEnum show;

    @Base
    @Field(displayName = "是否生效", defaultValue = "true", required = true)
    private ActiveEnum active;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "layoutDefinition", "loadLayout");
    }

}
