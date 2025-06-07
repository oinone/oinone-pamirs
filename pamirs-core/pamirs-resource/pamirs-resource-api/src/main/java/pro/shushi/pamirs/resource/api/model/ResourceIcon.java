package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.IconLibTypeEnum;

@Model(displayName = "图标")
@Model.Advanced(unique = {"outId,libId"})
@Model.model(ResourceIcon.MODEL_MODEL)
@UxRouteButton(
        action = @UxAction(name = "iconUploadAction", displayName = "上传图标", label = "上传图标", contextType = ActionContextTypeEnum.CONTEXT_FREE),
        value = @UxRoute(model = ResourceIcon.MODEL_MODEL, viewName = "iconUploadManagement", openType = ActionTargetEnum.ROUTER))
@UxRouteButton(
        action = @UxAction(name = "iconEditAction", displayName = "编辑", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = ResourceIcon.MODEL_MODEL, viewName = "图标form", openType = ActionTargetEnum.DIALOG))
public class ResourceIcon extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceIcon";

    @Field.String
    @Field(displayName = "图标id")
    private String outId;

    @Field.Enum
    @Field(displayName = "图标类型")
    private IconLibTypeEnum type;

    @Field.String
    @Field(displayName = "图标名称")
    private String name;

    @Field.String
    @Field(displayName = "图标显示名称")
    private String displayName;

    @Field.Integer
    @Field(displayName = "图标库id")
    private Long libId;

    @Field.many2one
    @Field(displayName = "图标库")
    @Field.Relation(relationFields = {"libId"}, referenceFields = {"id"})
    private ResourceIconLib lib;

    @Field.Integer
    @Field(displayName = "分组id")
    private Long groupId;

    @Field.many2one
    @Field(displayName = "分组")
    @Field.Relation(relationFields = {"groupId"}, referenceFields = {"id"})
    private ResourceIconGroup group;

    @Field.Boolean
    @Field(displayName = "显隐")
    private Boolean show;

    @Field.String
    @Field(displayName = "unicode")
    private String unicode;

    @Field.Text(max = "500")
    @Field(displayName = "备注")
    private String remark;

    @Field.String
    @Field(displayName = "字体图标类名")
    private String fontClass;

    @Field.String
    @Field(displayName = "字体图标全类名")
    private String fullFontClass;

    @Field.Boolean
    @Field(displayName = "是否系统图标")
    private Boolean sys;
}
