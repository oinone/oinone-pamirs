package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.io.naming.NameCoder;
import org.xmlpull.v1.XmlPullParser;

import java.io.Reader;

/**
 * @deprecated please using {@link pro.shushi.pamirs.framework.common.utils.xstream.PamirsXppReader}
 */
@Deprecated
public class PamirsXppReader extends pro.shushi.pamirs.framework.common.utils.xstream.PamirsXppReader {

    public PamirsXppReader(Reader reader, XmlPullParser parser) {
        super(reader, parser);
    }

    public PamirsXppReader(Reader reader, XmlPullParser parser, NameCoder nameCoder) {
        super(reader, parser, nameCoder);
    }
}
