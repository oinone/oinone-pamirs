package pro.shushi.pamirs.meta.api.core.faas.boot;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Set;

/**
 * 获取启动模块列表接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ModulesApi {

    /**
     * 获取启动模块列表
     *
     * @return 启动模块列表
     */
    Set<String> modules();

    void setModules(Set<String> modules);

}
