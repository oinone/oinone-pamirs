package pro.shushi.pamirs.meta.api.core.orm.systems.relation;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.List;

/**
 * 级联操作管理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
public interface RelationCascadeManager {

    /**
     * 逻辑外键约束更新检查
     *
     * @param data        实体值
     * @param modelConfig 模型配置
     */
    void onUpdateCheck(Object data, ModelConfig modelConfig);

    /**
     * 逻辑外键约束更新检查
     *
     * @param data        实体值
     * @param modelConfig 模型配置
     * @param checkFields 校验字段集
     */
    void onUpdateCheck(Object data, ModelConfig modelConfig, List<String> checkFields);

    /**
     * 逻辑外键约束删除检查
     *
     * @param data        实体值
     * @param modelConfig 模型配置
     */
    void onDeleteCheck(Object data, ModelConfig modelConfig);

    /**
     * 逻辑外键约束更新检查
     *
     * @param data             实体值
     * @param modelFieldConfig 关联关系字段配置
     */
    void onUpdateCheck(Object data, ModelFieldConfig modelFieldConfig);

    /**
     * 逻辑外键约束删除检查
     *
     * @param data             实体值
     * @param modelFieldConfig 关联关系字段配置
     */
    void onDeleteCheck(Object data, ModelFieldConfig modelFieldConfig);


}
