package pro.shushi.pamirs.channel.meta;

import pro.shushi.pamirs.channel.constant.IkAnalyzer;
import pro.shushi.pamirs.channel.constant.IkSearchAnalyzer;

import java.lang.annotation.*;

/**
 * IkAnalyzer
 *
 * @author yakir on 2023/02/09 16:24.
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Analyzer {

    /**
     * 字段
     */
    String value();

    /**
     * 默认分词器
     */
    String analyzer() default IkAnalyzer.STANDARD;

    /**
     * 搜索分词器
     */
    String searchAnalyzer() default IkSearchAnalyzer.STANDARD;

}
