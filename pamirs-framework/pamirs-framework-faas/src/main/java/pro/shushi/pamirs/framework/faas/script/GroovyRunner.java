package pro.shushi.pamirs.framework.faas.script;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Groovy执行器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
public class GroovyRunner {

    public static <R> R run(Function function, Object... args) {
        StringBuilder methodSignString = new StringBuilder("Object run(");
        List<String> argSigns = new ArrayList<>();
        if (null != function.getArguments()) {
            for (Arg arg : function.getArguments()) {
                argSigns.add((StringUtils.isBlank(arg.getLtype()) ? "" : arg.getLtype() + " ") + arg.getName());
            }
        }
        methodSignString.append(StringUtils.join(argSigns, ",")).append("){").append(function.getCodes()).append("}");
        String script = "class GroovyRunner {\n" +
                methodSignString + "\n" +
                "}";
        if (StringUtils.isNotBlank(script)) {
            try {
                //noinspection rawtypes
                Class clazz = new GroovyClassLoader().parseClass(script);
                GroovyObject o = (GroovyObject) clazz.newInstance();
                //noinspection unchecked
                return (R) o.invokeMethod("run", args);
            } catch (IllegalAccessException e) {
                throw PamirsException.construct(FaasExpEnumerate.BASE_ILLEGAL_ACCESS_ERROR, e).errThrow();
            } catch (InstantiationException e) {
                throw PamirsException.construct(FaasExpEnumerate.BASE_INSTANTIATION_ERROR, e).errThrow();
            } catch (Exception e) {
                throw PamirsException.construct(FaasExpEnumerate.BASE_SCRIPT_INVOKE_ERROR, e).errThrow();
            }
        }
        return null;
    }

    public static Script parse(String scriptString) {
        GroovyShell groovyShell = new GroovyShell();

        String scriptMd5 = null;
        try {
            scriptMd5 = DigestUtils.md5DigestAsHex(scriptString.getBytes());
        } catch (Exception e) {
            log.error("脚本解析错误：" + scriptString, e);
        }
        Script script;
        if (scriptMd5 == null) {
            script = groovyShell.parse(scriptString);
        } else {
            String finalScriptMd5 = scriptMd5;
            script = GroovyCache.getValue(GroovyCache.GROOVY_SHELL_KEY_PREFIX + scriptMd5,
                    () -> Optional.ofNullable(groovyShell.parse(scriptString, generateScriptName(finalScriptMd5))));
            if (script == null) {
                script = groovyShell.parse(scriptString, generateScriptName(finalScriptMd5));
            }
        }
        return script;
    }

    private static String generateScriptName(String scriptName) {
        return "Script" + scriptName + ".groovy";
    }

}
