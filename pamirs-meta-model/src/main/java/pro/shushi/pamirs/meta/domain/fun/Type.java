package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enumclass.TtypeEnumCls;

/**
 * 函数参数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model.model("base.Type")
@Model(displayName = "类型", summary = "类型")
public class Type extends D {

    @Base
    @Field.Enum
    @Field(displayName = "类型", required = true)
    private TtypeEnumCls ttype;

    @Base
    @Field.Boolean
    @Field(displayName = "多值类型", required = true)
    private Boolean multi;

    @Base
    @Field.String(size = 128)
    @Field(displayName = "Java类型", invisible = true)
    private String ltype;

    @Base
    @Field.String(size = 128)
    @Field(displayName = "Java范型实参", invisible = true)
    private String ltypeT;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "model")
    @Field(displayName = "模型")
    private ModelDefinition modelDefinition;

    @Base
    @Field.Related(related = {"modelDefinition","model"})
    @Field.String(size = 128)
    @Field(displayName = "模型编码")
    private String model;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "dictionary")
    @Field(displayName = "数据字典")
    private DataDictionary selection;

    @Base
    @Field(displayName = "枚举", invisible = true)
    private String dictionary;

}
