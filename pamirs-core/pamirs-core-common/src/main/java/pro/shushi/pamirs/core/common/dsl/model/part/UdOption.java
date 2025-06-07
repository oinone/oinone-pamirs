package pro.shushi.pamirs.core.common.dsl.model.part;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import pro.shushi.pamirs.core.common.dsl.constants.DslConstants;
import pro.shushi.pamirs.core.common.dsl.model.UdWidget;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 可选项
 * <p>
 * 2021/7/15 12:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@XStreamAlias(DslConstants.NODE_OPTION)
@Model(displayName = "可选项")
public class UdOption extends UdWidget {

    private static final long serialVersionUID = -909336290942235797L;

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
     * 关联模型类型
     */
    @XStreamAsAttribute
    @Field(displayName = "关联模型类型")
    private ModelTypeEnum referencesType;

}
