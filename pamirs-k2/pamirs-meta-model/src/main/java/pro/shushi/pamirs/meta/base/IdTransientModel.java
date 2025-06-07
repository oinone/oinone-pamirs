package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * IdTransientModel
 *
 * @author yakir on 2021/08/12 14:11.
 */
@Base
@Model.model(IdTransientModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 43)
@Model(displayName = "基础传输模型", summary = "该模型包含名称字段、编码字段")
abstract
public class IdTransientModel extends TransientModel {

    private static final long serialVersionUID = 420662814076550890L;

    public final static String MODEL_MODEL = "base.IdTransientModel";

    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    private Long id;

    @Field.String
    @Field(displayName = "名称", summary = "名称", priority = 6)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "编码", priority = 7)
    private String code;

}
