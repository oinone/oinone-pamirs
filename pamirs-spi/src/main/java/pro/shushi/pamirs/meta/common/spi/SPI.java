package pro.shushi.pamirs.meta.common.spi;

import pro.shushi.pamirs.meta.common.constants.SpiNamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.factory.AnnotationServiceLoaderFactory;
import pro.shushi.pamirs.meta.common.spi.factory.ServiceLoaderFactory;

import java.lang.annotation.*;

/**
 * SPI注解
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 2:30 上午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    /**
     * default extension service name
     */
    String value() default SpiNamespaceConstants.PAMIRS;

    /**
     * default extension loader factory class
     */
    Class<? extends ServiceLoaderFactory> factory() default AnnotationServiceLoaderFactory.class;

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Service {

        /**
         * extension service name
         */
        String value() default SpiNamespaceConstants.PAMIRS;

    }

}