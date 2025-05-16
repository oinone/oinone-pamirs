package pro.shushi.pamirs.framework.configure.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 继承工具类
 * <p>
 * 2022/10/14 10:44 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class InheritedUtil {

    public static ModelTypeEnum resolveModelType(Class<?> modelClazz, Model.Advanced selfModelAdvancedAnnotation) {
        ModelTypeEnum modelType = Optional.ofNullable(selfModelAdvancedAnnotation).map(Model.Advanced::type).orElse(ModelTypeEnum.STORE);
        if (TransientModel.class.isAssignableFrom(modelClazz) && !TransientModel.class.equals(modelClazz)) {
            modelType = ModelTypeEnum.TRANSIENT;
        }
        return modelType;
    }

    public static boolean isMultiTableInherited(Class<?> superClazz) {
        return null != AnnotationUtils.getAnnotation(superClazz, Model.MultiTable.class);
    }

    public static boolean isChangeTableInherited(Class<?> clazz) {
        return null != AnnotationUtils.getAnnotation(clazz, Model.ChangeTableInherited.class);
    }

    public static boolean isAbstractTableInherited(Class<?> clazz, Class<?> superClazz) {
        Model.Advanced superModelAdvancedAnnotation = AnnotationUtils.getAnnotation(superClazz, Model.Advanced.class);
        ModelTypeEnum modelType = resolveModelType(clazz, superModelAdvancedAnnotation);
        return ModelTypeEnum.ABSTRACT.equals(modelType);
    }

    public static boolean isTransientInherited(Class<?> clazz) {
        return TransientModel.class.isAssignableFrom(clazz) && !TransientModel.class.equals(clazz);
    }

    public static <T> T fetchModelConfigItemByClass(Class<?> clazz,
                                                    Function<Class<?>, T> configFetcher) {
        return fetchModelConfigObjByClass(clazz, configFetcher, (v, o) -> null == o);
    }

    public static <T> T fetchModelConfigStringByClass(Class<?> clazz,
                                                      Function<Class<?>, T> configFetcher) {
        return fetchModelConfigObjByClass(clazz, configFetcher, (v, o) -> StringUtils.isBlank((String) o));
    }

    @SuppressWarnings("unchecked")
    public static <T> T fetchModelConfigCollectionByClass(Class<?> clazz,
                                                          Function<Class<?>, T> configFetcher) {
        return fetchModelConfigObjByClass(clazz, configFetcher, (v, o) -> CollectionUtils.isEmpty((List<Object>) o));
    }

    public static <T> T fetchModelConfigArrayByClass(Class<?> clazz,
                                                     Function<Class<?>, T> configFetcher) {
        return fetchModelConfigObjByClass(clazz, configFetcher, (v, o) -> ArrayUtils.isEmpty((Object[]) o));
    }

    public static <T> T fetchModelConfigObjByClass(Class<?> clazz,
                                                   Function<Class<?>, T> configFetcher,
                                                   BiFunction<Class<?>, T, Boolean> continueAfterFetch) {
        return fetchModelConfigObjByClass(clazz, configFetcher, null, continueAfterFetch);
    }

    public static <T> T fetchModelConfigObjByClass(Class<?> clazz,
                                                   Function<Class<?>, T> configFetcher,
                                                   Function<Class<?>, Boolean> continueBeforeFetch,
                                                   BiFunction<Class<?>, T, Boolean> continueAfterFetch) {
        if (null == clazz) {
            return null;
        }
        T configItem = null;
        boolean continueBeforeFetchValid = null != continueBeforeFetch;
        boolean continueAfterFetchValid = null != continueAfterFetch;
        Class<?> tempClass = clazz;
        do {
            if (continueBeforeFetchValid && !continueBeforeFetch.apply(tempClass)) {
                return configItem;
            }
            configItem = configFetcher.apply(tempClass);
            if (continueAfterFetchValid && !continueAfterFetch.apply(tempClass, configItem)) {
                return configItem;
            }
            tempClass = tempClass.getSuperclass();
        } while (tempClass != null);
        return configItem;
    }

    public static <T> T fetchUnionModelConfigByClass(Class<?> clazz,
                                                     Function<Class<?>, T> configFetcher,
                                                     Function<Class<?>, Boolean> isTransientInherited,
                                                     BiFunction<Class<?>, Class<?>, Boolean> isNotSameTableInherited,
                                                     BiFunction<Class<?>, T, Boolean> validCondition) {
        /*
            如果是传输 -> 返回self
            如果是换表、多表、抽象继承，
                self不空 -> 返回self
                self为空，super不为空 -> 返回super
            其他 -> 取super继续遍历
        */
        T configItem = null;
        T superConfigItem = null;
        boolean superFetched = false;
        Class<?> tempClass = clazz;
        Class<?> superClass;
        while (tempClass != null) {
            configItem = superFetched ? superConfigItem : configFetcher.apply(tempClass);
            if (isTransientInherited.apply(tempClass)) {
                return configItem;
            }
            superClass = tempClass.getSuperclass();
            if (null == superClass) {
                break;
            }
            if (isNotSameTableInherited.apply(tempClass, superClass)) {
                if (validCondition.apply(tempClass, configItem)) {
                    return configItem;
                }
                superConfigItem = configFetcher.apply(superClass);
                if (validCondition.apply(superClass, superConfigItem)) {
                    return superConfigItem;
                }
                superFetched = true;
            } else {
                superFetched = false;
            }
            tempClass = superClass;
        }
        return configItem;
    }

    public static <T> Collection<T> fetchUnionCollectionModelConfigByClass(Class<?> clazz,
                                                                           Function<Class<?>, Collection<T>> configFetcher,
                                                                           Function<Class<?>, Boolean> continueBeforeFetch,
                                                                           BiFunction<Class<?>, Collection<T>, Boolean> continueAfterFetch) {
        if (null == clazz) {
            return null;
        }
        Collection<T> configItemList = null;
        boolean continueBeforeFetchValid = null != continueBeforeFetch;
        boolean continueAfterFetchValid = null != continueAfterFetch;
        Class<?> tempClass = clazz;
        do {
            if (continueBeforeFetchValid && !continueBeforeFetch.apply(tempClass)) {
                return configItemList;
            }
            Collection<T> tempConfigItemList = configFetcher.apply(tempClass);
            if (continueAfterFetchValid && !continueAfterFetch.apply(tempClass, tempConfigItemList)) {
                return configItemList;
            }
            configItemList = ListUtils.uniqueUnion(configItemList, tempConfigItemList);
            tempClass = tempClass.getSuperclass();
        } while (tempClass != null);
        return configItemList;
    }

}
