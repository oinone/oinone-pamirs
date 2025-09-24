package pro.shushi.pamirs.meta.annotation;

import org.springframework.core.annotation.AliasFor;
import pro.shushi.pamirs.meta.common.constants.MetaValueConstants;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ClientTypeEnum;
import pro.shushi.pamirs.meta.enmu.SoftwareLicenseEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模块
 *
 * @author deng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    // 显示名称
    @AliasFor("displayName")
    String value() default "";

    // 显示名称
    @AliasFor("value")
    String displayName() default "";

    // 模块api名称
    // 只支持大小写英文和数字，且由英文字符开头
    String name() default "";

    // repository上发布的最新版本
    String version() default "1.0.0";

    // 分类编码
    String category() default "";

    // 描述摘要
    String summary() default "";

    // 依赖模块名列表
    String[] dependencies() default ModuleConstants.MODULE_BASE;

    // 上游模块名列表
    String[] upstreams() default {};

    // 互斥模块名列表
    String[] exclusions() default {};

    ClientTypeEnum[] clientTypes() default {ClientTypeEnum.PC, ClientTypeEnum.MOBILE, ClientTypeEnum.PAD};

    ActiveEnum show() default ActiveEnum.ACTIVE;

    // 排序用的，默认100，order by priority asc, write_date desc
    long priority() default MetaValueConstants.priority;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Ds {
        // 数据源名
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Hook {

        // 不执行的hook
        String[] excludes();

    }

    // 更多配置
    Advanced advanced() default @Advanced;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Advanced {

        // 模块名简称
        // 小于等于8个字符，只支持小写英文和数字，且由英文字符开头
        // 若缺省则取模块编码（模块编码由下划线分隔则取最后的下划线之后部分字符，所得字符再超出8个字符则取前8个字符）
        String abbr() default "";

        // module的作者
        String author() default "";

        // 描述
        String description() default "";

        // 是否应用，应用是可访问页面的模块
        boolean application() default true;

        // 是否是demo
        boolean demo() default false;

        // 是否是web
        boolean web() default true;

        // 是否核心应用
        boolean core() default false;

        // 是否自建应用
        boolean selfBuilt() default false;

        // 是否需要跳转到website去购买
        boolean toBuy() default false;

        // 站点
        String website() default "";

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
        // 只支持小写英文、数字和下划线，且由英文字符开头
        String value();

    }

    // 低无一体融合模块
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Fuse {

    }

}
