package pro.shushi.pamirs.meta.domain.fun;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

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
@Model(displayName = "类型", summary = "类型", labelFields = {"ttype", "model"})
public class Type extends TransientModel {

    private static final long serialVersionUID = 8059441897085851578L;

    @Base
    @XStreamAsAttribute
    @Field.Enum
    @Field(displayName = "类型", required = true)
    private TtypeEnum ttype;

    @Base
    @XStreamAsAttribute
    @Field.Boolean
    @Field(displayName = "多值类型", required = true)
    private Boolean multi;

    @Base
    @Field.Boolean
    @Field(displayName = "泛化模型")
    private Boolean modelGeneric;

    @Base
    @Field.String
    @Field(displayName = "Java类型", invisible = true)
    private String ltype;

    @Base
    @Field.String
    @Field(displayName = "Java范型实参", invisible = true)
    private String ltypeT;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "model", referenceFields = "model")
    @Field(displayName = "模型")
    private ModelDefinition modelDefinition;

    @Base
    @Field.Related(related = {"modelDefinition", "model"})
    @Field.String
    @Field(displayName = "模型编码", store = NullableBoolEnum.TRUE)
    private String model;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "dictionary", referenceFields = "dictionary")
    @Field(displayName = "数据字典")
    private DataDictionary selection;

    @Base
    @Field(displayName = "枚举", invisible = true)
    private String dictionary;

}
