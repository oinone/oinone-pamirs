package pro.shushi.pamirs.boot.base.tmodel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组")
@Model.model(Grouping.MODEL_MODEL)
public class Grouping<T> extends TransientModel {

    public static final String MODEL_MODEL = "base.Grouping";

    @Field(displayName = "页面模型")
    private String model;

    @Field(displayName = "查询条件")
    private ConditionQueryWrapper queryWrapper;

    @Field(displayName = "总数据量")
    private Long totalDataCount;

    @Field(displayName = "所有分组字段")
    private List<GroupField> groupFields;

    @Field(displayName = "所选统计字段")
    private List<GroupField> statisticFields;

    @Field(displayName = "展开的分组路径")
    private List<GroupPath<T>> expandGroupPaths;

    @Field(displayName = "是否需要懒加载", defaultValue = "true")
    private Boolean needLazyLoad;

    private ModelConfig modelConfig;

    private Map<String, ModelFieldConfig> modelFieldConfigCache = new HashMap<>();

    public ModelFieldConfig getModelFieldConfig(String field) {
        return getModelFieldConfigCache().computeIfAbsent(field,
                key -> getModelConfig().getModelFieldConfigList().stream().filter(fieldConfig -> StringUtils.equals(fieldConfig.getField(), key)).findFirst().orElse(null)
        );
    }

    public boolean containsExpandPath(GroupPath<T> groupPath) {
        if (CollectionUtils.isEmpty(getExpandGroupPaths())) {
            return false;
        }
        return getExpandGroupPaths().contains(groupPath);
    }

}
