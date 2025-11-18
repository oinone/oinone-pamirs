package pro.shushi.pamirs.grouping.model;

import pro.shushi.pamirs.core.common.tmodel.CommonConditionWrapper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 表格分组查询条件
 *
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "表格分组查询条件")
@Model.model(TableGroupingWrapper.MODEL_MODEL)
public class TableGroupingWrapper extends TransientModel {

    private static final long serialVersionUID = 3581167861514625467L;

    public static final String MODEL_MODEL = "grouping.TableGroupingWrapper";

    @Field(displayName = "查询条件")
    private CommonConditionWrapper queryWrapper;

    @Field(displayName = "分组字段")
    private List<GroupingField> fields;

    @Field(displayName = "关联关系查询字段")
    private List<String> queryRelationFields;

    @Field(displayName = "统计字段")
    private GroupingStatisticField statisticField;
}
