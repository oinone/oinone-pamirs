package pro.shushi.pamirs.eip.api.serializable;

import jakarta.xml.soap.*;
import jakarta.xml.soap.Node;
import jakarta.xml.soap.Text;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipDeserialization;
import pro.shushi.pamirs.eip.api.IEipSerializable;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.xml.namespace.QName;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * DefaultSoapSerializable
 *
 * @author yakir on 2023/05/12 17:25.
 */
@Fun(EipFunctionConstant.FUNCTION_NAMESPACE)
@Component
@Slf4j
@SuppressWarnings({"unchecked"})
public class DefaultSoapSerializable implements IEipSerializable<SuperMap>, IEipDeserialization<SuperMap> {

    @Function.fun(EipFunctionConstant.DEFAULT_SOAP_SERIALIZABLE_FUN)
    @Function.Advanced(displayName = "默认SOAP序列化")
    @Function(name = EipFunctionConstant.DEFAULT_SOAP_SERIALIZABLE_FUN)
    @Override
    public SuperMap serializable(Object inObject) {

        SuperMap superMap = null;
        if (inObject instanceof InputStream) {
            try (BufferedInputStream bis = new BufferedInputStream((InputStream) inObject);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, len);
                }
                bytes = bos.toByteArray();
                String xml = new String(bytes, StandardCharsets.UTF_8);
                log.info("SOAP响应: {}", xml);
                xml = xml.replaceAll("\n+", "");
                xml = xml.replaceAll("\r+", "");
                xml = xml.replaceAll("\t+", "");
                bytes = xml.getBytes(StandardCharsets.UTF_8);
                try {
                    superMap = soap2Map(bytes, SOAPConstants.SOAP_1_2_PROTOCOL);
                } catch (SOAPException | IOException e0) {
                    log.error("1.2解析失败");
                    superMap = soap2Map(bytes, SOAPConstants.SOAP_1_1_PROTOCOL);
                }
            } catch (SOAPException | IOException ex) {
                log.error("SOAP反序列化异常", ex);
                superMap = new SuperMap();
            }
        } else {
            log.error("返回非InputStream");
            superMap = new SuperMap();
        }
        return superMap;
    }

    public SuperMap soap2Map(byte[] bytes, String version) throws SOAPException, IOException {

        MessageFactory factory = MessageFactory.newInstance(version);
        SOAPMessage message = factory.createMessage(null, new ByteArrayInputStream(bytes));
        message.saveChanges();
        SOAPHeader header = message.getSOAPHeader();
        SOAPBody body = message.getSOAPBody();
        SuperMap data = new SuperMap();
        children(data, body.getChildElements(), null);
        return data;
    }

    public void children(SuperMap data, Iterator<Node> elements, String parentName) {
        while (elements.hasNext()) {
            Node node = elements.next();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                SOAPElement element = (SOAPElement) node;
                String name = element.getNodeName();
                String qNamePrefix = Optional.ofNullable(element.getElementQName())
                        .map(QName::getPrefix)
                        .filter(StringUtils::isNotBlank)
                        .orElse(null);
                if (null != qNamePrefix) {
                    name = name.replaceAll(qNamePrefix + ":", "");
                }
                String value = element.getValue();
                Iterator<Node> children = element.getChildElements();

                if (null != children) {
                    SuperMap childMap = new SuperMap();
                    data.put(name, childMap);
                    children(childMap, children, name);
                } else {
                    data.put(name, value);
                }
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                Text element = (Text) node;
                String value = element.getValue();
                if (StringUtils.contains(value, "<") && StringUtils.contains(value, "</")) {
                    try {
                        Map<String, Object> dataMap = readXml(element.getValue());
                        data.putAll(dataMap);
                    } catch (Throwable e) {
                        log.error("解析内部XML失败");
                        data.put(parentName, value);
                    }
                } else {
                    data.put(parentName, value);
                }
            }
        }
    }

    private Map<String, Object> readXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(xml));
        return parseElement(document.getRootElement());
    }

    private Map<String, Object> parseElement(Element e) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        List<Element> list = e.elements();
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = list.get(i);
                List<Object> mapList = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(iter.elements())) {
                    Map<String, Object> m = parseElement(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList<>();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List<Object>) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList<>();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj instanceof List) {
                            mapList = (List<Object>) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else {
            map.put(e.getName(), e.getText());
        }
        return map;
    }

    @Function.fun(EipFunctionConstant.DEFAULT_SOAP_DESERIALIZATION_FUN)
    @Function.Advanced(displayName = "默认SOAP反序列化")
    @Function(name = EipFunctionConstant.DEFAULT_SOAP_DESERIALIZATION_FUN)
    @Override
    public Object deserialization(SuperMap outObject) {
        return outObject;
    }
}
