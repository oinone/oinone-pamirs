package pro.shushi.pamirs.meta.api;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.CheckProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.enmu.EnumProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.SequenceGenerator;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.VersionGenerator;
import pro.shushi.pamirs.meta.common.constants.BeanNameConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.PStringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用接口Common api工厂
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/23 5:49 下午
 */
@Configuration
public class CommonApiFactory {

    private static final Map<ApiCacheKey, Object> CACHE = new ConcurrentHashMap<>();

    private static final HoldKeeper<CommonApiConfigure> COMMON_API_CONFIGURE_HOLDER = new HoldKeeper<>();

    private static CommonApiConfigure getCommonApiConfigure() {
        return COMMON_API_CONFIGURE_HOLDER.supply(() -> BeanDefinitionUtils.getBean(BeanNameConstants.COMMON_API_CONFIGURE, CommonApiConfigure.class));
    }

    public static <API> API getApi(Class<API> interfaceClass, API defaultApi) {
        return Optional.ofNullable(getApi(interfaceClass)).orElse(defaultApi);
    }

    @SuppressWarnings("unchecked")
    public static <API> API getApi(Class<API> interfaceClass) {
        if (null != interfaceClass) {
            ApiCacheKey key = new ApiCacheKey(interfaceClass);
            Object beanObject = CACHE.get(key);
            if (beanObject == null) {
                CommonApiConfigure configure = getCommonApiConfigure();
                String beanName;
                if (null == configure) {
                    beanName = null;
                } else {
                    beanName = configure.getApiMap().get(interfaceClass.getName());
                }
                if (StringUtils.isBlank(beanName)) {
                    beanName = BeanNameConstants.DEFAULT + interfaceClass.getSimpleName();
                }
                if (!BeanDefinitionUtils.containsBean(beanName)) {
                    List<API> beans = BeanDefinitionUtils.getBeansOfTypeByOrdered(interfaceClass);
                    if (CollectionUtils.isNotEmpty(beans)) {
                        beanObject = beans.get(0);
                    }
                } else {
                    beanObject = BeanDefinitionUtils.getBean(beanName);
                }
                if (beanObject != null) {
                    CACHE.put(key, beanObject);
                }
            }
            return (API) beanObject;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <API> API getApi(Class<API> interfaceClass, String implClassName) {
        if (StringUtils.isNotBlank(implClassName)) {
            ApiCacheKey key = new ApiCacheKey(interfaceClass, implClassName);
            Object beanObject = CACHE.get(key);
            if (beanObject == null) {
                beanObject = BeanDefinitionUtils.getBean(PStringUtils.camelCaseFromModel(implClassName));
                if (beanObject != null) {
                    CACHE.put(key, beanObject);
                }
            }
            return (API) beanObject;
        }
        return null;
    }

    // 类型系统
    public static TypeProcessor getTypeProcessor() {
        return getApi(TypeProcessor.class);
    }

    // 约束校验系统
    @SuppressWarnings("unused")
    public static CheckProcessor getCheckProcessor() {
        return getApi(CheckProcessor.class);
    }

    // 枚举系统
    @SuppressWarnings("unused")
    public static <T> EnumProcessor<T> getEnumProcessor() {
        //noinspection unchecked
        return getApi(EnumProcessor.class);
    }

    // 序列 生成器
    public static <T> SequenceGenerator<T> getSequenceGenerator() {
        //noinspection unchecked
        return getApi(SequenceGenerator.class);
    }

    // 版本生成器
    public static VersionGenerator getVersionGenerator() {
        return getApi(VersionGenerator.class);
    }

    private static class ApiCacheKey implements Serializable {

        private static final long serialVersionUID = -675346929218238331L;

        private final Class<?> interfaceClass;

        private final String implClassName;

        public ApiCacheKey(Class<?> interfaceClass, String implClassName) {
            this.interfaceClass = interfaceClass;
            this.implClassName = implClassName;
        }

        public ApiCacheKey(Class<?> interfaceClass) {
            this(interfaceClass, null);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ApiCacheKey)) {
                return false;
            }
            ApiCacheKey that = (ApiCacheKey) o;
            return interfaceClass.equals(that.interfaceClass) &&
                    Objects.equals(implClassName, that.implClassName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(interfaceClass, implClassName);
        }
    }
}
