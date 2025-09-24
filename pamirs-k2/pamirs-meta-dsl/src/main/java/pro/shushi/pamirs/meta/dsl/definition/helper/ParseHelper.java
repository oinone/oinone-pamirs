package pro.shushi.pamirs.meta.dsl.definition.helper;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.dsl.definition.exception.DefinitionException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ParseHelper {

    final static Logger logger = LoggerFactory.getLogger(ParseHelper.class);

    public static XStream xstream = XStreamHelper.getInstance();

    /**
     * parse xml to definition
     *
     * @param is
     * @return
     */
    public static <T> T parse(InputStream is) {
        return parseInner(is);
    }

    /**
     * parse xml to definition
     *
     * @param xml
     * @return
     */
    public static <T> T parse(String xml) {
        return parseInner(xml);
    }

    private static <R, T> R parseInner(T xml) {
        R definition;
        try {
            if (xml instanceof String) {
                definition = (R) xstream.fromXML((String) xml);
            } else {
                definition = (R) xstream.fromXML((InputStream) xml);
            }
        } catch (Exception e) {
            logger.error("parse failed by xstream" + e);
            throw new DefinitionException("Parse failed by xstream", e);
        }
        if (definition == null) {
            logger.error("definition is empty");
            throw new DefinitionException("definition is empty");
        }
        return definition;
    }

    /**
     * parse xml to definition
     *
     * @param encodeStr
     * @return
     */
    public static String decode(String encodeStr) {
        String decodeStr = null;
        try {
            //解码
            if (StringUtils.isNotBlank(encodeStr)) {
                decodeStr = java.net.URLDecoder.decode(encodeStr, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            logger.error("decode error", e);
        }
        return decodeStr;
    }
}
