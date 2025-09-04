package pro.shushi.pamirs.eip.api.constant;

import org.apache.camel.ExtendedExchange;
import org.apache.camel.processor.ErrorHandler;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.*;
import pro.shushi.pamirs.eip.api.builder.DefaultEipInterfaceBuilder;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.converter.DefaultInOutConverter;
import pro.shushi.pamirs.eip.api.entity.impl.*;
import pro.shushi.pamirs.eip.api.handler.DefaultIntegrationInterfaceErrorHandler;
import pro.shushi.pamirs.eip.api.handler.DefaultOpenInterfaceErrorHandler;
import pro.shushi.pamirs.eip.api.processor.AbstractEipIntegrationInterfaceProcessor;
import pro.shushi.pamirs.eip.api.processor.DefaultPagingExpression;
import pro.shushi.pamirs.eip.api.serializable.DefaultJSONSerializable;
import pro.shushi.pamirs.eip.api.util.EipHelper;

import java.util.function.BiFunction;

/**
 * Eip 函数常量
 *
 * @author Adamancy Zhang on 2021-02-05 10:19
 */
public class EipFunctionConstant {

    /**
     * EIP 命名空间常量
     */
    public static final String FUNCTION_NAMESPACE = "pamirs.eip.default.namespace";

    //region 前缀常量

    public static final String PARAM_PROCESSOR_CONVERTER_PREFIX = "EIP_PARAM_PROCESSOR_CONVERTER_";

    public static final String IN_OUT_CONVERTER_PREFIX = "EIP_IN_OUT_CONVERTER_";

    public static final String AUTHENTICATION_PROCESSOR_PREFIX = "EIP_AUTHENTICATION_PROCESSOR_";

    public static final String EXCEPTION_PREDICT_PREFIX = "EIP_EXCEPTION_PREDICT_";

    public static final String SERIALIZABLE_PREFIX = "EIP_SERIALIZABLE_";

    public static final String DESERIALIZATION_PREFIX = "EIP_DESERIALIZATION_";

    public static final String PAGING_PREDICT_PREFIX = "EIP_PAGING_PREDICT_";

    public static final String COMPUTER_PREFIX = "EIP_COMPUTE_";

    public static final String OPEN_REQUEST_DECRYPT_PREFIX = "EIP_OPEN_REQUEST_DECRYPT_PREFIX_";

    public static final String OPEN_RESPONSE_ENCRYPTION_PREFIX = "EIP_OPEN_RESPONSE_ENCRYPTION_PREFIX_";

    public static final String OPEN_ERROR_HANDLER_PREFIX = "EIP_OPEN_ERROR_HANDLER_PREFIX_";

    //endregion

    //region fun 常量

    public static final String DEFAULT_TOKEN_AUTHENTICATION_PROCESSOR_FUN = AUTHENTICATION_PROCESSOR_PREFIX + "defaultTokenAuthenticationProcessor";

    public static final String DEFAULT_APPLICATION_AUTHENTICATION_PROCESSOR_FUN = AUTHENTICATION_PROCESSOR_PREFIX + "defaultApplicationAuthenticationProcessor";

    public static final String DEFAULT_AUTHENTICATION_PROCESSOR_FUN = AUTHENTICATION_PROCESSOR_PREFIX + "defaultAuthenticationProcessor";

    public static final String DEFAULT_NO_ENCRYPT_AUTHENTICATION_PROCESSOR_FUN = AUTHENTICATION_PROCESSOR_PREFIX + "defaultNoEncryptAuthenticationProcessor";

    public static final String DEFAULT_MD5_SIGNATURE_AUTHENTICATION_PROCESSOR_FUN = AUTHENTICATION_PROCESSOR_PREFIX + "defaultMD5SignatureAuthenticationProcessor";

    public static final String DEFAULT_JSON_SERIALIZABLE_FUN = SERIALIZABLE_PREFIX + "defaultJSONSerializableConverter";

    public static final String DEFAULT_JSON_DESERIALIZATION_FUN = DESERIALIZATION_PREFIX + "defaultJSONDeserializationConverter";

    public static final String DEFAULT_SOAP_SERIALIZABLE_FUN = SERIALIZABLE_PREFIX + "defaultSOAPSerializableConverter";

    public static final String DEFAULT_SOAP_DESERIALIZATION_FUN = DESERIALIZATION_PREFIX + "defaultSOAPDeserializationConverter";

    public static final String DEFAULT_STRING_SERIALIZABLE_FUN = SERIALIZABLE_PREFIX + "defaultStringSerializableConverter";

    public static final String DEFAULT_STRING_DESERIALIZATION_FUN = DESERIALIZATION_PREFIX + "defaultStringDeserializationConverter";

    public static final String DEFAULT_IN_OUT_CONVERTER_FUN = IN_OUT_CONVERTER_PREFIX + "defaultInOutConverter";

