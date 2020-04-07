package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * orm计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface OrmComputer<T, R> extends CommonApi {

    /**
     * orm装换
     *
     * @param model
     * @param o
     * @param mdConverter
     * @param listConverter
     * @param arrayConverter
     * @return
     */
    R compute(String model, T o,
                     OrmConverter<R> mdConverter,
                     OrmConverter<R> listConverter,
                     OrmConverter<R> arrayConverter
    );

    interface OrmConverter<R> {
        R compute();
    }

}
