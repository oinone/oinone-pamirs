package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContextSupplier;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.model.strategy.EipCircuitBreakerRule;
import pro.shushi.pamirs.eip.api.pamirs.DefaultContextSupplierFunction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * 集成接口
 *
 * @author Adamancy Zhang at 19:18 on 2021-06-09
 */
@Base
@Model.model(EipIntegrationInterface.MODEL_MODEL)
@Model.Advanced(unique = {"interfaceName"})
@Model(displayName = "集成接口", labelFields = "name")
public class EipIntegrationInterface extends AbstractSingleInterface implements IEipIntegrationInterface<SuperMap> {

    private static final long serialVersionUID = 8170110896085537907L;

    public static final String MODEL_MODEL = "pamirs.eip.EipIntegrationInterface";

    @Field(displayName = "集成应用Id")
    @Field.Integer
    private Long integrateId;

    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "请求参数处理器", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipParamProcessor requestParamProcessor;

    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "响应参数处理器", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipParamProcessor responseParamProcessor;

    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "异常参数处理器", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipExceptionParamProcessor exceptionParamProcessor;

    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "分页器", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipPaging paging;

    @Base
    @Field.Integer
    @Field(displayName = "排序索引", store = NullableBoolEnum.FALSE)
    private Integer orderNumber;

    @Base
    @Field.many2one
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "增量处理器", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private EipIncrementalProcessor incrementalProcessor;

    @Base
    @Field.one2many
    @Field.Advanced(columnDefinition = "text")
    @Field.Relation(store = false)
    @Field(displayName = "增量条件", store = NullableBoolEnum.TRUE, summary = Field.serialize.JSON)
    private List<EipIncrementalParam> incrementalList;

    @Field.many2one
    @Field.Relation(relationFields = {"integrateId"}, referenceFields = {"id"})
    @Field(displayName = "集成应用")
    private EipIntegrate eipIntegrate;

    @Field.String
    @Field(displayName = "熔断器规则编码")
    private String circuitBreakerRuleCode;

    @Field.many2one
    @Field.Relation(relationFields = {"circuitBreakerRuleCode"}, referenceFields = {"code"})
    @Field(displayName = "熔断器规则")
    private EipCircuitBreakerRule circuitBreakerRule;

    @JSONField(serialize = false)
    @Override
    public IEipContextSupplier<SuperMap> getContextSupplier() {
        String namespace = getContextSupplierNamespace();
        String fun = getContextSupplierFun();
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(fun)) {
            return new DefaultContextSupplierFunction<>(namespace, fun);
        }
        return EipFunctionConstant.DEFAULT_CONTEXT_SUPPLIER;
    }

    @Override
    public IEipIntegrationInterface<SuperMap> afterProperty() {
        return this;
    }
}
