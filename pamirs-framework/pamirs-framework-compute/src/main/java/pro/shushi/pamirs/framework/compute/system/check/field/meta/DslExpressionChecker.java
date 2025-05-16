package pro.shushi.pamirs.framework.compute.system.check.field.meta;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.Checker;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * DSL表达式格式检查
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 1:16 下午
 */
@Fun
public class DslExpressionChecker implements Checker<String>, MetaCheckConstants {

    @Function.Advanced(displayName = "校验DSL格式", type = FunctionTypeEnum.QUERY)
    @Function.fun(checkDslExpression)
    @Override
    public Boolean check(String value) {
        return true;
    }

}
