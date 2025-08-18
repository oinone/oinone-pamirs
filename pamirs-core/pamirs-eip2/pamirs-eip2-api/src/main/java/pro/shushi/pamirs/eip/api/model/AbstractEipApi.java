package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.business.api.model.BizIdModel;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.EipProtocolTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.ExchangePatternEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 集成Api的抽象基类
 *
 * @author Adamancy Zhang at 19:16 on 2021-06-09
 */
@Base
@Model.model(AbstractEipApi.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, unique = {"interfaceName"})
@Model(displayName = "集成Api的抽象基类", labelFields = "name")
public abstract class AbstractEipApi extends BizIdModel implements IEipApi, IDataStatus {

    private static final long serialVersionUID = -5433361326731101929L;

    public static final String MODEL_MODEL = "pamirs.eip.AbstractEipApi";

    public static final String DEFAULT_LIB_CODE = "unclassified";

    @Field.many2one
    @Field.Relation(relationFields = {"module"}, referenceFields = {"module"})
    @Field(displayName = "模块")
    private ModuleDefinition moduleDefinition;

    @Base
    @Field.String
    @Field(displayName = "模块编码", required = true)
    private String module;

    @Base
    @Field.String
    @Field(displayName = "接口名称", required = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "接口技术名称", required = true)
    private String interfaceName;

    @Base
    @Field.Text
    @Field(displayName = "接口描述", required = true)
    private String description;

    @Base
    @Field.Enum
    @Field(displayName = "交换模式", defaultValue = "InOut", required = true, summary = "Camel内置交换模式")
    private ExchangePatternEnum exchangePattern;

    @Base
    @Field.Boolean
    @Field(displayName = "是否启用日志", defaultValue = "true")
    private Boolean isEnabledLog;

    @Base
    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED", required = true)
    private DataStatusEnum dataStatus;

    @Base
    @Field.Boolean
    @Field(displayName = "是否被管理", defaultValue = "true", required = true)
    private Boolean isDBManaged;

    /**
     * eip接口定义不关心协议，uri的结构化处理要在view中进行识别和处理
     *
     * @deprecated 2.3.0
     */
    @Deprecated
    @Base
    @Field.Enum
    @Field(displayName = "请求响应协议", defaultValue = "http", summary = "请求响应协议")
    private EipProtocolTypeEnum protocolTypeEnum;

    @Deprecated
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"libCode"}, referenceFields = {"code"})
    @Field(displayName = "集成库")
    private EipLib lib;

    @Deprecated
    @Base
    @Field.String
    @Field(displayName = "集成库编码", defaultValue = DEFAULT_LIB_CODE, invisible = true)
    private String libCode;

    @Deprecated
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"connGroupCode"}, referenceFields = {"code"})
    @Field(displayName = "业务分组")
    private EipConnGroup connGroup;

    @Deprecated
    @Base
    @Field.String
    @Field(displayName = "业务分组编码")
    private String connGroupCode;

    @JSONField(serialize = false)
    private transient EipCamelContext context;

    @Field.Boolean
    @Field(displayName = "是否忽略日志记录频率限制", store = NullableBoolEnum.FALSE)
    private Boolean isIgnoreLogFrequency;

    @Override
    public EipCamelContext getContext() {
        return context;
    }

    public void setContext(EipCamelContext context) {
        if (this.context == null) {
            this.context = context;
        }
    }

    @Override
    public String getCategory() {
        return null;
    }
}
