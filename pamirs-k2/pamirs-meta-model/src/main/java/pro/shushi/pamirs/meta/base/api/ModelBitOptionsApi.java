package pro.shushi.pamirs.meta.base.api;

/**
 * 模型按位运算api
 * <p>
 * 2022/5/7 1:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ModelBitOptionsApi<T> {

    /**
     * 按位生效
     *
     * @param bit 位
     * @return 对象
     */
    T enableBitOption(Long bit);

    /**
     * 按位失效
     *
     * @param bit 位
     * @return 对象
     */
    T disableBitOption(Long bit);

    /**
     * 按位与有效
     *
     * @param bit 位
     * @return 是否有效
     */
    boolean hasBitOption(Long bit);

}
