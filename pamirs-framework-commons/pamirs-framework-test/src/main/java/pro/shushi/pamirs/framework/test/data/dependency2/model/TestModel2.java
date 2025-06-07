package pro.shushi.pamirs.framework.test.data.dependency2.model;

import pro.shushi.pamirs.framework.test.data.dependency1.model.TestModel;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 测试模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 5:48 下午
 */
@Model.model(TestModel2.MODEL_MODEL)
@Model(displayName = "测试模型2", summary = "测试模型2")
public class TestModel2 extends TestModel {

    public static final String MODEL_MODEL = "test.Model2";
    private static final long serialVersionUID = 3542253186269612015L;

    @Field.Boolean
    @Field
    private Integer field;

}
