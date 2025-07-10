package pro.shushi.pamirs.framework.common.utils.xstream;

import java.util.HashMap;
import java.util.Map;

/**
 * XML节点上下文
 *
 * @author Adamancy Zhang at 12:14 on 2021-08-04
 */
public class XMLNodeContent {

    private static final String TRUE_STRING = Boolean.TRUE.toString();

    private static final String FALSE_STRING = Boolean.FALSE.toString();

    private Map<String, String> attributes = new HashMap<>();

    private String content = null;

    public XMLNodeContent() {
    }

    public XMLNodeContent(String content) {
        this.content = content;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    public XMLNodeContent putAttribute(String name, String value) {
        this.attributes.put(name, value);
        return this;
    }

    public XMLNodeContent removeAttribute(String name) {
        this.attributes.remove(name);
        return this;
    }

    public boolean getBooleanAttribute(String name) {
        return getBooleanAttribute(name, false);
    }

    public boolean getBooleanAttribute(String name, boolean defaultValue) {
        String value = getAttribute(name);
        if (TRUE_STRING.equals(value)) {
            return true;
        }
        if (FALSE_STRING.equals(value)) {
            return false;
        }
        return defaultValue;
    }
}
