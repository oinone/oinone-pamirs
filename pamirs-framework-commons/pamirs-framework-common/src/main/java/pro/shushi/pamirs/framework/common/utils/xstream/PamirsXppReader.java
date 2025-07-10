package pro.shushi.pamirs.framework.common.utils.xstream;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.XppReader;
import org.xmlpull.v1.XmlPullParser;

import java.io.Reader;

/**
 * 重写{@link XppReader}
 *
 * @author Adamancy Zhang at 16:14 on 2021-08-27
 */
public class PamirsXppReader extends XppReader {

    public PamirsXppReader(Reader reader, XmlPullParser parser) {
        super(reader, parser);
    }

    public PamirsXppReader(Reader reader, XmlPullParser parser, NameCoder nameCoder) {
        super(reader, parser, nameCoder);
    }
}
