package pro.shushi.pamirs.boot.base.ux.model.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.framework.orm.xml.converter.CommaSingleValueConverter;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

import java.util.List;

@Base
@XStreamAlias(TemplateNodeConstants.NODE_NODE)
@Model(displayName = "节点", summary = "节点")
public class UINode extends UIWidget {

    private static final long serialVersionUID = -715562350550246413L;

    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "标题", summary = "级联-标题")
    private String title;

    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "过滤条件", summary = "过滤条件，使用RSQL协议")
    private String filter;

    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "数据标题")
    private String label;

    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "数据标题字段列表", multi = true, serialize = Field.serialize.COMMA)
    private List<String> labelFields;

    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "当前模型")
    private String model;

    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "层级关联")
    private String references;

    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "自关联")
    private String selfReferences;

    @XStreamAsAttribute
    @Field.String
    @Field(displayName = "列表关联")
    private String search;

}