package pro.shushi.pamirs.meta.api.core.compute.template;

import pro.shushi.pamirs.meta.api.CommonApi;

/**
 * orm计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface OrmComputer<T, R> extends CommonApi {

    /**
     * orm转换
     *
     * @param model          模型
     * @param o              对象
     * @param mdConverter    模型转换
     * @param listConverter  列表转换
     * @param arrayConverter 数组转换
     * @return 转换结果
     */
    R compute(String model, T o,
              OrmConverter<R> mdConverter,
              OrmConverter<R> listConverter,
              OrmConverter<R> arrayConverter
    );

    interface OrmConverter<R> {
        R compute(String model, R o);
    }

}
