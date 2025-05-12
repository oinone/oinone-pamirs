package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.meta.domain.fun.ExtPoint.MODEL_MODEL;

/**
 * 扩展点
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 11)
@Base
@Model.Advanced(unique = {"namespace,name"}, priority = 24)
@Model.model(MODEL_MODEL)
@Model(displayName = "扩展点", summary = "扩展点")
public class ExtPoint extends AbstractFunction {

    public static final String MODEL_MODEL = "base.ExtPoint";

    private static final long serialVersionUID = 3093451784165084003L;

    @Base
    @Field.String
    @Field(displayName = "显示名称")
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "api名称", summary = "api名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "命名空间", summary = "所扩展函数的命名空间", invisible = true)
    private String namespace;

    @Base
    @Field.String
    @Field(displayName = "描述", summary = "描述")
    private String description;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = {"namespace", "name"})
    @Field(displayName = "扩展点实现")
    private List<ExtPointImplementation> instances;

    @Base
    @Field(displayName = "是否激活", defaultValue = "true")
    private Boolean active;

    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public ExtPoint construct(ExtPoint extPoint) {
        extPoint.setName(Optional.ofNullable(extPoint.getName())
                .orElse(ExtPoint.class.getSimpleName() + CharacterConstants.SEPARATOR_UNDERLINE + UUIDUtil.getUUIDNumberString()))
        ;
        return extPoint;
    }

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

}

