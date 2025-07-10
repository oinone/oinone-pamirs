package pro.shushi.pamirs.core.common.xstream;

/**
 * @deprecated please using {@link pro.shushi.pamirs.framework.common.utils.xstream.XMLNodeContent}
 */
@Deprecated
public class XMLNodeContent extends pro.shushi.pamirs.framework.common.utils.xstream.XMLNodeContent {

    public XMLNodeContent() {
        super();
    }

    public XMLNodeContent(String content) {
        super(content);
    }

    public XMLNodeContent putAttribute(String name, String value) {
        this.attributes.put(name, value);
        return this;
    }

    public XMLNodeContent removeAttribute(String name) {
        this.attributes.remove(name);
        return this;
    }
}
