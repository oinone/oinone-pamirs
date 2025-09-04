package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

/**
 * 单个接口的抽象基类
 *
 * @author Adamancy Zhang at 19:16 on 2021-06-09
 */
@Base
@Model.model(AbstractSingleInterface.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, unique = {"interfaceName"})
@Model(displayName = "单个接口的抽象基类", labelFields = "name")
public abstract class AbstractSingleInterface extends AbstractEipApi {

    private static final long serialVersionUID = 6243255977673132057L;

    public static final String MODEL_MODEL = "pamirs.eip.AbstractSingleInterface";

    @Base
    @Field.String(size = 2048)
    @Field(displayName = "接口路由", required = true, summary = "Camel内置路由规则")
    private String uri;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"contextSupplierNamespace", "contextSupplierFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "上下文提供者函数")
    private FunctionDefinition contextSupplierFunction;

    @Base
    @Field.String
    @Field(displayName = "上下文提供者方法命名空间", required = true)
    private String contextSupplierNamespace;

    @Base
    @Field.String
    @Field(displayName = "上下文提供者方法名称", required = true)
    private String contextSupplierFun;

    @Base
    @Field.Boolean
    @Field(displayName = "是否是动态集成接口", defaultValue = "false", required = true)
    private Boolean isDynamic;

    @Base
    @Field.Integer
    @Field(displayName = "动态协议缓存的最大值", defaultValue = "2", required = true)
    private Integer dynamicProtocolCacheSize;

    @Field.Integer
    @Field(displayName = "调用次数", store = NullableBoolEnum.FALSE)
    private Long callCount;

    @Field.Integer
    @Field(displayName = "成功调用次数", store = NullableBoolEnum.FALSE)
    private Long successCallCount;

    @Field.Integer
    @Field(displayName = "失败调用次数", store = NullableBoolEnum.FALSE)
    private Long failCallCount;

    @Field.Integer
    @Field(displayName = "小于100ms调用次数", store = NullableBoolEnum.FALSE)
    private Long ultraFastCall;

    @Field.Integer
    @Field(displayName = "100-300ms调用数量", store = NullableBoolEnum.FALSE)
    private Long veryFastCall;

    @Field.Integer
    @Field(displayName = "300-500ms调用数量", store = NullableBoolEnum.FALSE)
    private Long fastCall;

    @Field.Integer
    @Field(displayName = "500-1000ms调用数量", store = NullableBoolEnum.FALSE)
    private Long moderateCall;

    @Field.Integer
    @Field(displayName = "1s-3s调用数量", store = NullableBoolEnum.FALSE)
    private Long slowCall;

    @Field.Integer
    @Field(displayName = "3s-8s调用数量", store = NullableBoolEnum.FALSE)
    private Long verySlowCall;

    @Field.Integer
    @Field(displayName = "8s-30s调用数量", store = NullableBoolEnum.FALSE)
    private Long slowestCall;

    @Field.Integer
    @Field(displayName = "大于30s调用数量", store = NullableBoolEnum.FALSE)
    private Long timeoutCall;

}
