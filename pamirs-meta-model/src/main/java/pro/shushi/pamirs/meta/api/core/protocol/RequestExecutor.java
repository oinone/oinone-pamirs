package pro.shushi.pamirs.meta.api.core.protocol;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestParam;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;

import java.util.List;

/**
 * 请求执行器接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:27 上午
 */
public interface RequestExecutor {

    PamirsRequestResult execute(PamirsRequestParam param);

    List<PamirsRequestResult> executeAsync(List<PamirsRequestParam> pamirsRequestParamList);

}
