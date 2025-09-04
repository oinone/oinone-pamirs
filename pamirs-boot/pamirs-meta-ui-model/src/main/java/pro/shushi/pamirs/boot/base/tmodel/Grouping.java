package pro.shushi.pamirs.boot.base.tmodel;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组")
@Model.model(Grouping.MODEL_MODEL)
public class Grouping<T extends D> extends TransientModel {

    public static final String MODEL_MODEL = "base.Grouping";

    @Field(displayName = "页面模型")
    private String model;

    @Field(displayName = "查询条件")
    private ConditionQueryWrapper queryWrapper;

    @Field(displayName = "是否是查询全部数据")
    private Boolean isFetchAll;

    @Field(displayName = "是否需要分页")
    private Boolean needPagination;

    @Field(displayName = "所有分组字段")
    private List<GroupField> groupFields;

    @Field(displayName = "当前已选择的分组字段")
    private List<GroupSelectField> selectGroupFields;

    @Field(displayName = "所选统计字段")
    private List<GroupField> statisticFields;

    private ModelConfig modelConfig;

    private Map<String, ModelFieldConfig> modelFieldConfigCache = new HashMap<>();

    private List<GroupField> sqlGroupFields;

    // 总数据量
    private long totalCount;

    public ModelFieldConfig getModelFieldConfig(String field) {
        return getModelFieldConfigCache().computeIfAbsent(field,
                key -> getModelConfig().getModelFieldConfigList().stream().filter(fieldConfig -> StringUtils.equals(fieldConfig.getField(), key)).findFirst().orElse(null)
        );
    }

}
