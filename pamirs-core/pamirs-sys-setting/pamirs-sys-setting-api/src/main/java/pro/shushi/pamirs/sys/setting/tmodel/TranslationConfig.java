package pro.shushi.pamirs.sys.setting.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model(displayName = "页面工具箱配置")
@Model.model(TranslationConfig.MODEL_MODEL)
public class TranslationConfig extends TransientModel {
    private static final long serialVersionUID = 3128868728485766758L;

    public static final String MODEL_MODEL = "translation.TranslationConfig";

    @Field(displayName = "页面单项翻译管理")
    private Boolean translationManage;

    @Field(displayName = "页面工具箱翻译管理")
    private Boolean toolboxTranslation;
}
