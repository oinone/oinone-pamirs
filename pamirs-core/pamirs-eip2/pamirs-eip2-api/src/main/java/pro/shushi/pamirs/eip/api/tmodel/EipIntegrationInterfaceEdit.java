package pro.shushi.pamirs.eip.api.tmodel;

import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipProtocolTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.HttpMethodEnum;
import pro.shushi.pamirs.eip.api.model.EipConvertParam;
import pro.shushi.pamirs.eip.api.model.EipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.model.EipLib;
import pro.shushi.pamirs.eip.api.model.EipParamProcessor;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.List;

@Model.model(EipIntegrationInterfaceEdit.MODEL_MODEL)
@Model(displayName = "集成接口创建")
public class EipIntegrationInterfaceEdit extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.eip.tmodel.EipIntegrationInterfaceEdit";

    @Field.Integer
    @Field(displayName = "接口id")
    private Long id;

    @Field.many2one
    @Field(displayName = "集成库")
    private EipLib lib;

    @Field.String
    @Field(displayName = "接口技术名称", required = true)
    private String interfaceName;

    @Field.String
    @Field(displayName = "接口名称", required = true)
    private String name;

    @Field.String
    @Field(displayName = "接口路由路径", required = true)
    private String uri;

    @Field.Boolean
    @Field(displayName = "是否启用日志")
    private Boolean isEnabledLog;

    @Field.Enum
    @Field(displayName = "数据状态")
    private DataStatusEnum dataStatus;

    @Field.Enum
    @Field(displayName = "请求响应协议", required = true, summary = "请求响应协议")
    private EipProtocolTypeEnum protocolTypeEnum;

    @Field.Enum
    @Field(displayName = "http请求方法", required = true, summary = "http请求方法")
    private HttpMethodEnum httpMethodEnum;

    @Field.many2one
    @Field(displayName = "所属模块")
    private ModuleDefinition moduleDefinition;

    //上下文信息
    @Field.many2one
    @Field(displayName = "上下文提供者函数")
    private FunctionDefinition contextSupplierFunction;

    @Field.one2many
    @Field(displayName = "上下文列表")
    private List<EipContextVariable> contextVariableList;

    //请求处理器信息
    @Field.many2one
    @Field(displayName = "请求认证处理器函数")
    private FunctionDefinition reqAuthenticationProcessorFunction;

    @Field.many2one
    @Field(displayName = "请求参数转换函数")
    private FunctionDefinition paramConverterFunction;

    @Field.many2one
    @Field(displayName = "自定义转换器函数")
    private FunctionDefinition converterFunction;

    @Field.many2one
    @Field(displayName = "请求输入输出转换函数")
    private FunctionDefinition reqInOutConverterFunction;

    @Field.one2many
    @Field(displayName = "请求转换参数列表")
    private List<EipConvertParam> reqConvertParamList;

    @Field.one2many
    @Field(displayName = "请求路径参数列表")
    private List<EipConvertParam> uriConvertParamList;

    @Field.one2many
    @Field(displayName = "请求头参数列表")
    private List<EipConvertParam> headerConvertParamList;

    @Field.String
    @Field(displayName = "请求最终结果键值")
    private String reqFinalResultKey;

    //响应处理器信息
    @Field.many2one
    @Field(displayName = "响应输入输出转换函数")
    private FunctionDefinition respInOutConverterFunction;

    @Field.one2many
    @Field(displayName = "响应转换参数列表")
    private List<EipConvertParam> respConvertParamList;

    @Field.String
    @Field(displayName = "响应最终结果键值")
    private String respFinalResultKey;

    //异常处理器信息
    @Field.many2one
    @Field(displayName = "异常判定函数")
    private FunctionDefinition exceptionPredictFunction;

    @Field.one2many
    @Field(displayName = "异常信息转换参数列表")
    private List<EipConvertParam> expConvertParamList;

    @Field.many2one
    @Field(displayName = "请求参数处理器")
    private EipParamProcessor requestParamProcessor;

    @Field.many2one
    @Field(displayName = "响应参数处理器")
    private EipParamProcessor responseParamProcessor;

    @Field.many2one
    @Field(displayName = "异常参数处理器")
    private EipExceptionParamProcessor exceptionParamProcessor;


}
