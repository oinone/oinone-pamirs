package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import pro.shushi.pamirs.framework.connectors.data.api.plugin.SqlPlugin;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL 插件辅助类
 */
public class SqlPluginHelper {

    /**
     * SQL 插件缓存
     * key 可能是 mappedStatement 的 ID,也可能是 class 的 name
     */
    private static final Map<String, Boolean> SQL_PLUGIN_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 初始化缓存 接口上 SqlParser 注解信息
     *
     * @param mapperClass Mapper Class
     */
    public synchronized static void initSqlPluginInfoCache(Class<?> mapperClass) {
        SqlPlugin sqlPlugin = mapperClass.getAnnotation(SqlPlugin.class);
        if (sqlPlugin != null) {
            SQL_PLUGIN_INFO_CACHE.put(mapperClass.getName(), sqlPlugin.filter());
        }
    }

    /**
     * 初始化缓存 方法上 SqlParser 注解信息
     *
     * @param mapperClassName Mapper Class Name
     * @param method          Method
     */
    public static void initSqlPluginInfoCache(String mapperClassName, Method method) {
        SqlPlugin sqlPlugin = method.getAnnotation(SqlPlugin.class);
        if (sqlPlugin != null) {
            if (SQL_PLUGIN_INFO_CACHE.containsKey(mapperClassName)) {
                // mapper 接口上有注解
                Boolean value = SQL_PLUGIN_INFO_CACHE.get(mapperClassName);
                if (!value.equals(sqlPlugin.filter())) {
                    // 取反,不属于重复注解,放入缓存
                    String sid = mapperClassName + StringPool.DOT + method.getName();
                    SQL_PLUGIN_INFO_CACHE.putIfAbsent(sid, sqlPlugin.filter());
                }
            } else {
                String sid = mapperClassName + StringPool.DOT + method.getName();
                SQL_PLUGIN_INFO_CACHE.putIfAbsent(sid, sqlPlugin.filter());
            }
        }
    }

    /**
     * 获取 SqlParser 注解信息
     *
     * @param metaObject 元数据对象
     */
    public static boolean getSqlPluginInfo(MetaObject metaObject) {
        String id = getMappedStatement(metaObject).getId();
        Boolean value = SQL_PLUGIN_INFO_CACHE.get(id);
        if (value != null) {
            return value;
        }
        String mapperName = id.substring(0, id.lastIndexOf(StringPool.DOT));
        return SQL_PLUGIN_INFO_CACHE.getOrDefault(mapperName, false);
    }

    /**
     * 获取当前执行 MappedStatement
     *
     * @param metaObject 元对象
     */
    public static MappedStatement getMappedStatement(MetaObject metaObject) {
        return (MappedStatement) metaObject.getValue("delegate.mappedStatement");
    }
}
