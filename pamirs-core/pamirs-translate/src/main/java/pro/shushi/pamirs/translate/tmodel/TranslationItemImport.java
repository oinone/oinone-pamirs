package pro.shushi.pamirs.translate.tmodel;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.translate.model.AbstractTranslationItem;

@Model("导入翻译文件")
@Model.model(TranslationItemImport.MODEL_MODEL)
public class TranslationItemImport extends AbstractTranslationItem {

    public static final String MODEL_MODEL = "translation.TranslationItemImport";

}
