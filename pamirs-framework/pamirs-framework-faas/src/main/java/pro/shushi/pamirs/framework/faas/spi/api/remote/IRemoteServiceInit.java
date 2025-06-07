package pro.shushi.pamirs.framework.faas.spi.api.remote;

import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Map;

/**
 * 远程服务注册与发布
 * <p>
 * 2021/8/20 8:24 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface IRemoteServiceInit {

    boolean init(Map<String/*module*/, Meta> metaMap);

    boolean init(List<String> models);

}
