package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;

/**
 * 序列生成配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:55 下午
 */
@Base
@Model.Advanced(unique = {"name,model","column,model"})
@Model.model("base.SequenceConfig")
@Model(displayName = "序列生成配置", summary = "序列生成配置", labelFields = {"prefix","separator","suffix"})
public class SequenceConfig extends TransientModel {

    @Base
    @Field.String
    @Field(displayName = "前缀")
    private String prefix;

    @Base
    @Field.String
    @Field(displayName = "后缀")
    private String suffix;

    @Base
    @Field.String
    @Field(displayName = "分隔符")
    private String separator;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"#sequence#", "sequence"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=sequence,ttype=eq=@{argTtypes}")
    @Field(summary = "序列生成器函数", displayName = "序列生成器函数")
    private FunctionDefinition sequenceFunction;

    @Base
    @Field.String
    @Field(displayName = "序列生成器", summary = "序列生成器")
    private String sequence;

}
