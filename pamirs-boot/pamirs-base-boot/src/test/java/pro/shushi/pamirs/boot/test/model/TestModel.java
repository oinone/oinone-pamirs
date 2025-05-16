package pro.shushi.pamirs.boot.test.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;

/**
 * 测试模型
 *
 * 2020/4/25 4:32 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model.model("test.TestModel")
@Model.Advanced(unique = "model")
@Model(displayName = "测试模型")
public class TestModel extends IdModel {

    @Field
    private Integer intValue;

    @Field
    private String stringValue;

    @Field.one2many
    @Field.Relation(relationFields = "model")
    @Field
    private List<TestField> fields;

    @Field
    private String model;

}
