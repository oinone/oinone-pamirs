package pro.shushi.pamirs.framework.test.data.inherited.inherited2.model;

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
@Model.model("test.InheritedModel2")
@Model(displayName = "测试模型2", summary = "测试模型2")
public class TestInheritedModel2 extends TestModel {

    @Field.Boolean
    @Field
    private Integer addField;

}
