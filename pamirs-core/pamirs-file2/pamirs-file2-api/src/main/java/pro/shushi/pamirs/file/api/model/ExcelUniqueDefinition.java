package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Base
@Model.model(ExcelUniqueDefinition.MODEL_MODEL)
@Model(displayName = "Excel唯一定义")
public class ExcelUniqueDefinition extends TransientModel {

    public static final String MODEL_MODEL = "file.ExcelUniqueDefinition";

    @Field(displayName = "模型编码")
    private String model;

    @Field(displayName = "唯一属性")
    private List<String> uniques;
}
