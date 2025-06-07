package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.boot.base.enmu.TemplateLayoutTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.ViewBizTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.DiffUtils;

/**
 * 布局定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(22)
@Base
@Model.model(LayoutDefinition.MODEL_MODEL)
@Model.Advanced(unique = "name", priority = 28)
@Model(displayName = "布局定义", labelFields = {"title"}, summary = "布局定义")
public class LayoutDefinition extends MetaBaseModel {

    private static final long serialVersionUID = -3263689583110771467L;

    public static final String MODEL_MODEL = "base.LayoutDefinition";

    @Base
    @Field(displayName = "名称", required = true)
    private String name;

    @Base
    @Field(displayName = "标题")
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
    @Field(displayName = "布局类型", required = true)
    private TemplateLayoutTypeEnum layoutType;

    @Base
    @Field(displayName = "业务类型", required = true)
    private ViewBizTypeEnum bizType;

    @Base
    @Field(displayName = "视图类型", required = true)
    private ViewTypeEnum type;

    @Base
    @Field(displayName = "优先级", required = true)
    private Integer priority;

    @Base
    @Field.Text
    @Field(displayName = "缩略图")
    private String thumbnail;

    @Base
    @Field.Text
    @Field(displayName = "模板")
    private String template;

    @Base
    @Field(displayName = "显隐", defaultValue = "true", required = true)
    private ActiveEnum show;

    /**
     * TODO: active暂时没有用到，如果后面用到更新这个值需要清除layout内存中的缓存
     *
     * @see pro.shushi.pamirs.boot.web.cache.LayoutMemCache#clearLayoutDefByName
     */
    @Base
    @Field(displayName = "是否生效", defaultValue = "true", required = true)
    private ActiveEnum active;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this);
    }

}
