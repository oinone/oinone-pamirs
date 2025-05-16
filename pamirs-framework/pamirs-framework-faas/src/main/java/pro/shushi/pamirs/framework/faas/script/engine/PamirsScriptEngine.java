package pro.shushi.pamirs.framework.faas.script.engine;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.Map;

/**
 * PamirsScriptEngine
 *
 * @author Adamancy Zhang at 16:26 on 2024-07-17
 */
public interface PamirsScriptEngine extends ScriptEngine, Compilable {

    /**
     * execute the script using the <code>Map</code> argument as the <code>ENGINE_SCOPE</code>
     *
     * @param script  The source for the script.
     * @param context The <code>Map</code> of attributes to be used for script execution.
     * @return The value returned by the script.
     * @throws ScriptException      if an error occurs in script.
     * @throws NullPointerException if either argument is null.
     */
    Object eval(String script, Map<String, Object> context) throws ScriptException;

    /**
     * Same as <code>eval(String, Map)</code> except that the source of the script
     * is provided as a <code>Reader</code>.
     *
     * @param reader  The source of the script.
     * @param context The <code>Map</code> of attributes to be used for script execution.
     * @return The value returned by the script.
     * @throws ScriptException      if an error occurs in script.
     * @throws NullPointerException if either argument is null.
     */
    Object eval(Reader reader, Map<String, Object> context) throws ScriptException;

}
