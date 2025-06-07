package pro.shushi.pamirs.core.common.test.script;

import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.junit.jupiter.api.Test;
import org.mvel2.MVEL;
import org.mvel2.jsr223.MvelScriptEngine;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.core.common.entry.SimpleEntity;
import pro.shushi.pamirs.framework.faas.expression.api.DefaultExpressionApi;
import pro.shushi.pamirs.framework.faas.script.ScriptRunner;
import pro.shushi.pamirs.meta.api.core.faas.ExpressionApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Adamancy Zhang at 18:58 on 2024-07-16
 */
public class ScriptRunnerTest {

    @Test
    public void test1() throws ScriptException {
        final String expression = "1 == 1";
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(FunctionLanguageEnum.MVEL.value().toLowerCase());
        Bindings bindings = scriptEngine.createBindings();
        ScriptContext context = scriptEngine.getContext();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            // 快
            // scriptEngine.eval(expression);
            // 慢
//            scriptEngine.eval(expression, bindings);

            SimpleScriptContext tempctxt = new SimpleScriptContext();
            tempctxt.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            tempctxt.setBindings(context.getBindings(ScriptContext.GLOBAL_SCOPE),
                    ScriptContext.GLOBAL_SCOPE);
            tempctxt.setWriter(context.getWriter());
            tempctxt.setReader(context.getReader());
            tempctxt.setErrorWriter(context.getErrorWriter());
            System.out.println(scriptEngine.eval(expression, tempctxt));
        }
        System.out.println("scriptEngine test: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test2() throws ScriptException {
        final String expression = "1 == 1";
        ExpressionApi expressionApi = new DefaultExpressionApi();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String finalExpression = expressionApi.pre(expression);
            Function function = new Function()
                    .setScriptType(ScriptType.EL)
                    .setCodes(finalExpression)
                    .setBitOptions(0L);
            ScriptRunner.run(function, new HashMap<>());
        }
        System.out.println("expressionApi test: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test3() throws ScriptException {
        final String expression = "1 == 1";
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(FunctionLanguageEnum.MVEL.value().toLowerCase());
        ExpressionApi expressionApi = new DefaultExpressionApi();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String finalExpression = expressionApi.pre(expression);
            Bindings bindings = scriptEngine.createBindings();
            CompiledScript compiledScript = ((Compilable) scriptEngine).compile(finalExpression);
            compiledScript.eval(bindings);
        }
        System.out.println("expressionApi test: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test4() {
        final String expression = "1 == 1";
        Map<String, Object> context = new SuperMap();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            MVEL.eval(expression, context);
        }
        System.out.println("MVEL test: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test5() {
        final String expression = "activeRecord.name";
        Map<String, Object> context = new SuperMap();
        SimpleEntity simpleEntity = new SimpleEntity("aaa");
        context.put("activeRecord", simpleEntity);
        System.out.println(MVEL.eval(expression, context));
    }

    @Test
    public void mvelTest() throws ScriptException, NoSuchMethodException {
        final String expression = "SUM(1,2)";
        MvelScriptEngine scriptEngine = (MvelScriptEngine) new ScriptEngineManager().getEngineByName(FunctionLanguageEnum.MVEL.value().toLowerCase());
        ScriptContext context = scriptEngine.getContext();

        Bindings bindings = scriptEngine.createBindings();
        bindings.put("SUM", ScriptRunnerTest.class.getMethod("computeSum", Integer.class, Integer.class));

        SimpleScriptContext tempctxt = new SimpleScriptContext();
        tempctxt.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        tempctxt.setBindings(context.getBindings(ScriptContext.GLOBAL_SCOPE), ScriptContext.GLOBAL_SCOPE);
        tempctxt.setReader(context.getReader());
        tempctxt.setWriter(context.getWriter());
        tempctxt.setErrorWriter(context.getErrorWriter());
        System.out.println(scriptEngine.eval(expression, tempctxt));
    }

