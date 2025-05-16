package pro.shushi.pamirs.framework.faas.script.engine;

import javax.script.ScriptEngine;

/**
 * 通用ScriptEngine
 *
 * @author Adamancy Zhang at 15:52 on 2024-07-17
 */
public class CommonScriptEngine extends AbstractScriptEngine implements PamirsScriptEngine {

    public CommonScriptEngine(String type) {
        super(type);
    }

    public CommonScriptEngine(ScriptEngine origin) {
        super(origin);
    }
}
