package pro.shushi.pamirs.eip.api.converter.compute;

import pro.shushi.pamirs.core.common.NumberHelper;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.util.EipParamConverterHelper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EIP数字计算函数
 *
 * @author Adamancy Zhang on 2021-02-24 20:43
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class EipNumberComputer {

    public static final String SUM = EipFunctionConstant.COMPUTER_PREFIX + "sum";

    public static final String BIG_DECIMAL_VALUE_OF = EipFunctionConstant.COMPUTER_PREFIX + "bigDecimalValueOf";

    public static final String LONG_VALUE_OF = EipFunctionConstant.COMPUTER_PREFIX + "longValueOf";

    public static final String INT_VALUE_OF = EipFunctionConstant.COMPUTER_PREFIX + "intValueOf";

    @Function.fun(SUM)
    @Function(name = SUM, openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public Object sum(IEipContext<?> context, IEipConvertParam<?> convertParam, List<AtomicInteger> inParamCounterList, Object object) {
        String outParam = EipParamConverterHelper.getFinalParameter(convertParam.getOutParam(), inParamCounterList);
        Object originSum = EipParamConverterHelper.getContextValue(convertParam.getTargetContextType(), context, outParam);
        BigDecimal targetSum = NumberHelper.valueOf(originSum);
        return targetSum.add(NumberHelper.valueOf(object));
    }

    @Function.fun(BIG_DECIMAL_VALUE_OF)
    @Function(name = BIG_DECIMAL_VALUE_OF, openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public Object bigDecimalValueOf(IEipContext<?> context, IEipConvertParam<?> convertParam, List<AtomicInteger> inParamCounterList, Object object) {
        BigDecimal value = NumberHelper.valueOfNullable(object);
        if (value != null) {
            return value;
        }
        return object;
    }

    @Function.fun(LONG_VALUE_OF)
    @Function(name = LONG_VALUE_OF, openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public Object longValueOf(IEipContext<?> context, IEipConvertParam<?> convertParam, List<AtomicInteger> inParamCounterList, Object object) {
        Long value = NumberHelper.longValueOfNullable(object);
        if (value != null) {
            return value;
        }
        return object;
    }

    @Function.fun(INT_VALUE_OF)
    @Function(name = INT_VALUE_OF, openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public Object intValueOf(IEipContext<?> context, IEipConvertParam<?> convertParam, List<AtomicInteger> inParamCounterList, Object object) {
        Integer value = NumberHelper.intValueOfNullable(object);
        if (value != null) {
            return value;
        }
        return object;
    }
}
