package pro.shushi.pamirs.core.common.dsl.model.view;

import com.alibaba.fastjson.annotation.JSONField;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.core.common.dsl.model.UdWidget;
import pro.shushi.pamirs.core.common.dsl.model.part.UdOption;
import pro.shushi.pamirs.framework.orm.xml.converter.CommaSingleValueConverter;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 视图字段
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_FIELD)
@Model(displayName = "视图字段")
public class UdField extends UdWidget {

    private static final long serialVersionUID = -2969412632056427122L;

    /**
     * 模型编码
     */
    @XStreamAsAttribute
    @Field(displayName = "模型编码")
    private String model;

    @XStreamConverter(CommaSingleValueConverter.class)
    @XStreamAsAttribute
    @Field(displayName = "可选项字段列表")
    private List<String> optionFields;

    /**
     * 字段编码
     */
    @XStreamAsAttribute
    @Field(displayName = "字段编码")
    private String field;

    /**
     * 标题
     */
    @XStreamAsAttribute
    @Field(displayName = "标题")
    private String label;

    /**
     * 业务类型
     */
    @XStreamAsAttribute
    @Field(displayName = "业务类型")
    private TtypeEnum ttype;

//    /**
//     * 占位提示
//     */
//    @XStreamAsAttribute
//    @Field(displayName = "占位提示")
//    private String placeholder;

    /**
     * 说明提示
     */
    @XStreamAsAttribute
    @Field(displayName = "说明提示")
    private String hint;

    /**
     * 默认值
     */
    @XStreamAsAttribute
    @Field(displayName = "默认值")
    private String defaultValue;

    /**
     * 自动填充默认值
     */
    @XStreamAsAttribute
    @Field(displayName = "自动填充默认值")
    private Boolean autoFill;

    /**
     * 必填
     */
    @XStreamAsAttribute
    @Field(displayName = "必填")
    private String required;

    /**
     * 只读
     */
    @XStreamAsAttribute
    @Field(displayName = "只读")
    private String readonly;

    /**
     * 禁用
     */
    @XStreamAsAttribute
    @Field(displayName = "禁用")
    private String disable;

    /**
     * 计算属性
     */
    @XStreamAsAttribute
    @Field(displayName = "计算属性")
    private String compute;

    /**
     * 显示清除按钮
     */
    @XStreamAsAttribute
    @Field(displayName = "显示清除按钮")
    private Boolean showClear;

    /**
     * 多值
     */
    @XStreamAsAttribute
    @Field(displayName = "多值")
    private Boolean multi;

    /**
     * 存储
     */
    @XStreamAsAttribute
    @Field(displayName = "存储")
    private Boolean store;

    /**
     * 长度
     */
    @XStreamAsAttribute
    @Field(displayName = "长度")
    private String size;

    /**
     * 精度
     */
    @XStreamAsAttribute
    @Field(displayName = "精度")
    private String decimal;

    /**
     * 最小值
     */
    @XStreamAsAttribute
    @Field(displayName = "最小值")
    private String min;

    /**
     * 最大值
     */
    @XStreamAsAttribute
    @Field(displayName = "最大值")
    private String max;

    /**
     * 格式
     */
    @XStreamAsAttribute
    @Field(displayName = "格式")
    private String format;

    /**
     * 可选项
     */
    @Field(displayName = "可选项")
    private List<UdOption> options;

    @JSONField(serialize = false, deserialize = false)
    @XStreamImplicit
    @Field(displayName = "可选项")
    private List<UdOption> proxyOptions;

    public List<UdOption> getProxyOptions() {
        return null;
    }

    public UdField setProxyOptions(List<UdOption> proxyOptions) {
        this.setOptions(proxyOptions);
        return this;
    }

    /**
     * 存储关联关系
     */
    @XStreamAsAttribute
    @Field(displayName = "存储关联关系")
    private Boolean relationStore;

    /**
     * 关联模型
     */
    @XStreamAsAttribute
    @Field(displayName = "关联模型")
    private String references;

    /**
     * 中间模型
     */
    @XStreamAsAttribute
    @Field(displayName = "中间模型")
    private String through;

    /**
     * 关系字段
     */
    @XStreamAsAttribute
    @Field(displayName = "关系字段")
    private List<String> relationFields;

    /**
     * 关联字段
     */
    @XStreamAsAttribute
    @Field(displayName = "关联字段")
    private List<String> referenceFields;

    /**
     * 中间模型关系字段
     */
    @XStreamAsAttribute
    @Field(displayName = "中间模型关系字段")
    private List<String> throughRelationFields;

    /**
     * 中间模型关联字段
     */
    @XStreamAsAttribute
    @Field(displayName = "中间模型关联字段")
    private List<String> throughReferenceFields;

    /**
     * 数据映射
     */
    @XStreamAsAttribute
    @Field(displayName = "数据映射")
    private Map<String, Object> mapping;

    /**
     * 数据加载方式
     */
    @XStreamAsAttribute
    @Field(displayName = "数据加载方式")
    private QueryModeEnum queryMode;

    /**
     * 数据加载函数
     */
    @XStreamAsAttribute
    @Field(displayName = "数据加载函数")
    private String load;

    /**
     * 筛选条件
     */
    @XStreamAsAttribute
    @Field(displayName = "筛选条件")
    private String domain;

    /**
     * 数量限制
     */
    @XStreamAsAttribute
    @Field(displayName = "数量限制")
    private Integer limit;

    /**
     * 可选项数量限制
     */
    @XStreamAsAttribute
    @Field(displayName = "可选项数量限制")
    private Integer domainSize;

    /**
     * 可选项分页
     */
    @XStreamAsAttribute
    @Field(displayName = "可选项分页")
    private Long pageSize;

    /**
     * 排序
     */
    @XStreamAsAttribute
    @Field(displayName = "排序")
    private String ordering;

}
