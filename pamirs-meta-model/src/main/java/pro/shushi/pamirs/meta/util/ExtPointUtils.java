package pro.shushi.pamirs.meta.util;

import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.lang.reflect.Method;

/**
 * 扩展点工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
@Slf4j
public class ExtPointUtils {

    public static Fun fetchInterfaceFun(Method source) {
        return AnnotationUtils.findAnnotation(source, Fun.class);
    }

}
