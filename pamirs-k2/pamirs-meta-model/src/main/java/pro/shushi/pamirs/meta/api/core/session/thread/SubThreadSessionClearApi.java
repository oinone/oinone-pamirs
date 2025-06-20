package pro.shushi.pamirs.meta.api.core.session.thread;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 清空关联请求（关联关系、引用字段查询）session接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface SubThreadSessionClearApi extends CommonApi {

    /**
     * 清空session
     */
    void clear();

}
