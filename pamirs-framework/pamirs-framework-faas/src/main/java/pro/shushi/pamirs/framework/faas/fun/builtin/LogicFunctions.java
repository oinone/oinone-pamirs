package pro.shushi.pamirs.framework.faas.fun.builtin;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.LOGIC;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;

/**
 * 逻辑函数
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(NamespaceConstants.expression)
public class LogicFunctions {

    @Function.Advanced(
            displayName = "条件函数", language = JAVA,
            builtin = true, category = LOGIC
    )
    @Function.fun("IF")
    @Function(name = "IF", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: IF(A,B,C)\n函数说明: 如果IF满足条件A，则返回B，否则返回C，支持多层嵌套IF函数"
    )
    public static Object ifElse(Boolean condition, Object trueResult, Object falseResult) {
        return Boolean.TRUE.equals(condition) ? trueResult : falseResult;
    }

    @Function.Advanced(
            displayName = "逻辑与", language = JAVA,
            builtin = true, category = LOGIC
    )
    @Function.fun("AND")
    @Function(name = "AND", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: AND(A,B)\n函数说明: 返回 条件A 逻辑与 条件B 的值"
    )
    public static Boolean and(Boolean a, Boolean b) {
        if (null == a || null == b) {
            return false;
        }
        return a && b;
    }

    @Function.Advanced(
            displayName = "逻辑或", language = JAVA,
            builtin = true, category = LOGIC
    )
    @Function.fun("OR")
    @Function(name = "OR", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: OR(A,B)\n函数说明: 返回 条件A 逻辑或 条件B 的值"
    )
    public static Boolean or(Boolean a, Boolean b) {
        if (null == a) {
            a = false;
        }
        if (null == b) {
            b = false;
        }
        return a || b;
    }

    @Function.Advanced(
            displayName = "逻辑非", language = JAVA,
            builtin = true, category = LOGIC
    )
    @Function.fun("NOT")
    @Function(name = "NOT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: NOT(A)\n函数说明: 返回 逻辑非 条件A 的值"
    )
    public static Boolean not(Boolean a) {
        if (null == a) {
            a = false;
        }
        return !a;
    }

}
