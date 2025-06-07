package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

import static pro.shushi.pamirs.meta.domain.fun.AbstractFunction.MODEL_MODEL;

/**
 * 函数定义抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 16)
@Model.model(MODEL_MODEL)
@Model(displayName = "函数抽象基类", summary = "函数抽象基类")
public abstract class AbstractFunction extends MetaBaseModel {

    private static final long serialVersionUID = -296300787527947568L;

    public static final String MODEL_MODEL = "base.AbstractFunction";

    @Base
    @Field.String
    @Field(displayName = "函数位置")
    private String clazz;

    @Base
    @Field.String
    @Field(displayName = "函数方法")
    private String method;

    @Base
    @Field.Advanced(columnDefinition = "varchar(3072)")
    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "函数参数", store = NullableBoolEnum.TRUE)
    private List<Argument> argumentList;

    @Base
    @Field.Advanced(columnDefinition = "varchar(512)")
    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "返回值类型", store = NullableBoolEnum.TRUE)
    private Type returnType;

}
