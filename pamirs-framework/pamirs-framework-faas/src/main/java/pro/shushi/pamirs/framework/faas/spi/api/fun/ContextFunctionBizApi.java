package pro.shushi.pamirs.framework.faas.spi.api.fun;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 内置上下文函数接口扩展点 - 商业相关
 * 2021/3/3 10:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ContextFunctionBizApi {

    /**
     * 获取当前用户的合作伙伴
     *
     * @return 合作伙伴
     */
    Object currentPartner();

    /**
     * 获取当前用户的合作伙伴的id
     *
     * @return 合作伙伴的id
     */
    Long currentPartnerId();

    /**
     * 获取当前用户部门
     *
     * @return 当前用户部门
     */
    Object currentUserDepart();

    /**
     * 获取当前用户部门编码
     *
     * @return 当前用户部门编码
     */
    String currentUserDepartCode();


}
