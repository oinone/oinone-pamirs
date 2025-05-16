package pro.shushi.pamirs.framework.configure.inject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 元数据注入字段标识
 *
 * @author deng d@shushi.pro
 * date 2019.04.10
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaField {

    // json数据来源字段名称
    String from();

    // 注入数据字段名称
    String to();

    Class<?> toClass();

    boolean isList();

}
