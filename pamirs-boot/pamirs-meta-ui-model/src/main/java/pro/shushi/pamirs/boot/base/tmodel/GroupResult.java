package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组信息")
@Model.model(GroupResult.MODEL_MODEL)
public class GroupResult extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupResult";

    @Field(displayName = "总记录数", defaultValue = "0", invisible = true)
    private Long totalElements;

    @Field(displayName = "总页数", defaultValue = "0", invisible = true)
    private Integer totalPages;

    @Field(displayName = "是否获取了所有数据")
    private Boolean isFetchAll;

    @Field(displayName = "返回的分组信息")
    private List<GroupInfo> groups;

}
