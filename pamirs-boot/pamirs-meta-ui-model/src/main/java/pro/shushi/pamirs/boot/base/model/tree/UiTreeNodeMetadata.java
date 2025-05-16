package pro.shushi.pamirs.boot.base.model.tree;

import org.springframework.beans.BeanUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;
import java.util.Map;

@Base
@Model.model(UiTreeNodeMetadata.MODEL_MODEL)
@Model(displayName = "树节点配置", summary = "树节点配置")
public class UiTreeNodeMetadata extends TransientModel {

    public static final String MODEL_MODEL = "base.UiTreeNodeMetadata";

    @Field.String
    @Field(displayName = "节点key")
    private String key;

    @Field.String
    @Field(displayName = "当前节点模型")
    private String model;

    @Field.String
    @Field(displayName = "关联字段模型")
    private String relModel;

    @Field.String
    @Field(displayName = "关联字段")
    private String relField;

    @Field.String
    @Field(displayName = "自关联字段")
    private String selfRelField;

    @Field.String
    @Field(displayName = "过滤条件rsql")
    private String filter;

    @Field.String
    @Field(displayName = "数据标题")
    private String label;

    @Field.String
    @Field(displayName = "图标")
    private String icon;

    @Field.String
    @Field(displayName = "数据标题字段列表", multi = true)
    private List<String> labelFields;

    @Field.String
    @Field(displayName = "数据标题字段列表", multi = true)
    private List<String> searchFields;

    @Field(displayName = "数据标题翻译")
    private Boolean translate;

    @Field.Boolean
    @Field(displayName = "树展开结束层级")
    private Boolean expandEndLevel;

    // m2m查询中间表的数据缓存,用于构造父子节点关联的key
    private List<Map<String, Object>> throughMapList;

    private UiTreeNodeMetadata next;

    public UiTreeNodeMetadata copy() {
        UiTreeNodeMetadata copied = new UiTreeNodeMetadata();
        BeanUtils.copyProperties(this, copied);
        copied.setThroughMapList(null);
        return copied;
    }
}