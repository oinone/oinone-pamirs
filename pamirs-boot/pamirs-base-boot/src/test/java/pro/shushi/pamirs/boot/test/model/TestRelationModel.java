package pro.shushi.pamirs.boot.test.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.domain.module.ModuleCategory;

import java.util.List;

import static pro.shushi.pamirs.boot.test.model.TestRelationModel.MODEL_MODEL;

/**
 * 测试关系模型
 * <p>
 * 2020/7/30 3:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model.model(MODEL_MODEL)
@Model(displayName = "测试关系模型", summary = "测试关系模型", labelFields = "name")
public class TestRelationModel extends CodeModel {

    public static final String MODEL_MODEL = "test.TestRelationModel";

    private static final long serialVersionUID = 3600347100528995952L;
    @Field(unique = true)
    private String name;

    @Field.many2many(through = "TestRelationModelRel")
    @Field
    private List<ModuleCategory> categoryList;

    @Field.one2many
    @Field.Relation(referenceFields = "childId")
    @Field
    private List<ModuleCategory> children;

    @Field
    private ModuleCategory parent;

    @Field.one2one
    @Field
    private ModuleCategory one;

    @Field.many2one
    @Field.Relation(relationFields = {"code", "name"}, referenceFields = {"code", "name"})
    @Field
    private ModuleCategory parent1;

    @Field.one2one
    @Field.Relation(relationFields = {"code", "name"}, referenceFields = {"code", "name"})
    @Field
    private ModuleCategory one1;

}
