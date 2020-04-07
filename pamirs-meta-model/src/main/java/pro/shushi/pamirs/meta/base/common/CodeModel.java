package pro.shushi.pamirs.meta.base.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.orm.EnhanceApi;
import pro.shushi.pamirs.meta.api.core.systems.type.gen.SequenceProcessor;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.FunctionUsageEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * id为主键且带code的模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.CodeModel")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "带code模型抽象基类", summary = "带code模型抽象基类")
public abstract class CodeModel extends IdModel {

    @Base
    @Field.Integer(sequence = "AUTO_INCREMENT", M = 20)
    @Field.Advanced(priority = 5)
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true)
    protected Long id;

    @Base
    @Field.String
    @Field(displayName = "编码", unique = true)
    private String code;

    @Function.Advanced(usage = FunctionUsageEnum.WRITE)
    @Function(summary = "编码生成函数")
    public <T> T generateCode(T data){
        return null;
    }

}
