package pro.shushi.pamirs.grouping.model;

import pro.shushi.pamirs.grouping.enumeration.GroupStatisticMethodEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 分组统计字段
 *
 * @author Adamancy Zhang at 10:41 on 2025-11-13
 */
@Base
@Model(displayName = "分组统计字段")
@Model.model(GroupingStatisticField.MODEL_MODEL)
public class GroupingStatisticField extends TransientModel {

    private static final long serialVersionUID = -3942379234137312594L;

    public static final String MODEL_MODEL = "grouping.GroupingStatisticField";

    @Field(displayName = "字段名")
    private String field;

    @Field(displayName = "统计方式")
    private String statisticMethod;

}
