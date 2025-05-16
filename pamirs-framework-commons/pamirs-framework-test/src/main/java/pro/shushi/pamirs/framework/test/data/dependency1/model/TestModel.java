package pro.shushi.pamirs.framework.test.data.dependency1.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 测试模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 5:48 下午
 */
@Model.Advanced(name = "model0")
@Model.model(TestModel.MODEL_MODEL)
@Model(displayName = "测试模型", summary = "测试模型")
public class TestModel extends IdModel {

    public static final String MODEL_MODEL = "test.Model";
    private static final long serialVersionUID = -6394015351349855855L;

    @Field.Integer
    @Field
    private Integer field;

}
