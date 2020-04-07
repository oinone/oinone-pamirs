package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import java.util.List;
import java.util.Optional;

/**
 * 扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaModel(priority = 11)
@Base
@Model.model("base.ExtPoint")
@Model(displayName = "扩展点", summary = "扩展点")
public class ExtPoint extends AbstractFunction {

    @Base
    @Field.String
    @Field(displayName = "显示名称")
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "技术名称", summary = "技术名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "命名空间", summary = "所属函数命名空间", invisible = true)
    private String namespace;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述")
    private String description;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"namespace", "fun"})
    @Field(displayName = "函数", invisible = true)
    private FunctionDefinition functionDefinition;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"namespace", "name"})
    @Field(displayName = "扩展点实现")
    private List<ExtPointInstance> instances;

    @Function
    public ExtPoint construct(ExtPoint extPoint){
        extPoint.setName(Optional.ofNullable(extPoint.getName())
                .orElse(ExtPoint.class.getSimpleName() + CharacterConstants.SEPARATOR_UNDERLINE + UUIDUtil.getUUIDNumberString()))
        ;
        return extPoint;
    }

}

