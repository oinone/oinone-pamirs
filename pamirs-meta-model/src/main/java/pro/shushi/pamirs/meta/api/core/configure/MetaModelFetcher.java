package pro.shushi.pamirs.meta.api.core.configure;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaModel;

import java.util.List;

/**
 * 获取元模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface MetaModelFetcher extends CommonApi {

    /**
     * 获取元模型
     *
     * @return
     */
    List<MetaModel> fetchMetaModel();

}
