package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enumclass.SystemSourceEnumCls;
import pro.shushi.pamirs.meta.enumclass.TtypeEnumCls;

import java.util.List;

/**
 * 数据字典
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaModel(priority = 7)
@Base
@Model.model("base.DataDictionary")
@Model(displayName = "数据字典", summary = "数据字典", labelFields = "displayName", check = {"checkModuleName(module)"})
public class DataDictionary extends IdModel {

    @Base
    @Field.String
    @Field(displayName = "应用", summary = "应用模块", required = true)
    private String module;

    @Base
    @Field.String
    @Field(displayName = "字典名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "字典编码", required = true)
    private String dictionary;

    @Base
    @Field.String
    @Field(displayName = "技术名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "枚举代码名称")
    private String lname;

    @Base
    @Field.Enum
    @Field(displayName = "枚举类型")
    private TtypeEnumCls valueType;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "dictionary")
    @Field(displayName = "数据字典项", store = NullableBoolEnum.TRUE)
    private List<DataDictionaryItem> itemList;

    @Base
    @Field.Enum
    @Field(displayName = "来源", summary = "BASE是系统创建, MANUAL是人工创建", defaultValue = "MANUAL")
    private SystemSourceEnumCls source;

}
