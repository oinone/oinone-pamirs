package pro.shushi.pamirs.file.api.pmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * Excel模型字段
 *
 * @author Adamancy Zhang at 16:06 on 2025-09-08
 */
@Model.model(ExcelModelField.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "Excel模型字段")
public class ExcelModelField extends ModelField {

    private static final long serialVersionUID = -4498823018441297902L;

    public static final String MODEL_MODEL = "file.ExcelModelField";

    @Field.String
    @Field(displayName = "选项字段")
    private String optionLabel;
}