    public static final String DEFAULT_FORM_URLENCODED_IN_OUT_CONVERTER_FUN = IN_OUT_CONVERTER_PREFIX + "defaultFormUrlencodedInOutConverter";

    public static final String DEFAULT_SOAP_IN_OUT_CONVERTER_FUN = IN_OUT_CONVERTER_PREFIX + "defaultSOAPInOutConverter";

    public static final String DEFAULT_MULTIPART_FORM_DATA_IN_OUT_CONVERTER_FUN = IN_OUT_CONVERTER_PREFIX + "defaultMultipartFormDataInOutConverter";

    public static final String DEFAULT_OPEN_API_GET_ACCESS_TOKEN_FUN = IN_OUT_CONVERTER_PREFIX + "defaultOpenApiGetAccessTokenConverter";

    public static final String DEFAULT_OPEN_API_CALL_INTEGRATION_API_FUN = IN_OUT_CONVERTER_PREFIX + "openApiCallIntegrationApiConverter";

    public static final String DEFAULT_Hl7_FHIR_DATA_IN_OUT_CONVERTER_FUN = IN_OUT_CONVERTER_PREFIX + "DefaultHl7FhirDataInOutConverter";

    public static final String DEFAULT_OPEN_Hl7_FHIR_JSON_PARAM_CONVERTER_FUN = IN_OUT_CONVERTER_PREFIX + "OpenApiHl7FhirJsonParamConvert";

    //endregion

    // 集成接口发送请求
    public static final String EIP_SEND_REQUEST_FUN = "sendRequest";

    //region 默认实例

    public static final IEipContextSupplier<SuperMap> DEFAULT_CONTEXT_SUPPLIER = DefaultEipContext::new;

    public static final IEipParamConverter<SuperMap> DEFAULT_PARAM_CONVERTER = new DefaultEipParamConverter<>();

    public static final DefaultJSONSerializable DEFAULT_JSON_SERIALIZABLE = new DefaultJSONSerializable();

    public static final IEipInOutConverter DEFAULT_IN_OUT_CONVERTER = new DefaultInOutConverter();

    public static final IEipPagingPredict<SuperMap> DEFAULT_PAGING_PREDICT = (context, exchange) -> true;

    public static final IEipExceptionPredict<SuperMap> DEFAULT_EXCEPTION_PREDICT = (context) -> false;

    public static final IEipParamProcessor<SuperMap> DEFAULT_REQUEST_PARAM_PROCESSOR = new DefaultEipRequestParamProcessor();

    public static final IEipParamProcessor<SuperMap> DEFAULT_RESPONSE_PARAM_PROCESSOR = new DefaultEipResponseParamProcessor();

    public static final IEipOpenParamProcessor<SuperMap> DEFAULT_OPEN_REQUEST_PARAM_PROCESSOR = new DefaultEipOpenParamProcessor();

    public static final IEipOpenParamProcessor<SuperMap> DEFAULT_OPEN_RESPONSE_PARAM_PROCESSOR = new DefaultEipOpenParamProcessor();

    public static final IEipExceptionParamProcessor<SuperMap> DEFAULT_EXCEPTION_PARAM_PROCESSOR = new DefaultEipExceptionParamProcessor();

    public static final IEipIncrementalParamConverter<SuperMap> DEFAULT_INCREMENTAL_PARAM_CONVERTER = new DefaultEipIncrementalParamConverter<>();

    public static final DefaultPagingExpression DEFAULT_PAGING_EXPRESSION = new DefaultPagingExpression();

    public static final ErrorHandler DEFAULT_INTEGRATION_INTERFACE_ERROR_HANDLER = new DefaultIntegrationInterfaceErrorHandler();

    public static final ErrorHandler DEFAULT_OPEN_INTERFACE_ERROR_HANDLER = new DefaultOpenInterfaceErrorHandler();

    //endregion

    public static final BiFunction<EipCamelContext, String, IEipIntegrationInterface<SuperMap>> EMPTY = (context, interfaceName) -> DefaultEipInterfaceBuilder.newInstance(context, interfaceName, EipHelper.generatorDirectUri(interfaceName))
            .createRequestParamProcessor()
            .setProcessor(eipInterface -> new AbstractEipIntegrationInterfaceProcessor<SuperMap>(eipInterface) {
                @Override
                public void processor(ExtendedExchange exchange) throws Exception {
                }
            })
            .and()
            .createResponseParamProcessor()
            .setProcessor(eipInterface -> new AbstractEipIntegrationInterfaceProcessor<SuperMap>(eipInterface) {
                @Override
                public void processor(ExtendedExchange exchange) throws Exception {
                }
            })
            .and()
            .createExceptionParamProcessor()
            .setProcessor(eipInterface -> new AbstractEipIntegrationInterfaceProcessor<SuperMap>(eipInterface) {
                @Override
                public void processor(ExtendedExchange exchange) throws Exception {
                }
            })
            .and()
            .build();
}
