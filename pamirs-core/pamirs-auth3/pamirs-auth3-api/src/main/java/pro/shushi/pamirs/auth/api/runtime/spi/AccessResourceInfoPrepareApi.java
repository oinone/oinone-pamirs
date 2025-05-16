package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;

/**
 * 访问资源信息预处理
 *
 * @author Adamancy Zhang at 20:47 on 2024-03-01
 */
public interface AccessResourceInfoPrepareApi {

    void prepareAccessInfo(Function function, String namespace, String fun, FunctionSourceEnum source);
}
