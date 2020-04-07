package pro.shushi.pamirs.meta.domain.fun;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * 函数定义抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model.model("base.AbstractFunction")
@Model(displayName = "函数抽象基类", summary = "函数抽象基类")
public class AbstractFunction extends IdModel {

    @Base
    @Field.String
    @Field(displayName = "函数位置")
    private String clazz;

    @Base
    @Field.String
    @Field(displayName = "函数方法")
    private String method;

    @Base
    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "函数参数", store = NullableBoolEnum.TRUE)
    private List<Argument> argumentList;

    @Base
    @Field.many2one
    @Field.Relation(store = false)
    @Field(displayName = "返回值类型", store = NullableBoolEnum.TRUE)
    private Type returnType;

}
