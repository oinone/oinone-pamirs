package pro.shushi.pamirs.framework.compare;

import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.List;

/**
 * 跨模块增量扩展
 * 2021/2/4 11:01 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface CrossingExtendService {

    /**
     * 差量计算
     *
     * @param metaList 元数据
     */
    void diffCompute(List<Meta> metaList);

}
