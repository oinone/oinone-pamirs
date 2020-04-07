package pro.shushi.pamirs.meta.dsl.fun;

import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.ExpressionApi;
import pro.shushi.pamirs.meta.api.core.compute.ExtPointApi;
import pro.shushi.pamirs.meta.api.core.compute.FunApi;
import pro.shushi.pamirs.meta.dsl.constants.DSLDefineConstants;
import pro.shushi.pamirs.meta.dsl.model.TxConfig;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class LogicFunInvoker {

    public final static String function = "pro.shushi.pamirs.base.model.meta.Functions";

    public final static String objectMapUtilsClazz = "pro.shushi.pamirs.base.util.ObjectMapUtils";

    public final static String translateUtilsClazz = "pro.shushi.pamirs.base.util.TranslateUtils";

    public final static String jsonUtilsClazz = "pro.shushi.pamirs.base.util.JsonUtils";

    public static Object exp(String expression, Map<String, Object> context) {
        return MetaApiFactory.getApi(ExpressionApi.class).run(expression, context);
    }

    public static Object exe(String namespace, String name, Object... param) {
        Object result = MetaApiFactory.getApi(FunApi.class).run(namespace, name, param);
        return result;
    }

    public static Object exeWithTx(String namespace, String name, TxConfig txConfig, Object... param) {
        Object result = MetaApiFactory.getApi(FunApi.class).run(namespace, name, txConfig, param);
        return result;
    }

    public static Object extPoint(String name, Object... param) {
        Object result = MetaApiFactory.getApi(ExtPointApi.class).run(name, param);
        return result;
    }

    public static Object lowcodeMapToModelMap(Object context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = Class.forName(objectMapUtilsClazz)
                .getMethod("lowcodeMapToModelMap", Map.class, String.class);
        return method.invoke(Class.forName(objectMapUtilsClazz).newInstance(), context, modelModel);
    }

    public static Object lowcodeMapToModel(Object context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = Class.forName(objectMapUtilsClazz)
                .getMethod("lowcodeMapToModel", Map.class, String.class);
        return method.invoke(Class.forName(objectMapUtilsClazz).newInstance(), context, modelModel);
    }

    public static Object lowcodeModelToMap(Object context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = Class.forName(objectMapUtilsClazz)
                .getMethod("lowcodeModelToMap", Object.class, String.class);
        return method.invoke(Class.forName(objectMapUtilsClazz).newInstance(), context, modelModel);
    }

    public static Object lowcodeModelToMapList(List<Object> context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = Class.forName(objectMapUtilsClazz)
                .getMethod("lowcodeModelToMapList", List.class, String.class);
        return method.invoke(Class.forName(objectMapUtilsClazz).newInstance(), context, modelModel);
    }

    public static Object lowcodePageToMapList(Object context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = Class.forName(objectMapUtilsClazz)
                .getMethod("lowcodePageToMapList", Object.class, String.class);
        return method.invoke(Class.forName(objectMapUtilsClazz).newInstance(), context, modelModel);
    }

    public static String jsonToString(Object context) {
        return JsonUtils.toJSONString(context);
    }

    public static Object jsonParseObject(String context, Class tClass) {
        return JsonUtils.parseObject(context, tClass);
    }

    public static Object rsql(String rsql, Map<String, Object> context) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = Class.forName(translateUtilsClazz)
                .getMethod("handleFunctions", String.class, Map.class, Boolean.class);
        return method.invoke(Class.forName(translateUtilsClazz).newInstance(), rsql, context, Boolean.TRUE);
    }

    public static Boolean isDslFun(String modelModel, String funName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return "DSL".equalsIgnoreCase(String.valueOf(((Map<String, Object>) exe("PAMIRS", "ONE", function, modelModel + "-" + funName)).get("type")));
    }

    public static void putResult(Map<String, Object> context, Object result) {
        context.put((String) context.get(DSLDefineConstants.CURRENT_STATE_NAME), result);
    }

    public static void putReturn(Map<String, Object> context, Object result) {
        context.put(DSLDefineConstants.DSL_RESULT_NAME, result);
    }

    public static String fetchCurrentStateName(Map<String, Object> context) {
        return (String) context.get(DSLDefineConstants.CURRENT_STATE_NAME);
    }

    public static Integer fetchCurrentIndex(Map<String, Object> context) {
        return (Integer) context.get(fetchCurrentStateName(context) + DSLDefineConstants.CURRENT_ITERATOR_INDEX);
    }

    public static void putCurrentIndex(Map<String, Object> context, Integer currentIndex) {
        context.put(fetchCurrentStateName(context) + DSLDefineConstants.CURRENT_ITERATOR_INDEX, currentIndex);
    }

    public static boolean isCUD(String methodName) {
        switch (methodName) {
            case "CREATE":
            case "UPDATE":
            case "DELETE": {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}
