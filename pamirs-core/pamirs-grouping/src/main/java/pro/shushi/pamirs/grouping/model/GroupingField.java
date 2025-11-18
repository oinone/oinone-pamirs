package pro.shushi.pamirs.grouping.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Base
@Model(displayName = "分组字段")
@Model.model(GroupingField.MODEL_MODEL)
public class GroupingField extends TransientModel {

    private static final long serialVersionUID = 8580269052821867685L;

    public static final String MODEL_MODEL = "grouping.GroupingField";

    @Field(displayName = "字段名")
    private String field;

    @Field(displayName = "字段值")
    private Object value;

    @Field(displayName = "排序方式")
    private SortDirectionEnum direction;

}
