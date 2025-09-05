package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;
import java.util.Map;

@Base
@Model.model(ConditionQueryWrapper.MODEL_MODEL)
@Model(displayName = "查询条件")
public class ConditionQueryWrapper extends TransientModel {

    public static final String MODEL_MODEL = "base.ConditionQueryWrapper";

    @Base
    @Field.String
    @Field(displayName = "模型编码")
    private String model;

    @Base
    @Field.String
    @Field(displayName = "rsql")
    private String rsql;

    @Base
    @Field.many2one
    @Field(displayName = "排序")
    private Sort sort;

    @Base
    @Field(displayName = "传输数据")
    private Map<String, Object> queryData;

    @Base
    @Field(displayName = "属性选择")
    private List<String> selects;

}