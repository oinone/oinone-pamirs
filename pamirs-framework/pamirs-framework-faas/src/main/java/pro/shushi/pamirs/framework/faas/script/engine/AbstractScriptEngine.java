package pro.shushi.pamirs.framework.faas.script.engine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.mvel2.jsr223.MvelScriptEngine;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.util.AppClassLoader;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 抽象脚本执行引擎
 *
 * @author Adamancy Zhang at 15:47 on 2024-07-17
 */
@Slf4j
public abstract class AbstractScriptEngine implements PamirsScriptEngine {

    private static final Map<String, ScriptEngine> CACHE = new ConcurrentHashMap<>(4);

    protected final ScriptEngine scriptEngine;

    protected final Compilable compilableEngine;

    protected final Cache<String, CompiledScript> compiledScriptCache;

    protected AbstractScriptEngine(String type) {
        this(init(type));
    }

    protected AbstractScriptEngine(ScriptEngine scriptEngine) {
        if (scriptEngine == null) {
            scriptEngine = generatorDefaultScriptEngine();
        }
        this.scriptEngine = scriptEngine;
        if (scriptEngine instanceof Compilable) {
            this.compilableEngine = (Compilable) scriptEngine;
            this.compiledScriptCache = Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(5, TimeUnit.MINUTES).build();
        } else {
            this.compilableEngine = null;
            this.compiledScriptCache = null;
        }
    }

    private static ScriptEngine init(String type) {
        return CACHE.computeIfAbsent(type, (k) -> {
            ScriptEngine engine = new ScriptEngineManager(AppClassLoader.getClassLoader(PamirsScriptEngine.class)).getEngineByName(k);
            if (log.isInfoEnabled()) {
                String engineClassName = null;
                if (engine != null) {
                    engineClassName = engine.getClass().getName();
                }
                log.info("load script engine {}: {}", k, engineClassName);
            }
            return engine;
        });
    }

    protected ScriptEngine generatorDefaultScriptEngine() {
        return null;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        script = prepare(script);
        return compile(script).eval(context);
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        String script = readFully(reader);
        return eval(script, context);
    }

    @Override
    public Object eval(String script) throws ScriptException {
        script = prepare(script);
        return compile(script).eval();
    }

    @Override
    public Object eval(Reader reader) throws ScriptException {
        String script = readFully(reader);
        return eval(script);
    }

    @Override
    public Object eval(String script, Bindings n) throws ScriptException {
        script = prepare(script);
        return compile(script).eval(n);
    }

    @Override
    public Object eval(Reader reader, Bindings n) throws ScriptException {
        String script = readFully(reader);
        return eval(script, n);
    }

    @Override
    public Object eval(String script, Map<String, Object> context) throws ScriptException {
        return eval(script, contextToBindings(context));
    }

    @Override
    public Object eval(Reader reader, Map<String, Object> context) throws ScriptException {
        return eval(reader, contextToBindings(context));
    }

    @Override
    public void put(String key, Object value) {
        this.scriptEngine.put(key, value);
    }

    @Override
    public Object get(String key) {
        return this.scriptEngine.get(key);
    }

    @Override
    public Bindings getBindings(int scope) {
        return this.scriptEngine.getBindings(scope);
    }

    @Override
    public void setBindings(Bindings bindings, int scope) {
        this.scriptEngine.setBindings(bindings, scope);
    }

    @Override
    public Bindings createBindings() {
        return this.scriptEngine.createBindings();
    }

    @Override
    public ScriptContext getContext() {
        return this.scriptEngine.getContext();
    }

    @Override
    public void setContext(ScriptContext context) {
        this.scriptEngine.setContext(context);
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return this.scriptEngine.getFactory();
    }

    protected String prepare(String script) {
        return script;
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        if (this.compilableEngine == null) {
            return new UncoiledScript(this.scriptEngine, script);
        }
        CompiledScript compiledScript = this.compiledScriptCache.getIfPresent(script);
        if (compiledScript == null) {
            compiledScript = this.compilableEngine.compile(script);
            this.compiledScriptCache.put(script, compiledScript);
        }
        return compiledScript;
    }

    @Override
    public CompiledScript compile(Reader reader) throws ScriptException {
        String script = readFully(reader);
        return compile(script);
    }

    protected Bindings contextToBindings(Map<String, Object> context) {
        Bindings bindings = createBindings();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            bindings.put(entry.getKey(), entry.getValue());
        }
        return bindings;
    }

    /**
     * Read fully script string.
     *
     * @param reader Script String Reader
     * @return Script String
     * @throws ScriptException Script Read Exception
     * @see MvelScriptEngine#readFully(java.io.Reader)
     */
    private static String readFully(Reader reader) throws ScriptException {
        char[] arr = new char[8192];
        StringBuilder buf = new StringBuilder();

        int numChars;
        try {
            while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
                buf.append(arr, 0, numChars);
            }
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return buf.toString();
    }
}
