package pro.shushi.pamirs.framework.faas.script.engine;

import com.google.common.collect.Sets;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.framework.faas.guard.ScriptInvokeGuard;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;
import pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum;
import pro.shushi.pamirs.meta.util.ExpressionUtils;

import javax.script.ScriptEngine;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Groovy脚本引擎
 *
 * @author Adamancy Zhang at 16:15 on 2024-07-17
 */
public class GroovyScriptEngine extends AbstractScriptEngine implements PamirsScriptEngine {

    public GroovyScriptEngine() {
        super(FunctionLanguageEnum.GROOVY.value().toLowerCase());
    }

    public GroovyScriptEngine(ScriptEngine origin) {
        super(origin);
    }

    @Override
    protected ScriptEngine generatorDefaultScriptEngine() {
        return new GroovyScriptEngineImpl(AccessController.doPrivileged(new PrivilegedAction<GroovyClassLoader>() {
            @Override
            public GroovyClassLoader run() {
                return new GroovyClassLoader(AppClassLoader.getClassLoader(PamirsScriptEngine.class), new CompilerConfiguration(CompilerConfiguration.DEFAULT));
            }
        }));
    }

    @Override
    protected String prepare(String expression) {
        if (StringUtils.isBlank(expression)) {
            return expression;
        }
        // 先过滤字符串
        Map<String, String> replaceMap = new HashMap<>();
        expression = ExpressionUtils.ignoreString(expression, replaceMap);
        // 获取表达式中所有函数名
        List<String> funs = ExpressionUtils.fetchFunQuote(expression);
        Set<String> funSet = Sets.newHashSet(funs);
        for (String fun : funSet) {
            String methodSign = StringUtils.substringBeforeLast(fun, "(");
            Function function = PamirsSession.getContext().getFunctionAllowNull(NamespaceConstants.expression, methodSign);
            if (null == function) {
                throw PamirsException.construct(FaasExpEnumerate.BASE_EXPRESSION_NOT_EXIST_FUN_ERROR).appendMsg("函数名: " + methodSign).errThrow();
            }
            ScriptInvokeGuard.judgeAllow(function);
            expression = expression.replace(methodSign + "(", ExpressionUtils.fetchJavaPlaceholder(function));
        }
        // 替换逻辑运算符
        expression = ExpressionUtils.replaceLogical(expression);
        // 回填字符串
        for (String key : replaceMap.keySet()) {
            expression = expression.replace(key, replaceMap.get(key));
        }
        return expression;
    }
}
