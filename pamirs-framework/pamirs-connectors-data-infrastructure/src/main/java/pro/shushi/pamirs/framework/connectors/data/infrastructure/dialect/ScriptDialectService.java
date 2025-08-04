package pro.shushi.pamirs.framework.connectors.data.infrastructure.dialect;

import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import jakarta.validation.constraints.NotBlank;

/**
 * 脚本执行方言服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Dialect
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ScriptDialectService {

    void run(@NotBlank String dsKey, @NotBlank String script);

    void ddl(@NotBlank String dsKey, @NotBlank String script);

    void unlockTables(@NotBlank String dsKey);

    String trimScript(String script);

}
