package pro.shushi.pamirs.resource.api.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

import java.util.List;

@Model.model(RegionNode.MODEL_MODEL)
@Model(displayName = "区域地址")
public class RegionNode extends TreeNode {

    public static final String MODEL_MODEL = "resource.RegionNode";

    @Field(displayName = "下级")
    @Field.one2many
    private List<RegionNode> children;
}
