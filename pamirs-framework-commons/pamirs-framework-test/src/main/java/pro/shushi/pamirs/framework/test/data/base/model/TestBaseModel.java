package pro.shushi.pamirs.framework.test.data.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

import static pro.shushi.pamirs.framework.test.data.base.model.TestBaseModel.MODEL_MODEL;

/**
 * 测试模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 5:48 下午
 */
@Model.model(MODEL_MODEL)
@Model(displayName = "测试模型", summary = "测试模型")
public class TestBaseModel extends BaseModel {

    public static final String MODEL_MODEL = "test.TestBaseModel";

    @Base
    @Field.PrimaryKey(keyGenerator = KeyGeneratorEnum.AUTO_INCREMENT)
    @Field.Integer(M = 20)
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    protected Long id;

    @Field.Integer
    @Field
    private Integer field;

}
