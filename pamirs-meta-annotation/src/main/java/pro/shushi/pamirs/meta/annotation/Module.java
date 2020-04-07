package pro.shushi.pamirs.meta.annotation;

import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.enmu.SoftwareLicenseEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模块
 * @author deng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    // 显示名称
    String displayName();

    // 模块前端技术名称
    String name() default "";

    // repository上发布的最新版本
    String version();

    // 分类编码
    String category() default "";

    // 描述摘要
    String summary() default "";

    // 依赖模块名列表
    String[] dependencies() default PackageConstants.PACKAGE_BASE;

    // 互斥模块名列表
    String[] exclusions() default {};

    // 排序用的，默认100，order by priority,name
    long priority() default 100;

    // 更多配置
    Advanced advanced() default @Advanced;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Advanced {

        // 数据库名
        String database() default "";

        // 站点
        String website() default "";

        // module的作者
        String author() default "";

        // 描述
        String description() default "";

        // 是否应用
        boolean application() default true;

        // 是否是demo
        boolean demo() default false;

        // 是否是web
        boolean web() default true;

        // 是否需要跳转到website去购买
        boolean toBuy() default true;

        // 是否自建应用
        boolean selfBuilt() default false;

        // license
        SoftwareLicenseEnum license() default SoftwareLicenseEnum.PEEL1;

        // 维护者
        String maintainer() default "";

        // 贡献者列表
        String contributors() default "";

        // 代码库的地址
        String url() default "";

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface module {

        // 模块编码，严重警告：若已安装，该值不可变更
        String value();

    }

}
