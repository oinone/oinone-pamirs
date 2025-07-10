package pro.shushi.pamirs.framework.common.utils.xstream;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * 重写{@link MXParser}
 *
 * @author Adamancy Zhang at 16:25 on 2021-08-27
 */
public class PamirsMXParser extends MXParser {

    @Override
    protected int nextImpl() throws XmlPullParserException, IOException {
        return super.nextImpl();
    }
}
