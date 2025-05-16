package pro.shushi.pamirs.meta.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.Empty;

import java.lang.reflect.Method;


/**
 * 扩展点命名空间和编码获取工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/16 2:38 下午
 */
@SuppressWarnings({"unused"})
@Slf4j
public class ExtNamespaceAndNameUtils {

    public static String namespace(Class<?> source) {
        String namespace = NamespaceAndFunUtils.findSelfAndParentAnnotationValue(Lists.newArrayList(source), ExtNamespaceAndNameUtils::findClassNamespace);
        if (StringUtils.isNotBlank(namespace)) {
            return namespace;
        }
        namespace = NamespaceAndFunUtils.namespace(source);
        if (StringUtils.isNotBlank(namespace)) {
            return namespace;
        }
        return source.getName();
    }

    private static String findClassNamespace(Class<?> source) {
        Ext extAnnotation = AnnotationUtils.findAnnotation(source, Ext.class);
        if (null != extAnnotation) {
            Class<?> clazz = extAnnotation.value();
            if (!Empty.class.isAssignableFrom(clazz)) {
                return NamespaceAndFunUtils.namespace(clazz);
            }
        }
        return null;
    }

    public static String namespace(Method source) {
        return namespace(source.getDeclaringClass());
    }

    public static String name(Method source) {
        ExtPoint.name extPointAnnotation = AnnotationUtils.findAnnotation(source, ExtPoint.name.class);
        if (null != extPointAnnotation) {
            String name = extPointAnnotation.value();
            if (StringUtils.isNotBlank(name)) {
                return name;
            }
        }
        return source.getName();
    }

}
