package pro.shushi.pamirs.translate.proxy;

import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxAction;
import pro.shushi.pamirs.boot.base.ux.annotation.action.UxRoute;
import pro.shushi.pamirs.boot.base.ux.annotation.button.UxRouteButton;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;

@Model(displayName = "页面弹窗 翻译资源项")
@Model.model(TranslationItemProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@UxRouteButton(
        action = @UxAction(name = "resourceResourceTranslationCreate", label = "创建", contextType = ActionContextTypeEnum.CONTEXT_FREE, bindingView = "translationItemTable", bindingType = ViewTypeEnum.TABLE),
        value = @UxRoute(model = ResourceTranslation.MODEL_MODEL, viewName = "translationForm", viewType = ViewTypeEnum.FORM, openType = ActionTargetEnum.ROUTER)
)
@UxRouteButton(
        action = @UxAction(name = "resourceResourceTranslationDetail", label = "详情", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = TranslationItemProxy.MODEL_MODEL, viewName = "translationDetail", viewType = ViewTypeEnum.DETAIL, openType = ActionTargetEnum.DRAWER)
)
@UxRouteButton(
        action = @UxAction(name = "resourceResourceTranslationEdit", label = "编辑", contextType = ActionContextTypeEnum.SINGLE),
        value = @UxRoute(model = TranslationItemProxy.MODEL_MODEL, viewName = "translationEdit", viewType = ViewTypeEnum.FORM, openType = ActionTargetEnum.DRAWER)
)
public class TranslationItemProxy extends ResourceTranslationItem {

    public static final String MODEL_MODEL = "translation.TranslationItemProxy";

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "翻译所在模块", translate = true)
    private ModuleDefinition moduleDefinition;


    @Field.many2one
    @Field.Relation(relationFields = {"resLangCode"}, referenceFields = {"code"})
    @Field(displayName = "源语言")
    private ResourceLang resLang;

    @Field.many2one
    @Field.Relation(relationFields = {"langCode"}, referenceFields = {"code"})
    @Field(displayName = "目标语言")
    private ResourceLang lang;

    @Field.String
    @Field(displayName = "前端行为", summary = "前端行为")
    private String action;

}
