package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;
import java.util.Map;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组结果")
@Model.model(GroupResult.MODEL_MODEL)
public class GroupResult<T> extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupResult";

    @Field(displayName = "一级分组总记录数", defaultValue = "0", invisible = true)
    private Long totalElements;

    @Field(displayName = "一级分组总页数", defaultValue = "0", invisible = true)
    private Integer totalPages;

    @Field(displayName = "当前所有一级分组下的总数据量")
    private Long totalDataCount;

    @Field(displayName = "返回的分组信息")
    private List<GroupInfo<T>> groups;

    @Field(displayName = "展开的分组数据")
    private List<String> expandGroupDataStr;

    private Map<GroupPath<T>, String> expandGroupData;

    private Map<GroupPath<T>, Map<String, Object>> expandGroupStatistic;

}
