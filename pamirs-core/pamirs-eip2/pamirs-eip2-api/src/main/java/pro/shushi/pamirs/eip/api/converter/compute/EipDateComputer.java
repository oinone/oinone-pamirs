package pro.shushi.pamirs.eip.api.converter.compute;

import pro.shushi.pamirs.core.common.DateHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EIP日期计算函数
 *
 * @author Adamancy Zhang at 14:22 on 2021-06-16
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class EipDateComputer {

    public static final String DATE_FORMAT_TZ = EipFunctionConstant.COMPUTER_PREFIX + "dateFormatTz";

    @Function.fun(DATE_FORMAT_TZ)
    @Function(name = DATE_FORMAT_TZ, summary = "格式化形如\"yyyy-MM-dd'T'HH:mm:ss.SS'Z'\"的日期数据", openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public Object dateFormatTz(IEipContext<?> context, IEipConvertParam<?> convertParam, List<AtomicInteger> inParamCounterList, Object object) {
        try {
            return DateHelper.parse(StringHelper.valueOf(object), "yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
        } catch (ParseException ignored) {
        }
        return object;
    }
}
