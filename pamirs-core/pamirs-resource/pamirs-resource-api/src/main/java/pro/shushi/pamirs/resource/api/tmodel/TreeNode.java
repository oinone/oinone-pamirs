package pro.shushi.pamirs.resource.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(TreeNode.MODEL_MODEL)
@Model(displayName = "树")
public class TreeNode extends TransientModel {

    public static final String MODEL_MODEL = "resource.TreeNode";

    @Field.Integer
    @Field(displayName = "id")
    private Long id;

    @Field.String
    @Field(displayName = "名称")
    private String name;

    @Field.String
    @Field(displayName = "编码")
    private String code;

}
