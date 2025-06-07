package pro.shushi.pamirs.meta.common.enmu;

import com.google.common.collect.Lists;
import pro.shushi.pamirs.meta.common.util.BitUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 二进制枚举接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:53 上午
 */
public interface BitEnum extends IEnum<Long> {

    default <T extends BitEnum> boolean in(List<T> options) {
        if (null == options) {
            return false;
        }
        for (T e : options) {
            if (this.value().equals(e.value())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将当前枚举加入到列表中
     *
     * @param options
     * @param <T>
     * @return
     */
    default <T extends BitEnum> List<T> addTo(List<T> options) {
        if (options == null) {
            return Lists.newArrayList((T) this);
        }
        if (!in(options)) {
            options.add((T) this);
        }
        return options;
    }

    /**
     * 将当前枚举从列表中移出
     *
     * @param options
     * @param <T>
     * @return
     */
    default <T extends BitEnum> List<T> removeFrom(List<T> options) {
        if (options == null) {
            return new ArrayList<>();
        }
        if (in(options)) {
            options.remove(this);
        }
        return options;
    }

    /**
     * 查询按位与的位数的值是否在值内
     *
     * @param bits 原属性
     * @return 是否存在
     */
    default boolean isBitIn(Long bits) {
        return BitUtil.has(bits, BitUtil.longValue(value()));
    }

    /**
     * 原属性中添加属性
     *
     * @param bits 原属性
     * @return 最终属性值
     */
    default long setBitIn(Long bits) {
        return BitUtil.enable(bits, BitUtil.longValue(value()));
    }

    /**
     * 原属性中去掉属性
     *
     * @param bits 原属性
     * @return 最终属性值
     */
    default long unsetBitIn(Long bits) {
        return BitUtil.disable(bits, BitUtil.longValue(value()));
    }

    /**
     * 获取属性位置，相当于log2 + 1
     *
     * @return 位置
     */
    default int getBitPos() {
        return Double.valueOf((Math.log(BitUtil.longValue(value())) / Math.log(2)) + 1).intValue();
    }

}
