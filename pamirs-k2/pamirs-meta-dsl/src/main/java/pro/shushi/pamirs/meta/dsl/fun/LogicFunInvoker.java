package pro.shushi.pamirs.meta.dsl.fun;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.Ext;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.dsl.constants.DSLDefineConstants;
import pro.shushi.pamirs.meta.dsl.model.TxConfig;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class LogicFunInvoker {

    @Deprecated
    public final static String function = "pro.shushi.pamirs.base.model.meta.Functions";
    @Deprecated
    public final static String objectMapUtilsClazz = "pro.shushi.pamirs.base.util.ObjectMapUtils";
    @Deprecated
    public final static String translateUtilsClazz = "pro.shushi.pamirs.base.util.TranslateUtils";

    public final static String rsqlParseHelperClazz = "pro.shushi.pamirs.framework.gateways.rsql.RsqlParseHelper";

    public static final String pamirsDataUtilsClazz = "pro.shushi.pamirs.framework.orm.json.PamirsDataUtils";
    public static final String dataFeatureClazz = "pro.shushi.pamirs.framework.orm.json.emnu.DataFeature";
    public static final String dataFeatureClazzArray = "[L" + dataFeatureClazz + ";";

    public static Object exp(String expression, Map<String, Object> context) {
        return Exp.run(expression, context);
    }

    public static Object exe(String namespace, String name, Object... param) {
        return Fun.run(namespace, name, param);
    }

    public static Object exeWithTx(String namespace, String name, TxConfig txConfig, Object... param) {
        // FIXME: 2021/10/25 事务? Fun.run看上去默认带事务
        return Fun.run(namespace, name, param);
    }

    public static Object extPoint(String namespace, String name, Object... param) {
        return Ext.run(namespace, name, param);
    }

    public static Map<?, ?> lowcodeMapToModelMap(Object context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {

        Class<?> dfArrayClass = Class.forName(dataFeatureClazzArray);
        Class<?> pduClass = Class.forName(pamirsDataUtilsClazz);
        Class<?> dfClass = Class.forName(dataFeatureClazz);
        Method toStrMethod = pduClass.getDeclaredMethod("toJSONString", String.class, Object.class, dfArrayClass);
        toStrMethod.setAccessible(true);
        String json = (String) toStrMethod.invoke(pduClass.newInstance(), modelModel, context, Array.newInstance(dfClass, 0));
        Method parseMethod = pduClass.getDeclaredMethod("parseModelMap", String.class, String.class, dfArrayClass);
        parseMethod.setAccessible(true);
        return (Map<String, Object>) parseMethod.invoke(pduClass.newInstance(), modelModel, json, Array.newInstance(dfClass, 0));
    }

    public static Object lowcodeMapToModel(Object context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> dfArrayClass = Class.forName(dataFeatureClazzArray);
        Class<?> pduClass = Class.forName(pamirsDataUtilsClazz);
        Class<?> dfClass = Class.forName(dataFeatureClazz);
        Method toStrMethod = pduClass.getDeclaredMethod("toJSONString", String.class, Object.class, dfArrayClass);
        toStrMethod.setAccessible(true);
        String json = (String) toStrMethod.invoke(pduClass.newInstance(), modelModel, context, Array.newInstance(dfClass, 0));
        Method parseMethod = pduClass.getDeclaredMethod("parseModelObject", String.class, String.class, dfArrayClass);
        parseMethod.setAccessible(true);
        return parseMethod.invoke(pduClass.newInstance(), modelModel, json, Array.newInstance(dfClass, 0));
    }

    public static Map lowcodeModelToMap(Object context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        return JsonUtils.parseObject2Map(context);
//        Method method = Class.forName(objectMapUtilsClazz)
//                .getMethod("lowcodeModelToMap", Object.class, String.class);
//        return method.invoke(Class.forName(objectMapUtilsClazz).newInstance(), context, modelModel);
    }

    public static List<Map<String, Object>> lowcodeModelToMapList(List<Object> context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        return JsonUtils.parseObjectList2MapList(context);
//        Method method = Class.forName(objectMapUtilsClazz)
//                .getMethod("lowcodeModelToMapList", List.class, String.class);
//        return method.invoke(Class.forName(objectMapUtilsClazz).newInstance(), context, modelModel);
    }

    public static List<Map> lowcodePageToMapList(Pagination context, String modelModel) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        return lowcodeModelToMapList(context.getContent(), modelModel);
//        Method method = Class.forName(objectMapUtilsClazz)
//                .getMethod("lowcodePageToMapList", Object.class, String.class);
//        return method.invoke(Class.forName(objectMapUtilsClazz).newInstance(), context, modelModel);
    }

    public static String jsonToString(Object context) {
        return JsonUtils.toJSONString(context);
    }

    public static Object jsonParseObject(String context, Class tClass) {
        return JsonUtils.parseObject(context, tClass);
    }

    // FIXME: 2021/10/25 不清楚含义,调用方用返回结果覆盖了rsql
    @Deprecated
    public static Object rsql(String rsql, Map<String, Object> context) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = Class.forName(translateUtilsClazz)
                .getMethod("handleFunctions", String.class, Map.class, Boolean.class);
        return method.invoke(Class.forName(translateUtilsClazz).newInstance(), rsql, context, Boolean.TRUE);
    }

    public static String parseRsql2Sql(String rsql, String model, Map<String, Object> context) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        //执行下表达式, rsql里可以含上下文的变量
        rsql = (String) exp(rsql, context);
        Method method = Class.forName(rsqlParseHelperClazz)
                .getMethod("parseRsql2Sql", String.class, String.class);
        return (String) method.invoke(null, model, rsql);
    }

    /**
     * fixme 2021年10月25日10:41:51 不清楚含义
     * 历史调用:pro/shushi/pamirs/meta/dsl/signal/Act.java:67
     */
    @Deprecated
    public static Boolean isDslFun(String modelModel, String funName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return "DSL".equalsIgnoreCase(String.valueOf(((Map<String, Object>) exe("PAMIRS", "ONE", function, modelModel + "-" + funName)).get("type")));
    }

    /**
     * 获取参数,支持下表达式? 可以实现list转对象(如:列表查询后取第一个). 直接get某个节点对象写法是一致的. 先把调用收口再说
     *
     * @param arg
     * @param context
     * @return
     */
    public static Object getArg(String arg, Map<String, Object> context) {
        if (StringUtils.isEmpty(arg)) {
            return null;
        }
        return exp(arg, context);
    }

    public static void putResult(Map<String, Object> context, Object result) {
        context.put((String) context.get(DSLDefineConstants.CURRENT_STATE_NAME), result);
    }

    public static void replaceContext(Map<String, Object> context, String key, Object result) {
        context.put(key, result);
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

    public static void foreachBreak(Map<String, Object> context, String foreachId) {
        String foreachIndexKey = foreachId + DSLDefineConstants.CURRENT_ITERATOR_INDEX;
        // TODO: 2021/10/26 先把下标移动到最大值吧
        context.put(foreachIndexKey, Integer.MAX_VALUE - 1);
    }

    //不区分action
    @Deprecated
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
