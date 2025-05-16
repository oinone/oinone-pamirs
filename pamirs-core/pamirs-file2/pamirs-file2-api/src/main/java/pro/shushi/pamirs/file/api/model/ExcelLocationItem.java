package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * Excel国际化配置项
 *
 * @author Adamancy Zhang at 15:51 on 2024-06-01
 */
@Base
@Model.model(ExcelLocationItem.MODEL_MODEL)
@Model(displayName = "Excel国际化配置项")
public class ExcelLocationItem extends TransientModel {

    private static final long serialVersionUID = 4784408217596688048L;

    public static final String MODEL_MODEL = "file.ExcelLocationItem";

    @Field.String
    @Field(displayName = "原始值")
    private String origin;

    @Field.String
    @Field(displayName = "翻译值")
    private String target;
}
