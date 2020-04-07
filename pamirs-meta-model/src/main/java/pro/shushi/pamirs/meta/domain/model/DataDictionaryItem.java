package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.enumclass.ActiveEnumCls;
import pro.shushi.pamirs.meta.enumclass.SystemSourceEnumCls;

/**
 * 数据字典项
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.DataDictionaryItem")
@Model(displayName = "数据字典项", summary = "数据字典项", pk = {"dictionary", "name"}, labelFields = "displayName")
public class DataDictionaryItem extends BaseModel {

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
    @Field(displayName = "颜色")
    private String color;

    @Base
    @Field.String
    @Field(displayName = "图标")
    private String icon;

    @Base
    @Field.String
    @Field(displayName = "扩展字段1")
    private String extend1;

    @Base
    @Field.String
    @Field(displayName = "扩展字段2")
    private String extend2;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "dictionary")
    @Field(displayName = "数据字典", required = true)
    private DataDictionary dataDictionary;

    @Base
    @Field(displayName = "字典编码")
    private String dictionary;

    @Base
    @Field.Enum
    @Field(displayName = "状态", defaultValue = "true")
    private ActiveEnumCls state;

    @Base
    @Field.Enum
    @Field(displayName = "来源", summary = "BASE是系统创建, MANUAL是人工创建", defaultValue = "MANUAL")
    private SystemSourceEnumCls source;

}
