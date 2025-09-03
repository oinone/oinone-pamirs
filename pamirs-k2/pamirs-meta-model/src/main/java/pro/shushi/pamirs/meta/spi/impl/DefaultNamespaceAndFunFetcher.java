package pro.shushi.pamirs.meta.spi.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.spi.NamespaceAndFunFetcher;

import java.beans.Introspector;
import java.lang.reflect.Method;

/**
 * 默认函数命名空间和函数编码获取API
 *
 * @author Adamancy Zhang at 14:16 on 2025-08-29
 */
@Order
@Component
@SPI.Service
public class DefaultNamespaceAndFunFetcher implements NamespaceAndFunFetcher {

    @Override
    public String getBeanName(Method method) {
        Component componentAnnotation = AnnotationUtils.getAnnotation(method.getDeclaringClass(), Component.class);
        Service serviceAnnotation = AnnotationUtils.getAnnotation(method.getDeclaringClass(), Service.class);
        String beanName = null;
        if (null != serviceAnnotation) {
            beanName = serviceAnnotation.value();
            if (StringUtils.isBlank(beanName)) {
                beanName = Introspector.decapitalize(ClassUtils.getShortName(method.getDeclaringClass().getSimpleName()));
            }
        } else if (null != componentAnnotation) {
            beanName = componentAnnotation.value();
            if (StringUtils.isBlank(beanName)) {
                beanName = Introspector.decapitalize(ClassUtils.getShortName(method.getDeclaringClass().getSimpleName()));
            }
        }
        return beanName;
    }
}
