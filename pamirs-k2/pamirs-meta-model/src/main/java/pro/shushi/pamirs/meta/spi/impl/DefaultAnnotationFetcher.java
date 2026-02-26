package pro.shushi.pamirs.meta.spi.impl;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.spi.AnnotationFetcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * 默认注解获取API
 *
 * @author Adamancy Zhang at 13:44 on 2026-02-25
 */
@Order
@Component
@SPI.Service
public class DefaultAnnotationFetcher implements AnnotationFetcher {

    @Override
    public <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return AnnotationUtils.getAnnotation(annotatedElement, annotationType);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        return AnnotationUtils.getAnnotation(method, annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(annotatedElement, annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(method, annotationType);
    }
}
