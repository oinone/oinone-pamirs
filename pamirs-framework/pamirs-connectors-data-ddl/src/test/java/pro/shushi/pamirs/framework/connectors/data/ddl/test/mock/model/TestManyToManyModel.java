package pro.shushi.pamirs.framework.connectors.data.ddl.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;

import java.util.List;

import static pro.shushi.pamirs.framework.connectors.data.ddl.test.mock.model.TestManyToManyModel.MODEL_MODEL;

/**
 * 测试多对多模型
 * <p>
 * 2020/7/30 3:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model.Static
@Model.model(MODEL_MODEL)
@Model(displayName = "应用分类", summary = "应用分类", labelFields = "name")
public class TestManyToManyModel extends IdModel {

    public static final String MODEL_MODEL = "test.TestManyToManyModel";

    private static final long serialVersionUID = 3600347100528995952L;
    @Field
    private String name;

    @Field.many2many
    private List<ModuleCategory> categoryList;

}
