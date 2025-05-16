package pro.shushi.pamirs.framework.faas.script.engine;

import org.mvel2.jsr223.MvelScriptEngineFactory;
import pro.shushi.pamirs.framework.faas.spi.api.expression.ExpressionFunctionFetchApi;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * MVEL脚本引擎
 *
 * @author Adamancy Zhang at 15:46 on 2024-07-17
 */
@Slf4j
public class MvelScriptEngine extends AbstractScriptEngine implements PamirsScriptEngine {

    private static Map<String, Method> PAMIRS_FUNCTIONS = null;

    public MvelScriptEngine() {
        super(FunctionLanguageEnum.MVEL.value().toLowerCase());
    }

    public MvelScriptEngine(ScriptEngine origin) {
        super(origin);
    }

    @Override
    protected ScriptEngine generatorDefaultScriptEngine() {
        return new MvelScriptEngineFactory().getScriptEngine();
    }

    @Override
    public Bindings createBindings() {
        Bindings bindings = super.createBindings();
        fillFunctionToBindings(bindings);
        return bindings;
    }

    private void fillFunctionToBindings(Bindings bindings) {
        if (ScriptEngineSprintSupport.isUnsupported()) {
            return;
        }
        Map<String, Method> functions = getExpressionFunctions();
        if (functions != null) {
            bindings.putAll(functions);
        }
    }

    private Map<String, Method> getExpressionFunctions() {
        if (PAMIRS_FUNCTIONS == null) {
            synchronized (MvelScriptEngine.class) {
                if (PAMIRS_FUNCTIONS == null) {
                    List<FunctionDefinition> expressionFunctions = Spider.getDefaultExtension(ExpressionFunctionFetchApi.class).fetch();
                    Map<String, Method> functions = new HashMap<>(expressionFunctions.size());
                    Map<String, MethodSearchCache> expressionFunctionClassMap = new HashMap<>(8);
                    for (FunctionDefinition expressionFunction : expressionFunctions) {
                        functions.computeIfAbsent(expressionFunction.getFun(), (fun) -> {
                            MethodSearchCache methodSearchCache = expressionFunctionClassMap.computeIfAbsent(expressionFunction.getClazz(), k -> {
                                try {
                                    return new MethodSearchCache(Class.forName(k));
                                } catch (ClassNotFoundException e) {
                                    log.error("expression function class error.", e);
                                }
                                return null;
                            });
                            if (methodSearchCache == null) {
                                return null;
                            }
                            return methodSearchCache.getMethod(expressionFunction.getMethod());
                        });
                    }
                    PAMIRS_FUNCTIONS = functions;
                }
            }
        }
        return PAMIRS_FUNCTIONS;
    }

    private static class MethodSearchCache {

        private final Map<String, Method> cache;

        private final Iterator<Method> iterator;

        public MethodSearchCache(Class<?> cls) {
            this.cache = new HashMap<>();
            this.iterator = Arrays.stream(cls.getMethods()).iterator();
        }

        public Method getMethod(String name) {
            Method method = this.cache.get(name);
            if (method != null) {
                return method;
            }
            while (iterator.hasNext()) {
                method = iterator.next();
                int m = method.getModifiers();
                if (Modifier.isPublic(m) && Modifier.isStatic(m) &&
                        method.getDeclaredAnnotation(Function.class) != null) {
                    String methodName = method.getName();
                    this.cache.put(methodName, method);
                    if (methodName.equals(name)) {
                        return method;
                    }
                }
            }
            return null;
        }
    }
}
