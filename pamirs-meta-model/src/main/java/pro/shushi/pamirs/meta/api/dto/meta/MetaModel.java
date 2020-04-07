package pro.shushi.pamirs.meta.api.dto.meta;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 元模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 4:16 下午
 */
@Data
public class MetaModel {

    /**
     * 元模型类
     */
    private Class clazz;

    /**
     * 模型优先级
     */
    private Integer priority;

}
