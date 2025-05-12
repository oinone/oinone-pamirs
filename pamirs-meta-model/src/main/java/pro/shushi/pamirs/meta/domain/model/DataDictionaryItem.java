package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.Map;

import static pro.shushi.pamirs.meta.domain.model.DataDictionaryItem.MODEL_MODEL;

/**
 * 数据字典项
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.Advanced(priority = 13)
@Model.model(MODEL_MODEL)
@Model(displayName = "数据字典项", summary = "数据字典项", labelFields = "displayName")
public class DataDictionaryItem extends TransientModel {

    private static final long serialVersionUID = -7972557422608296515L;

    public static final String MODEL_MODEL = "base.DataDictionaryItem";

    @Base
    @Field.String
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "值", required = true)
    private String value;

    @Base
    @Field.String
    @Field(displayName = "帮助", translate = true)
    private String help;

    @Base
    @Field.String
    @Field(displayName = "扩展属性")
    private Map<String, Object> attributes;

    @Base
    @Field.Enum
    @Field(displayName = "状态", defaultValue = "true")
    private ActiveEnum state;

    @Base
    @Field.Enum
    @Field(displayName = "来源", summary = "BASE是系统创建, MANUAL是人工创建", defaultValue = "MANUAL")
    private SystemSourceEnum source;

}
