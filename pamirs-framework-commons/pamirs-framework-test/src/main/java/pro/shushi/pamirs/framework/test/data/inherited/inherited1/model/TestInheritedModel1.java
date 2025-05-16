package pro.shushi.pamirs.framework.test.data.inherited.inherited1.model;

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
@Model.model("test.InheritedModel1")
@Model(displayName="测试模型2", summary="测试模型2")
public class TestInheritedModel1 extends TestModel {

    private static final long serialVersionUID = 5307497487278138498L;

    @Field.String
    @Field
    private Integer addField;

}
