package pro.shushi.pamirs.meta.api.core.compute.data;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;

/**
 * 字段值计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@Fun(value = NamespaceConstants.pamirs)
@XService(publish = false)
public interface FieldValueComputer<T> extends CommonApi {

    /**
     * 字段值计算
     *
     * @param fieldValue 字段值
     * @return 计算结果
     */
    Object compute(T fieldValue);

}
