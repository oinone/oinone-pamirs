package pro.shushi.pamirs.eip.api.converter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.IEipParamConverterCallback;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.connector.ApiContentType;
import pro.shushi.pamirs.eip.api.entity.impl.DefaultEipParamConverter;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;

/**
 * @author yeshenyue on 2025/4/18 17:17.
 */
@Slf4j
@Component
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class OpenApiHl7FhirJsonParamConvert<T> extends DefaultEipParamConverter<T> {

    @Function.fun(EipFunctionConstant.DEFAULT_OPEN_Hl7_FHIR_JSON_PARAM_CONVERTER_FUN)
    @Function.Advanced(displayName = "开放接口HL7_FHIR_JSON服务")
    @Function(name = EipFunctionConstant.DEFAULT_OPEN_Hl7_FHIR_JSON_PARAM_CONVERTER_FUN)
    public void convertFunction(IEipContext<T> context, List<IEipConvertParam<T>> convertParamList, IEipParamConverterCallback<T> callback) {
        final String contentTypeKey = IEipContext.HEADER_PARAMS_KEY + "." + HttpHeaders.CONTENT_TYPE;
        Object contentTypeValue = context.getExecutorContextValue(contentTypeKey);
        if (contentTypeValue == null || !ApiContentType.APPLICATION_JSON_FHIR.value().equalsIgnoreCase(contentTypeValue.toString())) {
            throw PamirsException.construct(EipExpEnumerate.EIP_CONTENT_TYPE_ERROR).errThrow();
        }
        convert(context, convertParamList, callback);
    }
}
