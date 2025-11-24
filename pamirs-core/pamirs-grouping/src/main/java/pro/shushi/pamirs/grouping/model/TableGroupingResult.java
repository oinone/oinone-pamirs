package pro.shushi.pamirs.grouping.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 表格分组结果
 *
 * @author Gesi at 15:46 on 2025/9/1
 */
@Base
@Model(displayName = "表格分组结果")
@Model.model(TableGroupingResult.MODEL_MODEL)
public class TableGroupingResult extends TransientModel {

    private static final long serialVersionUID = 8412195160011406354L;

    public static final String MODEL_MODEL = "grouping.TableGroupingResult";

    @Field(displayName = "一级分组总记录数", defaultValue = "0", invisible = true)
    private Long totalElements;

    @Field(displayName = "一级分组总页数", defaultValue = "0", invisible = true)
    private Integer totalPages;

    @Field(displayName = "是否展开全部")
    private Boolean expandedAll;

    @Field(displayName = "分组数据")
    private List<GroupingData> groups;

}
