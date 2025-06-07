package pro.shushi.pamirs.boot.base.ux.model.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import pro.shushi.pamirs.boot.base.ux.constants.TemplateNodeConstants;
import pro.shushi.pamirs.boot.base.ux.model.UIWidget;
import pro.shushi.pamirs.framework.orm.xml.converter.CommaSingleValueConverter;
import pro.shushi.pamirs.framework.orm.xml.converter.SemicolonSingleValueConverter;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

/**
 * 可选项
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@XStreamAlias(TemplateNodeConstants.NODE_OPTION)
@Model(displayName = "可选项")
public class UIOption extends UIWidget {

    private static final long serialVersionUID = -2478394148882807935L;

    // 数据字典元数据

    /**
     * 显示名称
     */
    @XStreamAsAttribute
    @Field(displayName = "显示名称")
    private String displayName;

    /**
     * 值
     */
    @XStreamAsAttribute
    @Field(displayName = "值")
    private String value;

    /**
     * 描述
     */
    @XStreamAsAttribute
    @Field(displayName = "描述")
    private String summary;

    /**
     * 状态
     */
    @XStreamAsAttribute
    @Field(displayName = "状态")
    private ActiveEnum state;

    // 关联模型元数据

    /**
     * 关联模型编码
     */
    @XStreamAsAttribute
    @Field(displayName = "关联模型编码")
    private String references;

    /**
     * 关联模型api名称
     */
    @XStreamAsAttribute
    @Field(displayName = "关联模型api名称")
    private String referencesModelName;

    /**
     * 关联模块api名称
     */
    @XStreamAsAttribute
    @Field(displayName = "关联模块api名称")
    private String referencesModuleName;

    /**
     * 关联模型类型
     */
    @XStreamAsAttribute
    @Field(displayName = "关联模型类型")
    private ModelTypeEnum referencesType;

    /**
     * 关联模型主键列表
     */
    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "关联模型主键列表")
    private List<String> referencesPks;

    /**
     * 关联模型唯一键列表
     */
    @XStreamConverter(SemicolonSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "关联模型唯一键列表")
    private List<String> referencesUniques;

    /**
     * 关联模型数据标题字段列表
     */
    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "关联模型标题字段列表")
    private List<String> referencesLabelFields;

    /**
     * 引用字段列表
     */
    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "引用字段列表")
    private List<String> related;

}
