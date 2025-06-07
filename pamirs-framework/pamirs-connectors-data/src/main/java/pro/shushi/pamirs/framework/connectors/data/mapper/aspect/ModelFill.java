package pro.shushi.pamirs.framework.connectors.data.mapper.aspect;

import java.lang.annotation.*;

/**
 * Mapper获取模型编码切面注解
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface ModelFill {

}