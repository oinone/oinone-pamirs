package pro.shushi.pamirs.meta.api.core.orm.systems.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelModelApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Optional;

/**
 * 模型类接口实现
 * <p>
 * 2020/6/30 2:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI.Service
public class DefaultModelModelApi implements ModelModelApi {

    public static final Cache<Class<?>, String> cache = Caffeine.newBuilder().maximumSize(10_000).build();

    public static String sign(Class<?> source) {
        return cache.get(source, _clazz -> {
            Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(_clazz, Model.model.class);
            String sign = Optional.ofNullable(modelModelAnnotation).map(Model.model::value).filter(StringUtils::isNotBlank).orElse(null);
            if (StringUtils.isBlank(sign)) {
                Fun funAnnotation = AnnotationUtils.getAnnotation(_clazz, Fun.class);
                return Optional.ofNullable(funAnnotation).map(Fun::value).filter(StringUtils::isNotBlank).orElse(_clazz.getName());
            }
            return sign;
        });
    }

}
