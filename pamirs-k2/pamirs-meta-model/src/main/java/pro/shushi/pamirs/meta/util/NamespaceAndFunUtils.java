package pro.shushi.pamirs.meta.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.spi.NamespaceAndFunFetcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 函数命名空间和编码获取工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/16 2:38 下午
 */
@SuppressWarnings({"unused"})
@Slf4j
public class NamespaceAndFunUtils {

    private static final HoldKeeper<NamespaceAndFunFetcher> HOLDER = new HoldKeeper<>();

    private static NamespaceAndFunFetcher getApi() {
        return HOLDER.supply(() -> Spider.getDefaultExtension(NamespaceAndFunFetcher.class));
    }

    public static final Cache<Method, String> namespaceCache = Caffeine.newBuilder().maximumSize(10_000).build();

    public static final Cache<Method, String> funCache = Caffeine.newBuilder().maximumSize(10_000).build();

    public static String namespace(Method source) {
        return namespaceCache.get(source, v -> namespace(v.getDeclaringClass()));
    }

    public static String namespace(Class<?> source) {
        String namespace = findSelfAndParentAnnotationValue(Lists.newArrayList(source), NamespaceAndFunUtils::findClassNamespace);
        if (StringUtils.isNotBlank(namespace)) {
            return namespace;
        }
        return source.getName();
    }

    public static String findSelfAndParentAnnotationValue(List<Class<?>> interfaces, AnnotationHandler handler) {
        String namespace;
        if (CollectionUtils.isEmpty(interfaces)) {
            return null;
        }
        for (Class<?> interfaceClass : interfaces) {
            if (null == interfaceClass) {
                return null;
            }
            namespace = handler.handle(interfaceClass);
            if (StringUtils.isNotBlank(namespace)) {
                return namespace;
            }
            Class<?>[] superInterfaces = interfaceClass.getInterfaces();
            List<Class<?>> superInterfaceList = new ArrayList<>(Lists.newArrayList(interfaceClass.getSuperclass()));
            superInterfaceList.addAll(ListUtils.toList(superInterfaces));
            namespace = findSelfAndParentAnnotationValue(superInterfaceList, handler);
            if (StringUtils.isNotBlank(namespace)) {
                return namespace;
            }
        }
        return null;
    }

    public static String findClassNamespace(Class<?> clazz) {
        String namespace = null;
        Model.model modelModelAnnotation = AnnotationUtils.findAnnotation(clazz, Model.model.class);
        if (null != modelModelAnnotation) {
            namespace = modelModelAnnotation.value();
            if (StringUtils.isNotBlank(namespace)) {
                return namespace;
            }
        }
        Fun funAnnotation = AnnotationUtils.findAnnotation(clazz, Fun.class);
        if (null != funAnnotation) {
            namespace = funAnnotation.value();
            if (StringUtils.isNotBlank(namespace)) {
                return namespace;
            }
        }
        return namespace;
    }

    public static String fun(Method source) {
        return funCache.get(source, v -> {
            pro.shushi.pamirs.meta.annotation.Function.fun funAnnotation
                    = AnnotationUtils.findAnnotation(source, pro.shushi.pamirs.meta.annotation.Function.fun.class);
            if (null != funAnnotation) {
                String fun = funAnnotation.value();
                if (StringUtils.isNotBlank(fun)) {
                    return fun;
                }
            }
            return source.getName();
        });
    }

    public static void fillBeanName(Method method, FunctionDefinition functionDefinition) {
        functionDefinition.setBeanName(getApi().getBeanName(method));
    }

    public interface AnnotationHandler {

        String handle(Class<?> clazz);

    }
}
