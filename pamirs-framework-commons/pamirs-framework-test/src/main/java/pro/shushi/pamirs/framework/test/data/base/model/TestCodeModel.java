package pro.shushi.pamirs.framework.test.data.base.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;

/**
 * 测试编码模型
 * <p>
 * 2020/7/30 3:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model.model("test.TestCodeModel")
@Model(displayName = "测试编码模型", summary = "测试编码模型", labelFields = "name")
public class TestCodeModel extends CodeModel {
    private static final long serialVersionUID = -5385609467272259419L;

    @Field
    private String name;

}
