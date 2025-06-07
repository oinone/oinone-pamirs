package pro.shushi.pamirs.boot.base.model.tree;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;
import java.util.Map;

@Slf4j
@Base
@Model.model(UiTreeNode.MODEL_MODEL)
@Model(displayName = "树节点配置", summary = "树节点配置")
// FIXME: 2022/8/3 存储模型才有xxxPage
public class UiTreeNode extends IdModel {

    public static final String MODEL_MODEL = "base.UiTreeNode";

    @Field.String
    @Field(displayName = "节点key")
    private String metadataKey;

    @Field.String
    @Field(displayName = "key")
    private String key;

    @Field.String
    @Field(displayName = "parentKeys", multi = true)
    private List<String> parentKeys;

    // 两个配置之间的关联
    private List<String> relParentKeys;

    //一个配置自循环之间的关联
    private String selfParentKey; // 自循环现在只允许从1变多

    @Field.String
    @Field(displayName = "value")
    private String value;

    private Map<String, Object> valueObj;

    @Field.String
    @Field(displayName = "label")
    private String label;

    @Field(displayName = "图标")
    private String icon;

    //对象的pk值,用于构建key
    private String pkValue;

    @Field.Boolean
    @Field(displayName = "补充数据", summary = "回填已选中的数据时,不足指定长度时填充数据")
    private Boolean filler;

    @Field.Boolean
    @Field(displayName = "标记是否还有子节点")
    private Boolean isLeaf;

    @Field(displayName = "子节点")
    private List<UiTreeNode> nodes;

    //自循环树的顶级和末级节点标记
    private Boolean isSelfTreeRoot;
    private Boolean isSelfTreeLastLeaf;
}