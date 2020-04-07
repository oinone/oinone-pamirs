package pro.shushi.pamirs.meta.util;

import com.google.common.primitives.Primitives;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import static pro.shushi.pamirs.meta.enumclass.ExpEnumerate.BASE_ENUM_CLASS_IS_NOT_EXISTS_ERROR;
import static pro.shushi.pamirs.meta.enumclass.ExpEnumerate.BASE_MODEL_CLASS_IS_NOT_EXISTS_ERROR;

/**
 * 扩展点工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
@Slf4j
public class ExtPointUtils {

    public static ExtPoint fetchInterfaceExtPoint(Method source){
        ExtPoint extPointAnnotation = null;
        if(null != source.getDeclaringClass().getInterfaces()){
            for(Class i : source.getDeclaringClass().getInterfaces()){
                for(Method method : i.getMethods()){
                    if(method.getName().equals(source.getName())){
                        extPointAnnotation = AnnotationUtils.getAnnotation(method, ExtPoint.class);
                        if(null != extPointAnnotation){
                            break;
                        }
                    }
                }
            }
        }
        return extPointAnnotation;
    }

    public static Fun fetchInterfaceFun(Method source){
        if(null != source.getDeclaringClass().getInterfaces()){
            for(Class i : source.getDeclaringClass().getInterfaces()){
                Fun funAnnotation = AnnotationUtils.getAnnotation(i, Fun.class);
                if(null != funAnnotation){
                    return funAnnotation;
                }
            }
        }
        return null;
    }

}
