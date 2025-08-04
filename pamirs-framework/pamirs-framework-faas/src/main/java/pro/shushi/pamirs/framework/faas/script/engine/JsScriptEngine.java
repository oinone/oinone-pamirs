package pro.shushi.pamirs.framework.faas.script.engine;

import pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum;

import javax.script.ScriptEngine;

/**
 * JS脚本引擎
 *
 * @author Adamancy Zhang at 16:17 on 2024-07-17
 */
@Deprecated
public class JsScriptEngine extends AbstractScriptEngine implements PamirsScriptEngine {

    private static final String[] DEFAULT_OPTIONS = new String[]{"-doe"};

    public JsScriptEngine() {
        super(FunctionLanguageEnum.JS.value().toLowerCase());
    }

    public JsScriptEngine(ScriptEngine origin) {
        super(origin);
    }

}
