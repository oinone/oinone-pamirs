package pro.shushi.pamirs.framework.configure.inject.domain;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 自动注入字段配置
 * <p>
 * 2020/10/13 8:30 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class MetaFieldConfig {

    private String fromFieldName;

    private String toFieldName;

    private Class<?> toFieldClass;

    private boolean list;

}
