package pro.shushi.pamirs.meta.base.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * NameCodeModel
 *
 * @author yakir on 2021/08/12 11:00.
 */
@Base
@Model.model(NameCodeModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 37)
@Model(displayName = "基础存储模型", summary = "该模型包含名称字段、编码字段，编码字段可以设置自动生成的数据编码")
abstract
public class NameCodeModel extends CodeModel {

    private static final long serialVersionUID = -1310429028188060023L;

    public final static String MODEL_MODEL = "base.NameCodeModel";

    @Base
    @Field(displayName = "名称", priority = 99)
    private String name;

}
