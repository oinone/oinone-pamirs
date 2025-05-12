package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * 并行与优先计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface Prioritized extends CommonApi {

    /**
     * 计算优先级
     * <p>
     * 如果优先级相同，则表示可以并行计算
     *
     * @return 优先级
     */
    int priority();

}
