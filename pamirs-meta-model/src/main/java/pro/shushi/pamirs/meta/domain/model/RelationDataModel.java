package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;
import java.util.Map;

@Base
@Model.model(RelationDataModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model(displayName = "关系数据")
public class RelationDataModel extends TransientModel {
    public static final String MODEL_MODEL = "base.RelationDataModel";

    @Field(displayName = "表单数据")
    private Map<String,Object> data;

    /**
     * 字段名 : (增删改: 数据)
     */
    @Field(displayName = "关系字段数据")
    private Map<String, Map<String, List<RelationDataModel>>> relations;

}
