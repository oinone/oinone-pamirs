package pro.shushi.pamirs.meta.api.core.orm.convert;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;

/**
 * 请求参数处理
 * <p>
 * 2021/3/12 6:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface ClientArgumentHandlerApi {

    void in(ModelConfig modelConfig, Function function, boolean isQuery, Map<String, Object> requestArgs, Object[] args);

    Object out(String returnModel, String model, Object obj);

}
