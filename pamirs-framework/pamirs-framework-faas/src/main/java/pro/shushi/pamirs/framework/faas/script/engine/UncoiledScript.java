package pro.shushi.pamirs.framework.faas.script.engine;

import javax.script.*;

/**
 * Uncoiled Script
 *
 * @author Adamancy Zhang at 17:32 on 2024-07-17
 */
public final class UncoiledScript extends CompiledScript {

    private final ScriptEngine scriptEngine;

    private final String script;

    public UncoiledScript(ScriptEngine scriptEngine, String script) {
        this.scriptEngine = scriptEngine;
        this.script = script;
    }

    @Override
    public Object eval(Bindings bindings) throws ScriptException {
        return this.scriptEngine.eval(script, bindings);
    }

    @Override
    public Object eval() throws ScriptException {
        return this.scriptEngine.eval(script);
    }

    @Override
    public Object eval(ScriptContext context) throws ScriptException {
        return this.scriptEngine.eval(script, context);
    }

    @Override
    public ScriptEngine getEngine() {
        return scriptEngine;
    }
}
