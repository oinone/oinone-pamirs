package pro.shushi.pamirs.meta.spi;

import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * 注解获取API
 *
 * @author Adamancy Zhang at 13:40 on 2026-02-25
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AnnotationFetcher {

    <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType);

    <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType);

    <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType);

    <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType);

    HoldKeeper<AnnotationFetcher> holder = new HoldKeeper<>();

    static AnnotationFetcher get() {
        return holder.supply(() -> Spider.getDefaultExtension(AnnotationFetcher.class));
    }

}
