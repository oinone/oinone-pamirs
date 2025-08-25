package pro.shushi.pamirs.eip.api.converter;

import org.apache.camel.ExtendedExchange;
import org.apache.camel.Message;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.eip.api.constant.WebServicePrefix.*;

/**
 * DefaultSOAPInOutConverter
 *
 * @author yakir on 2023/05/06 17:07.
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Slf4j
public class DefaultSOAPInOutConverter implements IEipInOutConverter {

    private static final String NS = "ns";

    @Function.fun(EipFunctionConstant.DEFAULT_SOAP_IN_OUT_CONVERTER_FUN)
    @Function.Advanced(displayName = "默认SOAP输入输出转换器")
    @Function(name = EipFunctionConstant.DEFAULT_SOAP_IN_OUT_CONVERTER_FUN)
    @Override
    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {

        if (null == inObject) {
            return inObject;
        }

        boolean isMap = inObject instanceof Map;
        if (!isMap) {
            return null;
        }

        Message message = exchange.getMessage();
        Object messageBody = message.getBody();
        SuperMap headers = Optional.of(messageBody)
                .map(_in -> (SuperMap) _in)
                .map(_in -> _in.get("http"))
                .map(_in -> (SuperMap) _in)
                .map(_in -> _in.get("headers"))
                .map(_in -> (SuperMap) _in)
                .orElse(new SuperMap());

        String contentType = Optional.ofNullable(headers.get("Content-Type"))
                .map(String::valueOf)
                .orElse(null);
        if (null == contentType) {
            throw PamirsException.construct(EipExpEnumerate.SYSTEM_ERROR)
                    .errThrow();
        }
        SOAPMessage soapMessage = null;
        boolean isV2 = false;
        if (contentType.contains("text/xml")) {
            soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
        } else if (contentType.contains("application/soap+xml")) {
            isV2 = true;
            soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        } else {
            soapMessage = MessageFactory.newInstance(SOAPConstants.DEFAULT_SOAP_PROTOCOL).createMessage();
        }
        soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");

        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("soap12", "http://schemas.xmlsoap.org/soap/envelope");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");

        String wsNs = Optional.of(messageBody)
                .map(_in -> (SuperMap) _in)
                .map(_in -> _in.get(PAMIRS_WEBSERVICE_NS))
                .map(String::valueOf)
                .orElse(null);
        String wsOp = Optional.of(messageBody)
                .map(_in -> (SuperMap) _in)
                .map(_in -> _in.get(PAMIRS_WEBSERVICE_OP))
                .map(String::valueOf)
                .orElse("");

        Object requestBody = Optional.of(inObject)
                .map(_in -> (SuperMap) _in)
                .map(_in -> _in.get(PAMIRS_WEBSERVICE_PREFIX))
                .orElse(new SuperMap());

        soapElement(soapMessage.getSOAPBody(), isV2, wsNs, wsOp, null, requestBody);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            soapMessage.writeTo(baos);
            byte[] bytes = baos.toByteArray();
            String msg = new String(bytes, StandardCharsets.UTF_8);
            log.info("SOAP Message: {}", msg);
            return msg;
        }
    }

    private static SOAPBodyElement createPayload(SOAPBody soapBody, boolean isV2, String wsNs, String wsOp) throws SOAPException {
        if (isV2) {
            return soapBody.addBodyElement(new QName(wsNs, wsOp));
        } else {
            return soapBody.addBodyElement(new QName(wsNs, wsOp, NS));
        }
    }

    @SuppressWarnings("unchecked")
    public static void soapElement(SOAPBody soapBody, boolean isV2, String wsNs, String wsOp, SOAPBodyElement payload, Object requestBody) throws SOAPException {
        if (requestBody instanceof Map) {
            if (payload == null) {
                payload = createPayload(soapBody, isV2, wsNs, wsOp);
            }
            Map<String, ?> body = (Map<String, ?>) requestBody;
            for (Map.Entry<String, ?> entry : body.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                soapElement(payload, key, value);
            }
        } else if (requestBody instanceof Collection) {
            if (payload != null) {
                throw new UnsupportedOperationException("无效的二维数组");
            }
            Collection<?> body = (Collection<?>) requestBody;
            for (Object item : body) {
                payload = createPayload(soapBody, isV2, wsNs, wsOp);
                soapElement(soapBody, isV2, wsNs, wsOp, payload, item);
            }
        } else {
            throw new UnsupportedOperationException("无效的数据类型");
        }
    }

    public static void soapElement(SOAPElement parent, String key, Object value) throws SOAPException {
        if (value instanceof Map) {
            // 嵌套 Map，递归处理
            SOAPElement element = parent.addChildElement(key);
            Map<?, ?> map = (Map<?, ?>) value;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                soapElement(element, entry.getKey().toString(), entry.getValue());
            }
        } else if (value instanceof Collection) {
            // Collection：每个元素都复用 key 作为 element 名
            Collection<?> collection = (Collection<?>) value;
            for (Object item : collection) {
                soapElement(parent, key, item);  // 注意复用当前 key
            }
        } else {
            // 基础类型或字符串
            SOAPElement element = parent.addChildElement(key);
            element.addTextNode(value == null ? "" : value.toString());
        }
    }
}
