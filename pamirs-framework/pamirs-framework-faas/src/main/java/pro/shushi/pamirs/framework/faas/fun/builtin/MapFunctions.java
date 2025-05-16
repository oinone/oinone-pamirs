package pro.shushi.pamirs.framework.faas.fun.builtin;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;

import java.util.Map;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.MAP;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;

/**
 * 键值对函数
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Fun(NamespaceConstants.expression)
public class MapFunctions {

    @Function.Advanced(
            displayName = "从键值对中获取指定键的值", language = JAVA,
            builtin = true, category = MAP
    )
    @Function.fun("MAP_GET")
    @Function(name = "MAP_GET", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MAP_GET(map,key)\n函数说明: 从键值对中获取键为key的值"
    )
    public static Object mapGet(Map map, String key) {
        if (null == map) {
            return null;
        }
        return map.get(key);
    }

    @Function.Advanced(
            displayName = "判断键值对是否为空", language = JAVA,
            builtin = true, category = MAP
    )
    @Function.fun("MAP_IS_EMPTY")
    @Function(name = "MAP_IS_EMPTY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MAP_IS_EMPTY(map)\n函数说明: 判断键值对map是否为空"
    )
    public static Boolean mapIsEmpty(Map map) {
        if (null == map) {
            return true;
        } else {
            return map.isEmpty();
        }
    }

    @Function.Advanced(
            displayName = "向键值对中添加键值", language = JAVA,
            builtin = true, category = MAP
    )
    @Function.fun("MAP_PUT")
    @Function(name = "MAP_PUT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MAP_PUT(map,key,value)\n函数说明: 将键为key的值为value添加到键值对map中"
    )
    public static Map mapPut(Map map, String key, Object value) {
        if (null == map) {
            return map;
        }
        map.put(key, value);
        return map;
    }

    @Function.Advanced(
            displayName = "移除键值对中的元素", language = JAVA,
            builtin = true, category = MAP
    )
    @Function.fun("MAP_REMOVE")
    @Function(name = "MAP_REMOVE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MAP_REMOVE(map,key)\n函数说明: 从键值对map中移除键key"
    )
    public static Object mapRemove(Map map, String key) {
        if (null == map) {
            return null;
        }
        return map.remove(key);
    }

    @Function.Advanced(
            displayName = "判断键值对中是否包含键", language = JAVA,
            builtin = true, category = MAP
    )
    @Function.fun("MAP_CONTAINS_KEY")
    @Function(name = "MAP_CONTAINS_KEY", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MAP_CONTAINS_KEY(map,key)\n函数说明: 判断键值对map中是否包含键key"
    )
    public static Boolean mapContainsKey(Map map, String key) {
        if (null == map) {
            return false;
        } else {
            return map.containsKey(key);
        }
    }

    @Function.Advanced(
            displayName = "获取键值数量", language = JAVA,
            builtin = true, category = MAP
    )
    @Function.fun("MAP_COUNT")
    @Function(name = "MAP_COUNT", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: MAP_COUNT(map)\n函数说明: 获取键值对map的键值数量"
    )
    public static Integer mapCount(Map map) {
        if (null == map) {
            return 0;
        } else {
            return map.size();
        }
    }

}