    @Test
    public void groovyTest() throws ScriptException, NoSuchMethodException {
//        final String expression = "SUM(1,2)";
        final String expression = "activeRecords.id";
        GroovyScriptEngineImpl scriptEngine = (GroovyScriptEngineImpl) new ScriptEngineManager().getEngineByName(FunctionLanguageEnum.GROOVY.value().toLowerCase());
        ScriptContext context = scriptEngine.getContext();

        Bindings bindings = scriptEngine.createBindings();
        bindings.put("SUM", ScriptRunnerTest.class.getMethod("computeSum", Integer.class, Integer.class));

        SimpleScriptContext tempctxt = new SimpleScriptContext();
        tempctxt.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        tempctxt.setBindings(context.getBindings(ScriptContext.GLOBAL_SCOPE), ScriptContext.GLOBAL_SCOPE);
        tempctxt.setReader(context.getReader());
        tempctxt.setWriter(context.getWriter());
        tempctxt.setErrorWriter(context.getErrorWriter());
        System.out.println(scriptEngine.eval(expression, tempctxt));
    }

    public static Integer computeSum(Integer a, Integer b) {
        return a + b;
    }

    public static Long computeSum(Long a, Long b) {
        return a + b;
    }

    private void t() {
//        if ("CONCAT(LIST_FIELD_VALUES(activeRecords, 'demo.TestModel1', 'name'), ',')".equals(formatObject)) {
//            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(FunctionLanguageEnum.GROOVY.value().toLowerCase());
//            Bindings bindings = scriptEngine.createBindings();
//            for (Map.Entry<String, Object> entry : context.entrySet()) {
//                String pk = entry.getKey();
//                Object pv = entry.getValue();
//                if (pv != null) {
//                    bindings.put(pk, pv);
//                }
//            }
//            List<FunctionDefinition> expressionFunctions = Models.origin().queryListByWrapper(Pops.<FunctionDefinition>lambdaQuery()
//                    .from(FunctionDefinition.MODEL_MODEL)
//                    .eq(FunctionDefinition::getNamespace, NamespaceConstants.expression));
//            Map<String, Object> expressionFunctionObjectMap = new HashMap<>();
//            for (FunctionDefinition expressionFunction : expressionFunctions) {
//                Object expressionFunctionObject = expressionFunctionObjectMap.computeIfAbsent(expressionFunction.getClazz(), k -> {
//                    try {
//                        return Class.forName(k).newInstance();
//                    } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
//                        log.error("expression function object new instance error.", e);
//                    }
//                    return null;
//                });
//                if (expressionFunctionObject == null) {
//                    continue;
//                }
//                Method[] methods = expressionFunctionObject.getClass().getMethods();
//                Method bindingMethod = null;
//                for (Method method : methods) {
//                    if (method.getName().equals(expressionFunction.getMethod()) && method.getDeclaredAnnotation(pro.shushi.pamirs.meta.annotation.Function.class) != null) {
//                        bindingMethod = method;
//                        break;
//                    }
//                }
//                if (bindingMethod == null) {
//                    continue;
//                }
//                bindings.put(expressionFunction.getFun(), bindingMethod);
//            }
//            ScriptContext scriptContext = scriptEngine.getContext();
//            SimpleScriptContext tempctxt = new SimpleScriptContext();
//            tempctxt.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
//            tempctxt.setBindings(scriptContext.getBindings(ScriptContext.GLOBAL_SCOPE), ScriptContext.GLOBAL_SCOPE);
//
//            String expression = (String) formatObject;
////                    String expression = Exp.pre((String) formatObject);
//            CompiledScript expressionCompiledScript = ((Compilable) scriptEngine).compile(expression);
//
//            long start = System.currentTimeMillis();
//            for (int i = 0; i < 100000; i++) {
////                        MVEL.eval(expression, context);
//                expressionCompiledScript.eval(tempctxt);
////                        Exp.run(expression, ScriptType.EL.getType(), context);
//            }
//            log.error("expression test: {}ms", System.currentTimeMillis() - start);
//        }
    }
}
