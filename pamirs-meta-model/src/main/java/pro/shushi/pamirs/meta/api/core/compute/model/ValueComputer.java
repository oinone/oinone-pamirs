package pro.shushi.pamirs.meta.api.core.compute.model;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

/**
 * 字段值计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/4/2 3:04 上午
 */
public interface ValueComputer {

    <T> void compute(ModelFieldConfig modelField, T data);

}
