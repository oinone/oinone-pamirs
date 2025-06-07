package pro.shushi.pamirs.framework.test.data.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.BaseModel;

import static pro.shushi.pamirs.framework.test.data.base.model.TestNoIdModel.MODEL_MODEL;

/**
 * 测试模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 5:48 下午
 */
@Model.model(MODEL_MODEL)
@Model(displayName = "测试模型", summary = "测试模型")
public class TestNoIdModel extends BaseModel {

    public static final String MODEL_MODEL = "test.TestNoIdModel";

    @Field.Integer
    @Field
    private Integer field;

}
