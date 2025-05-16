package pro.shushi.pamirs.framework.test.data.dependency1.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * 测试模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 5:48 下午
 */
@Model.model(TestModel1.MODEL_MODEL)
@Model(displayName="测试模型1", summary="测试模型1")
public class TestModel1 extends TestModel {

    public static final String MODEL_MODEL = "test.Model1";
    private static final long serialVersionUID = 6101474868390978990L;

    @Field.String
    @Field
    private Integer field;

}
