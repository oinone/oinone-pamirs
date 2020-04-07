package pro.shushi.pamirs.meta.domain.fun;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 校验表达式
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.Advanced(inherited = "base.IdModel")
@Model.model("base.CheckExpression")
@Model(displayName = "校验表达式", summary = "校验表达式")
public class CheckExpression extends ExpressionDefinition {

    @Base
    @Field.String
    @Field(displayName = "表达式", summary = "表达式", check = "checkDslExpression", required = true)
    private String expression;

    @Base
    @Field.String
    @Field(displayName = "校验规则", summary = "校验规则", check = "checkRuleExpression", watch = {"expression","message"}, inverse = "inverseRule")
    private String rule;

    @Function
    public CheckExpression construct(CheckExpression checkExpression){
        inverseRule(checkExpression);
        if(StringUtils.isNotBlank(checkExpression.getExpression()) && StringUtils.isNotBlank(checkExpression.getMessage())){
            checkExpression.setRule(checkExpression.getExpression() + CharacterConstants.SEPARATOR_COLON + checkExpression.getMessage());
        }
        return checkExpression;
    }

    @Function
    public CheckExpression inverseRule(CheckExpression checkExpression){
        if(StringUtils.isNotBlank(checkExpression.getRule())){
            String[] rules = checkExpression.getRule().split(CharacterConstants.SEPARATOR_COLON);
            if(rules.length > 0){
                checkExpression.setExpression(rules[0]);
            }else if(rules.length > 1){
                checkExpression.setMessage(rules[1]);
            }
        }
        return checkExpression;
    }

}
