package pro.shushi.pamirs.core.common.builder;

/**
 * 建造者接口
 *
 * @param <T> 构建对象
 */
public interface IBuilder<T> {

    /**
     * 构建
     *
     * @return 构建对象
     */
    T build();
}
