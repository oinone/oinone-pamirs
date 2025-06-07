package pro.shushi.pamirs.meta.annotation;

import pro.shushi.pamirs.meta.common.constants.FunctionDefaultsConstants;
import pro.shushi.pamirs.meta.enmu.*;

import java.lang.annotation.*;

/**
 * 函数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Function {

    // api名称
    // 供接口调用使用，非实际调用方法名称，默认与方法名称相同
    String name() default "";

    // 函数可执行场景
    FunctionSceneEnum[] scene() default {};

    // 描述
    String summary() default "";

    // 函数开放级别
    FunctionOpenEnum[] openLevel() default {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @Inherited
    @interface fun {

        // 函数编码，不可更改，默认与方法名称相同
        java.lang.String value();

    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface Advanced {

        // 展示名称
        String displayName() default "";

        // 函数类型
        FunctionTypeEnum[] type() default FunctionTypeEnum.UPDATE;

        // 是否是数据库管理器函数
        boolean managed() default false;

        // 函数语言
        FunctionLanguageEnum language() default FunctionLanguageEnum.JAVA;

        // 是否内置函数
        boolean builtin() default false;

        // 校验
        boolean check() default false;

        // 函数分类
        FunctionCategoryEnum category() default FunctionCategoryEnum.OTHER;

        // 系统分组
        String group() default FunctionDefaultsConstants.GROUP;

        // 系统版本
        String version() default FunctionDefaultsConstants.VERSION;

        // 超时时间
        int timeout() default FunctionDefaultsConstants.TIMEOUT;

        // 重试次数
        int retries() default 0;

        // 是否支持long polling
        boolean isLongPolling() default false;

        // long polling的唯一key，从前端上下文中获取
        String longPollingKey() default "userId";

        // long polling的超时时间，单位为秒
        int longPollingTimeout() default 1;

    }

}
