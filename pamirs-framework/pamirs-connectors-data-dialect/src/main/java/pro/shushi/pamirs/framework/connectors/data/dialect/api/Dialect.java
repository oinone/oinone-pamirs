package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方言注解
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Dialect {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface component {

        DataSourceEnum type() default DataSourceEnum.MYSQL;

        String version() default DataProductVersion.DEFAULT_MYSQL_VERSION;

        String majorVersion() default DataProductVersion.DEFAULT_MYSQL_MAJOR_VERSION;

    }

    /**
     * 忽略方言SQL解析
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface ignored {
    }

}