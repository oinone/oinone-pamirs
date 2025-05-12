package pro.shushi.pamirs.meta.api.core.orm.processor;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.List;
import java.util.Map;

/**
 * 名称映射处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface NameProcessor {

    /**
     * 单个字段转换
     *
     * @param fieldConfig 字段配置
     * @param origin      原值
     * @return 转换结果
     */
    Map<String, Object> convert(ModelFieldConfig fieldConfig, Map<String, Object> origin);

    /**
     * 整个map转换
     *
     * @param model  模型
     * @param origin 原值
     * @return 转换结果
     */
    Map<String, Object> convert(String model, Map<String, Object> origin);

    /**
     * 整个list转换
     *
     * @param model  模型
     * @param origin 原值
     * @return 转换结果
     */
    List<Map<String, Object>> convert(String model, List<Map<String, Object>> origin);

}
