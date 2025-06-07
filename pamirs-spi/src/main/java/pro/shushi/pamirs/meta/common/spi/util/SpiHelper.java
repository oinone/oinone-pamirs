package pro.shushi.pamirs.meta.common.spi.util;

import org.springframework.core.annotation.PamirsAnnotationUtils;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * SPI帮助类
 * <p>
 * 2021/9/11 1:10 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SpiHelper {

    public static String fetchExtensionName(Class<?> spiClass) {
        SPI.Service pamirsSpi = PamirsAnnotationUtils.getAnnotation(spiClass, SPI.Service.class);
        if (null == pamirsSpi) {
            return null;
        }
        return pamirsSpi.value();
    }

}
