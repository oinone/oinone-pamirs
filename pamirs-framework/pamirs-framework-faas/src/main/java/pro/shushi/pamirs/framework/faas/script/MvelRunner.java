package pro.shushi.pamirs.framework.faas.script;

import org.mvel2.MVEL;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

import java.util.Map;

/**
 * Mvel执行器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
public class MvelRunner {

    public static Object run(Function function, Object args) {
        String expression = function.getCodes();
        Map<String, Object> context = ScriptRunner.makeContext(function, args);
        return MVEL.eval(expression, context);
    }

}
