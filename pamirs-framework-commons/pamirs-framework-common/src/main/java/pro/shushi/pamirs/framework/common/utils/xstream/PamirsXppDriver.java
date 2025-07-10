package pro.shushi.pamirs.framework.common.utils.xstream;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.Reader;
import java.io.Writer;

/**
 * 重写{@link XppDriver}
 *
 * @author Adamancy Zhang at 16:10 on 2021-08-27
 */
public class PamirsXppDriver extends XppDriver {

    @Override
    public HierarchicalStreamReader createReader(Reader in) {
        try {
            return new PamirsXppReader(in, createParser(), getNameCoder());
        } catch (final XmlPullParserException e) {
            throw new StreamException("Cannot create XmlPullParser", e);
        }
    }

    @Override
    public HierarchicalStreamWriter createWriter(Writer out) {
        return new PamirsPrettyPrintWriter(out, getNameCoder());
    }

    @Override
    protected synchronized XmlPullParser createParser() throws XmlPullParserException {
        return new PamirsMXParser();
    }
}
