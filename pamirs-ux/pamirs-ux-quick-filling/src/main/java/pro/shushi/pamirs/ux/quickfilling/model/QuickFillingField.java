package pro.shushi.pamirs.ux.quickfilling.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;

import java.util.List;

/**
 * @author Gesi at 17:15 on 2025/9/10
 */
@Base
@Model(displayName = "快速填报字段")
@Model.model(QuickFillingField.MODEL_MODEL)
public class QuickFillingField extends TransientModel {

    private static final long serialVersionUID = 3186548059015651004L;

    public static final String MODEL_MODEL = "quick.filling.QuickFillingField";

    @Field(displayName = "字段名")
    private String field;

    @Field(displayName = "是否必填")
    private Boolean required;

    @Field(displayName = "是否验证")
    private Boolean validate;

    @Field(displayName = "关联选项字段名")
    private List<String> labelFields;

    @Field(displayName = "数据字典可选项")
    private List<DataDictionaryItem> options;
}
