package pro.shushi.pamirs.eip.api.serializable;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipDeserialization;
import pro.shushi.pamirs.eip.api.IEipSerializable;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

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

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(">(\\s+)<");

    @Autowired
    private DefaultJSONSerializable defaultJSONSerializable;

    @Function.fun(EipFunctionConstant.DEFAULT_SOAP_SERIALIZABLE_FUN)
    @Function.Advanced(displayName = "默认SOAP序列化")
    @Function(name = EipFunctionConstant.DEFAULT_SOAP_SERIALIZABLE_FUN)
    @Override
    public SuperMap serializable(Object inObject) {
        if (inObject == null) {
            return new SuperMap();
        }
        SuperMap result;
        if (inObject instanceof SuperMap) {
            result = (SuperMap) inObject;
        } else if (inObject instanceof Map) {
            result = new SuperMap((Map<String, Object>) inObject);
        } else if (inObject instanceof InputStream) {
            String xml = inputStreamToString((InputStream) inObject);
            result = stringToMap(xml);
        } else if (inObject instanceof String) {
            result = stringToMap((String) inObject);
        } else {
            log.error("无法识别的SOAP入参类型");
            result = new SuperMap();
        }
        return result;
    }

    private String inputStreamToString(InputStream inObject) {
        try (BufferedInputStream bis = new BufferedInputStream(inObject);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[2048];
            int len;
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            bytes = bos.toByteArray();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取", e);
        }
        return null;
    }

    private SuperMap stringToMap(String content) {
        if (StringUtils.isNotBlank(content)) {
            if (JSON.isValid(content)) {
                return defaultJSONSerializable.serializable(content);
            }
            return soapXML2Map(content);
        }
        return new SuperMap();
    }

    public SuperMap soapXML2Map(String xml) {
        log.debug("SOAP响应: {}", xml);
        xml = WHITESPACE_PATTERN.matcher(xml).replaceAll("><");
        byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
        try {
            return soap2Map(bytes, SOAPConstants.SOAP_1_2_PROTOCOL);
        } catch (SOAPException | IOException e) {
            log.debug("SOAP 1.2 反序列化异常", e);
            try {
                return soap2Map(bytes, SOAPConstants.SOAP_1_1_PROTOCOL);
            } catch (SOAPException | IOException ee) {
                log.error("SOAP 1.1 反序列化异常", ee);
                return new SuperMap();
            }
        }
    }

    public SuperMap soap2Map(byte[] bytes, String version) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance(version);
        SOAPMessage message = factory.createMessage(null, new ByteArrayInputStream(bytes));
        message.saveChanges();
        SOAPBody body = message.getSOAPBody();
        return (SuperMap) resolveXMLObject(body.getChildElements());
    }

    public static Object resolveXMLObject(Iterator<Node> elements) {
        SuperMap superMap = new SuperMap();
        while (elements.hasNext()) {
            Node node = elements.next();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                SOAPElement element = (SOAPElement) node;
                Iterator<Node> children = element.getChildElements();
                String fieldName = getFieldName(element);
                if (children != null && children.hasNext()) {
                    Object value = resolveXMLObject(children);
                    Object exist = superMap.get(fieldName);
                    if (exist == null) {
                        superMap.put(fieldName, value);
                    } else if (exist instanceof Collection) {
                        ((Collection<Object>) exist).add(value);
                    } else {
                        superMap.put(fieldName, Lists.newArrayList(exist, value));
                    }
                } else {
                    superMap.put(fieldName, null);
                }
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                if (elements.hasNext()) {
                    continue;
                }
                Text element = (Text) node;
                String value = element.getValue();
                if (StringUtils.contains(value, "<") && StringUtils.contains(value, "</")) {
                    try {
                        return readXml(element.getValue());
                    } catch (Throwable e) {
                        log.error("解析内部XML失败");
                    }
                }
                return value;
            }
        }
        return superMap;
    }

    private static String getFieldName(SOAPElement element) {
        String name = element.getNodeName();
        String qNamePrefix = Optional.ofNullable(element.getElementQName())
                .map(QName::getPrefix)
                .filter(StringUtils::isNotBlank)
                .orElse(null);
        if (null != qNamePrefix) {
            name = name.replaceAll(qNamePrefix + ":", "");
        }
        return name;
    }

    private static Map<String, Object> readXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(xml));
        return parseElement(document.getRootElement());
    }

    private static Map<String, Object> parseElement(Element e) {
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
