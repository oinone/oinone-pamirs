package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.dto.common.Result;

/**
 * 扩展点API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface ExtPointApi<R> {

    /**
     * 执行扩展点
     *
     * @param extPointName
     * @param args
     * @return
     */
    R run(String extPointName, Object... args);

    /**
     * 执行默认扩展点
     *
     * @param extPointName
     * @param args
     * @return
     */
    R runDefault(String extPointName, Object... args);

}
