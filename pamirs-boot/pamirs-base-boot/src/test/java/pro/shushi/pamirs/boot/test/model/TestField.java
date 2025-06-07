package pro.shushi.pamirs.boot.test.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 测试字段
 * <p>
 * 2020/4/25 4:32 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model.model("test.TestField")
@Model(displayName = "测试字段")
public class TestField extends IdModel {

    @Field
    private Integer intValue;

    @Field
    private String stringValue;

    @Field
    @Field.Relation(relationFields = "model")
    private TestModel testModel;

    @Field
    private String model;

}
