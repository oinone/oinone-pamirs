package pro.shushi.pamirs.boot.test.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum;

/**
 * 函数测试模型
 *
 * 2020/5/9 5:21 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model.model("test.TestFunction")
@Model(displayName = "测试函数", summary = "测试函数")
public class TestFunction extends IdModel {

    @Base
    @Field.Enum
    @Field(displayName = "函数语言", summary = "代码语言", defaultValue = "DSL", required = true)
    private FunctionLanguageEnum language;

}
