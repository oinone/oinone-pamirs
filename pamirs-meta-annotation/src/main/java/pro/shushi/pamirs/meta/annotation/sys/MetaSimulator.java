package pro.shushi.pamirs.meta.annotation.sys;

import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 元数据模拟环境标识
 *
 * @author deng d@shushi.pro
 * date 2019.04.10
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaSimulator {

    String module() default ModuleConstants.MODULE_BASE;

    String moduleAbbr() default "";

    boolean onlyBasicTypeField() default true;

    boolean preCreateTable() default true;

    String SIMULATE_PREFIX = "simulate.";

}
