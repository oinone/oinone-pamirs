package pro.shushi.pamirs.meta.util;

import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;


/**
 * 系统来源工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/16 2:38 下午
 */
@Slf4j
public class SystemSourceUtils {

    public static SystemSourceEnum fetch(Class<?> source) {
        Base baseAnnotation = AnnotationUtils.getAnnotation(source, Base.class);
        return Optional.ofNullable(baseAnnotation).map(Base::value).orElse(null);
    }

    public static SystemSourceEnum fetch(Field source) {
        Base baseAnnotation = AnnotationUtils.findAnnotation(source, Base.class);
        if (null == baseAnnotation) {
            baseAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Base.class);
        }
        return Optional.ofNullable(baseAnnotation).map(Base::value).orElse(null);
    }

    public static SystemSourceEnum fetch(Method source) {
        Base baseAnnotation = AnnotationUtils.findAnnotation(source, Base.class);
        if (null == baseAnnotation) {
            baseAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Base.class);
        }
        return Optional.ofNullable(baseAnnotation).map(Base::value).orElse(null);
    }

}
