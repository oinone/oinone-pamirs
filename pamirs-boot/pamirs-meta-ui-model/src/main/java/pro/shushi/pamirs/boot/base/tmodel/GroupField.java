package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.boot.base.enmu.GroupStatisticTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组字段")
@Model.model(GroupField.MODEL_MODEL)
public class GroupField extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupField";

    @Field(displayName = "页面模型")
    private String field;

    @Field(displayName = "排序类型")
    private SortDirectionEnum orderType;

    @Field(displayName = "统计类型")
    private GroupStatisticTypeEnum statisticType;

}
