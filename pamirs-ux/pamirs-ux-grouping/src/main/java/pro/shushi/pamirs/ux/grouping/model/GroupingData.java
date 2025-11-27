package pro.shushi.pamirs.ux.grouping.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组信息")
@Model.model(GroupingData.MODEL_MODEL)
public class GroupingData extends TransientModel {

    private static final long serialVersionUID = -3315270915746384140L;

    public static final String MODEL_MODEL = "grouping.GroupingData";

    @Field(displayName = "字段名")
    private String field;

    @Field(displayName = "分组值")
    private Object value;

    @Field(displayName = "分组值是否为JSON格式")
    private Boolean isJsonValue;

    @Field(displayName = "分组数据")
    private String data;

    @Field(displayName = "是否为数据节点")
    private Boolean isLeaf;

    @Field(displayName = "下一级分组信息")
    private List<GroupingData> groups;

}
