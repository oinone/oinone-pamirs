package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author Gesi at 17:15 on 2025/9/10
 */
@Model(displayName = "快速填报字段")
@Model.model(QuickFillingField.MODEL_MODEL)
public class QuickFillingField extends TransientModel {

    public static final String MODEL_MODEL = "base.QuickFillingField";

    @Field(displayName = "字段名")
    private String field;

    @Field(displayName = "关联选项字段名")
    private List<String> relationSelectFields;

    private ModelFieldConfig modelConfigField;

}
