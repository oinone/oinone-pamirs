package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.framework.orm.xml.PamirsXmlUtils;
import pro.shushi.pamirs.framework.orm.xml.feature.PamirsXmlParserFeature;
import pro.shushi.pamirs.meta.util.ClassUtils;

import java.util.Set;

/**
 * 视图XML工具
 * <p>
 * 2022/4/25 1:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ViewXmlUtils {

    public final static String X_STREAM_TYPE = "view-xml";

    static {
        Set<Class<?>> annotationClassSet = ClassUtils.getClasses(UIView.class.getPackage().getName());
        if (!CollectionUtils.isEmpty(annotationClassSet)) {
            Class<?>[] annotationClasses = annotationClassSet.toArray(new Class[0]);
            PamirsXmlUtils.register(X_STREAM_TYPE, annotationClasses, PamirsXmlParserFeature.FillTagToObject);
        }
    }

    public static Object fromXML(String xml) {
        return PamirsXmlUtils.fromXML(X_STREAM_TYPE, xml);
    }

    public static String toXML(Object obj) {
        return PamirsXmlUtils.toXML(X_STREAM_TYPE, obj);
    }

}
