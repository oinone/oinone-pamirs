package pro.shushi.pamirs.translate.tmodel;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.translate.model.AbstractTranslationItem;

@Model("更改翻译应用范围")
@Model.model(TranslationItemChange.MODEL_MODEL)
public class TranslationItemChange extends AbstractTranslationItem {

    public static final String MODEL_MODEL = "translation.TranslationItemChange";

}
