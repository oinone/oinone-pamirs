package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * Excel国际化配置项
 *
 * @author Adamancy Zhang at 15:56 on 2024-06-01
 */
@Base
@Model.model(ExcelLocation.MODEL_MODEL)
@Model.Advanced(unique = {"model,name,lang"})
@Model(displayName = "Excel国际化配置")
public class ExcelLocation extends IdModel {

    private static final long serialVersionUID = 4784408217596688048L;

    public static final String MODEL_MODEL = "excel.ExcelLocation";

    @Field.String
    @Field(displayName = "模型编码")
    private String model;

    @Field.String
    @Field(displayName = "模板名称")
    private String name;

    @Field.String
    @Field(displayName = "语言")
    private String lang;

    @Field.one2many
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field.Relation(store = false)
    @Field(displayName = "Excel国际化配置项", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private List<ExcelLocationItem> locationItems;
}
