package pro.shushi.pamirs.meta.api.core.compute.systems.constraint;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.x.XService;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;

/**
 * 字段校验接口
 *
 * @param <T> 入参类型
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:50 上午
 */
@Fun(value = NamespaceConstants.constraint)
@XService(publish = false)
public interface Checker<T> extends CommonApi {

    /**
     * 校验
     *
     * @param value 值
     * @return 是否匹配
     */
    Boolean check(T value);

}
