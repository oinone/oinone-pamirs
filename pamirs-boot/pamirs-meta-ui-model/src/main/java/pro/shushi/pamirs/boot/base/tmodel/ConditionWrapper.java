package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.Map;

@Base
@Model.model(ConditionWrapper.MODEL_MODEL)
@Model(displayName = "查询条件")
public class ConditionWrapper extends TransientModel {

    private static final long serialVersionUID = 8483736574402915828L;

    public static final String MODEL_MODEL = "base.ConditionWrapper";

    @Field.String
    @Field(displayName = "模型编码")
    private String model;

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



}