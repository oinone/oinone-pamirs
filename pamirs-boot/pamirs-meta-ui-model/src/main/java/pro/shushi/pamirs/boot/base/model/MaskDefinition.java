package pro.shushi.pamirs.boot.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.util.DiffUtils;

/**
 * 母版
 * <p>
 * 2021/5/26 12:22 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(22)
@Base
@Model.model(MaskDefinition.MODEL_MODEL)
@Model.Advanced(unique = "name")
@Model(displayName = "母版", labelFields = {"title"}, summary = "母版")
public class MaskDefinition extends AbstractView {

    private static final long serialVersionUID = -2450613044946907963L;

    public static final String MODEL_MODEL = "base.MaskDefinition";

    @Base
    @Field(displayName = "母版名称", required = true)
    private String name;

    @Base
    @Field(displayName = "母版标题")
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
    @Field(displayName = "布局定义")
    @Field.many2one
    @Field.Relation(relationFields = {"baseLayoutName", "#mask#"}, referenceFields = {"name", "layoutType"})
    private LayoutDefinition baseLayoutDefinition;

    @Base
    @Field(displayName = "优先级", required = true)
    private Integer priority;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "layoutDefinition", "loadLayout", "baseLayoutDefinition");
    }

}
