package pro.shushi.pamirs.core.common.dsl.model.view;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.boot.base.enmu.ActionTargetEnum;
import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.boot.base.enmu.QueryModeEnum;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.core.common.dsl.model.UdWidget;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.DataContainerTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 视图动作
 * <p>
 * 2021/7/15 2:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_ACTION)
@Model(displayName = "视图动作")
public class UdAction extends UdWidget {

    private static final long serialVersionUID = 3239034351313922943L;

    /**
     * 标题
     */
    @XStreamAsAttribute
    @Field(displayName = "标题")
    private String label;

    /**
     * 动作类型
     */
    @XStreamAsAttribute
    @Field(displayName = "动作类型")
    private ActionTypeEnum actionType;

    /**
     * 源模型
     */
    @XStreamAsAttribute
    @Field(displayName = "源模型")
    private String model;

    /**
     * 唯一标识
     */
    @XStreamAsAttribute
    @Field(displayName = "唯一标识")
    private String refs;

    /**
     * 标题
     */
    @XStreamAsAttribute
    @Field(displayName = "标题")
    private String text;

    /**
     * 槽
     */
    @XStreamAsAttribute
    @Field(displayName = "槽")
    private String tag;

    /**
     * 上下文类型
     */
    @XStreamAsAttribute
    @Field(displayName = "上下文类型")
    private ActionContextTypeEnum contextType;

    /**
     * 数据映射
     */
    @XStreamAsAttribute
    @Field(displayName = "数据映射")
    private Map<String, Object> mapping;

    /**
     * 禁用
     */
    @XStreamAsAttribute
    @Field(displayName = "禁用")
    private String disable;

    /**
     * 打开方式
     */
    @XStreamAsAttribute
    @Field(displayName = "打开方式")
    private ActionTargetEnum target;

    /**
     * 目标模型
     */
    @XStreamAsAttribute
    @Field(displayName = "目标模型")
    private String resModel;

    /**
     * 目标模块
     */
    @XStreamAsAttribute
    @Field(displayName = "目标模块")
    private String resModule;

    /**
     * 视图类型
     */
    @XStreamAsAttribute
    @Field(displayName = "视图类型")
    private ViewTypeEnum viewType;

    /**
     * 目标视图
     */
    @XStreamAsAttribute
    @Field(displayName = "目标视图")
    private String resViewName;

    /**
     * 视图数据类型
     */
    @XStreamAsAttribute
    @Field(displayName = "视图数据类型")
    private DataContainerTypeEnum dataType;

    /**
     * 母版
     */
    @XStreamAsAttribute
    @Field(displayName = "母版")
    private String mask;

    /**
     * 主题
     */
    @XStreamAsAttribute
    @Field(displayName = "主题")
    private String theme;

    /**
     * 查询方式
     */
    @XStreamAsAttribute
    @Field(displayName = "查询方式")
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
     * 查询数量限制
     */
    @XStreamAsAttribute
    @Field(displayName = "查询数量限制")
    private Integer limit;

    /**
     * 链接
     */
    @XStreamAsAttribute
    @Field(displayName = "链接")
    private String url;

    /**
     * 计算函数
     */
    @XStreamAsAttribute
    @Field(displayName = "计算函数")
    private String compute;

    /**
     * 函数编码
     * 服务器或客户端函数编码
     */
    @XStreamAsAttribute
    @Field(displayName = "函数编码")
    private String fun;

    /**
     * 数据函数定义
     * 窗口动作：数据加载函数定义
     * 链接动作：计算函数定义
     * 客户端动作：计算函数定义
     * 服务器动作：服务器函数定义
     */
    @XStreamAsAttribute
    @Field(displayName = "数据函数定义")
    private FunctionDefinition function;

    /**
     * 引用元数据
     */
    @XStreamAsAttribute
    @Field(displayName = "引用元数据")
    private Boolean refMeta;

    /**
     * 显示名称
     */
    @XStreamAsAttribute
    @Field(displayName = "显示名称")
    private String displayName;

    /**
     * 描述
     */
    @XStreamAsAttribute
    @Field(displayName = "描述")
    private String summary;

    /**
     * 过滤动作
     */
    @XStreamAsAttribute
    @Field(displayName = "过滤动作")
    private String rule;

    /**
     * 服务端筛选条件
     */
    @XStreamAsAttribute
    @Field(displayName = "服务端筛选条件")
    private String filter;

    /**
     * 可选视图类型
     */
    @XStreamAsAttribute
    @Field.Enum
    @Field(displayName = "可选视图类型", summary = "打开目标模型后，可支持切换的视图类型", multi = true)
    private List<ViewTypeEnum> optionViewTypes;

}
