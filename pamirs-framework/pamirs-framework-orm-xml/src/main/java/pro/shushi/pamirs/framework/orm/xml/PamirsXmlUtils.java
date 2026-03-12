package pro.shushi.pamirs.framework.orm.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.framework.orm.xml.converter.ModelObjectXmlConverter;
import pro.shushi.pamirs.framework.orm.xml.feature.PamirsXmlParserFeature;
import pro.shushi.pamirs.framework.orm.xml.mapper.IEnumMapper;
import pro.shushi.pamirs.framework.orm.xml.provider.PamirsReflectionProvider;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * XML转换工具
 * <p>
 * 2022/3/15 7:31 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class PamirsXmlUtils {

    public volatile static XStream xStream;

    public static ConcurrentHashMap<String, XStream> xStreamMap = new ConcurrentHashMap<>();

    private PamirsXmlUtils() {
    }

    public static void register(String key, Class<?>[] annotationClasses, PamirsXmlParserFeature... features) {
        XStream xStream = getInstance(key, features);
        xStream.processAnnotations(annotationClasses);
    }

    public static XStream getInstance(String key, PamirsXmlParserFeature... features) {
        XStream xStream = xStreamMap.get(key);
        if (null == xStream) {
            synchronized (XStream.class) {
                xStream = xStreamMap.get(key);
                if (null == xStream) {
                    xStream = new XStream();
                    XStream.setupDefaultSecurity(xStream);
                    xStreamMap.put(key, xStream);
                }

                configXStream(xStream, features);
            }
        }
        return xStream;
    }

    public static XStream getInstance(PamirsXmlParserFeature... features) {
        if (null == xStream) {
            synchronized (XStream.class) {
                if (null == xStream) {
                    xStream = new XStream();
                    XStream.setupDefaultSecurity(xStream);
                }

                configXStream(xStream, features);
            }
        }
        return xStream;
    }

    private static void configXStream(XStream xStream, PamirsXmlParserFeature... features) {
        int intFeatures = PamirsXmlParserFeature.of(features);
        boolean caseSensitive = PamirsXmlParserFeature.isEnabled(intFeatures, PamirsXmlParserFeature.ParseEnumCaseSensitive);
        boolean fillTagToObject = PamirsXmlParserFeature.isEnabled(intFeatures, PamirsXmlParserFeature.FillTagToObject);

        xStream.autodetectAnnotations(true);
        xStream.registerConverter(new ModelObjectXmlConverter(new IEnumMapper(xStream.getMapper(), caseSensitive),
                new PamirsReflectionProvider(), fillTagToObject));
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.aliasSystemAttribute(null, "class");
        xStream.aliasSystemAttribute(null, "defined-in");
        xStream.addPermission(AnyTypePermission.ANY);
    }

    public static Object fromXML(String xml, PamirsXmlParserFeature... features) {
        return fromXML(null, xml, features);
    }

    public static String toXML(Object obj, PamirsXmlParserFeature... features) {
        return toXML(null, obj, features);
    }

    public static Object fromXML(String key, String xml, PamirsXmlParserFeature... features) {
        try {
            if (null == key) {
                return getInstance(features).fromXML(xml);
            }
            return getInstance(key, features).fromXML(xml);
        } catch (Exception e) {
            log.error("template:" + xml);
            throw PamirsException.construct(OrmExpEnumerate.BASE_OBJ_FROM_XML_ERROR, e)
                    .errThrow();
        }
    }

    public static String toXML(String key, Object obj, PamirsXmlParserFeature... features) {
        try {
            if (null == key) {
                return getInstance(features).toXML(obj);
            }
            return getInstance(key, features).toXML(obj);
        } catch (Exception e) {
            try {
                log.error("object: {}", JsonUtils.toJSONString(obj));
            } catch (Throwable ee) {
                log.error("object to json string error.", ee);
            }
            throw PamirsException.construct(OrmExpEnumerate.BASE_OBJ_TO_XML_ERROR, e).errThrow();
        }
    }

}
