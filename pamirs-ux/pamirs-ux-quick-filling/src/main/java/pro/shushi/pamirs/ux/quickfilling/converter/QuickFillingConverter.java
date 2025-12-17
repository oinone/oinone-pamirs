package pro.shushi.pamirs.ux.quickfilling.converter;

import java.util.List;

/**
 * 快速填报转换API
 *
 * @author Adamancy Zhang at 12:20 on 2025-11-27
 */
public interface QuickFillingConverter {

    /**
     * 是否为基础类型转换器
     */
    default boolean isBasicConverter() {
        return true;
    }

    /**
     * 单值转换
     *
     * @param row   当前行
     * @param value 值
     */
    void convert(QuickFillingRow row, String value);

    /**
     * 以列纬度进行数据收集
     */
    void collect(QuickFillingRow row, String value);

    /**
     * 以列纬度进行批量填充
     */
    void fill();

}
