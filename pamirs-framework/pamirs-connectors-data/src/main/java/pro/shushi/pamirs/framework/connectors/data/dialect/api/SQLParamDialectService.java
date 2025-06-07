package pro.shushi.pamirs.framework.connectors.data.dialect.api;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;

/**
 * 脚本执行方言服务 ，WriteDate等参数处理
 *
 * @author wangxian 2023-06-29
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SQLParamDialectService {

    void resolveIfWriteDate(Map<String, Object> map);

    void resolveWriteDate(Map<String, Object> map);
}
