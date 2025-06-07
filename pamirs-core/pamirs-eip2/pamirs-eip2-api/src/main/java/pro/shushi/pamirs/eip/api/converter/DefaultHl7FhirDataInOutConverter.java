package pro.shushi.pamirs.eip.api.converter;

import org.apache.camel.ExtendedExchange;
import org.springframework.http.HttpHeaders;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.enmu.connector.ApiContentType;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * Hl7-FHIR
 *
 * @author yeshenyue on 2024/4/12 18:30
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
public class DefaultHl7FhirDataInOutConverter extends DefaultInOutConverter implements IEipInOutConverter {


    @Function.fun(EipFunctionConstant.DEFAULT_Hl7_FHIR_DATA_IN_OUT_CONVERTER_FUN)
    @Function.Advanced(displayName = "默认Hl7-FHIR-JSON输入输出转换器")
    @Function(name = EipFunctionConstant.DEFAULT_Hl7_FHIR_DATA_IN_OUT_CONVERTER_FUN)
    @Override
    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
        Object outObject = super.exchangeObject(exchange, inObject);
        exchange.getIn().setHeader(HttpHeaders.CONTENT_TYPE, ApiContentType.APPLICATION_JSON_FHIR.value());
        exchange.getIn().setHeader(HttpHeaders.ACCEPT, ApiContentType.APPLICATION_JSON_FHIR.value());
        return outObject;
    }
}
