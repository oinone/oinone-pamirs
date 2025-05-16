package pro.shushi.pamirs.boot.test.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;

/**
 * 测试分类
 *
 * 2020/4/25 4:32 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model.model("test.TestCategory")
@Model(displayName = "测试分类")
public class TestCategory extends IdModel {

    @Field
    private Integer intValue;

    @Field
    private String stringValue;

    @Field.many2many(through = "modelRelCategory")
    @Field
    private List<TestModel> modelList;

    @Field
    private String model;

}
