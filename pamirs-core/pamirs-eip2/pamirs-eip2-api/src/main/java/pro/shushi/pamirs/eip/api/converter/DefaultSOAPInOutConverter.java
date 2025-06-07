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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.eip.api.constant.WebServicePrefix.*;

/**
 * DefaultSOAPInOutConverter
 *
 * @author yakir on 2023/05/06 17:07.
 */
@SuppressWarnings({"unchecked"})
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Slf4j
public class DefaultSOAPInOutConverter implements IEipInOutConverter {

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

        SuperMap body = Optional.of(inObject)
                .map(_in -> (SuperMap) _in)
                .map(_in -> _in.get(PAMIRS_WEBSERVICE_PREFIX))
                .map(_body -> (SuperMap) _body)
                .orElse(new SuperMap());

        SOAPBodyElement payload = null;
        if (isV2) {
            payload = soapMessage.getSOAPBody().addBodyElement(new QName(wsNs, wsOp));
        } else {
            payload = soapMessage.getSOAPBody().addBodyElement(new QName(wsNs, wsOp, "ns"));
        }

        for (Map.Entry<String, ?> entry : body.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            soapElement(payload, key, value);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            soapMessage.writeTo(baos);
            byte[] bytes = baos.toByteArray();
            String msg = new String(bytes, StandardCharsets.UTF_8);
            log.info("SOAP Message: {}", msg);
            return msg;
        }
    }

    private static void soapElement(SOAPElement element, String key, Object value) throws SOAPException {
        if (key.contains(".")) {
            String[] arr = key.split("\\.", 2);
            Iterator<SOAPElement> iterator = element.getChildElements();
            Map<String, SOAPElement> childs = new HashMap<>();
            while (iterator.hasNext()) {
                SOAPElement e = iterator.next();
                childs.put(e.getLocalName(), e);
            }
            boolean flag = false;
            for (Map.Entry<String, SOAPElement> entry : childs.entrySet()) {
                if (entry.getKey().equals(arr[0])) {
                    flag = true;
                    soapElement(entry.getValue(), arr[1], value);
                }
            }
            if (!flag) {
                SOAPElement soapElement = element.addChildElement(arr[0]);
                soapElement(soapElement, arr[1], value);
            }
        } else {
            element.addChildElement(key).addTextNode(String.valueOf(value));
        }
    }

}
