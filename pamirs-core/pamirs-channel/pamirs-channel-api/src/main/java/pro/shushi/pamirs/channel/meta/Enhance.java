package pro.shushi.pamirs.channel.meta;

import pro.shushi.pamirs.channel.enmu.IncrementEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enhance
 *
 * @author yakir on 2022/08/31 18:12.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enhance {

    // 索引名称, 为空表示自动生成
    String index() default "";

    // 索引别名, 为空表示自动生成
    String alias() default "";

    // 分片数
    String shards() default "5";

    // 副本数
    String replicas() default "0";

    // 增量同步
    IncrementEnum increment() default IncrementEnum.CLOSE;

    // 全量同步之后移动别名
    boolean reAlias() default true;

    // 全量同步批次容量
    long batchSize() default 200L;

    Analyzer[] analyzers() default {};

}
