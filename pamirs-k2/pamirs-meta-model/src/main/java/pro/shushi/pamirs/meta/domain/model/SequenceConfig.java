package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.TimePeriodEnum;
import pro.shushi.pamirs.meta.util.DiffUtils;

import static pro.shushi.pamirs.meta.domain.model.SequenceConfig.MODEL_MODEL;

/**
 * 序列生成配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:55 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(99)
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(unique = {"code"}, priority = 11)
@Model(displayName = "序列生成配置", summary = "序列生成配置", labelFields = {"prefix", "separator", "suffix"})
public class SequenceConfig extends MetaBaseModel {

    public static final String MODEL_MODEL = "base.SequenceConfig";
    private static final long serialVersionUID = -6279655310595792874L;

    @Field.String(size = 512)
    @Field(displayName = "显示名称")
    private String displayName;

    @Base
    @Field.String
    @Field(displayName = "描述摘要")
    private String summary;

    @Base
    @Field.String
    @Field(displayName = "模块编码")
    private String module;

    @Base
    @Field.String(size = 256)
    @Field(displayName = "编码", unique = true)
    private String code;

    @Base
    @Field.String
    @Field(displayName = "前缀")
    private String prefix;

    @Base
    @Field.String
    @Field(displayName = "后缀")
    private String suffix;

    @Base
    @Field.Integer
    @Field(displayName = "长度")
    private Integer size;

    @Base
    @Field.String
    @Field(displayName = "日期格式", summary = "精确到日期，不包含时间")
    private String format;

    @Base
    @Field.Integer
    @Field(displayName = "步长")
    private Integer step;

    @Base
    @Field.Boolean
    @Field(displayName = "是否随机步长", defaultValue = "false")
    private Boolean isRandomStep;

    @Base
    @Field.Integer
    @Field(displayName = "初始序列", invisible = true)
    private Long initial;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"#sequence#", "sequence"}, referenceFields = {"namespace", "fun"}, domain = "namespace=eq=sequence,ttype=eq=@{argTtypes}")
    @Field(summary = "序列生成器函数", displayName = "序列生成器函数")
    private FunctionDefinition sequenceFunction;

    @Base
    @Field.String
    @Field(displayName = "序列生成器", summary = "序列生成器")
    private String sequence;

    @Base
    @Field.Enum
    @Field(displayName = "归零周期", summary = "归零周期")
    private TimePeriodEnum zeroingPeriod;

    @Base
    @Field.Enum
    @Field(displayName = "可见", defaultValue = "true")
    private ActiveEnum show;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = MetaDefaultConstants.PRIORITY_VALUE_STRING)
    private Long priority;

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "sequenceFunction");
    }

}
