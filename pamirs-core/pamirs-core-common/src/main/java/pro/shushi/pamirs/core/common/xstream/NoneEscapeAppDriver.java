package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.Writer;

public class NoneEscapeAppDriver extends XppDriver {

    @Override
    public HierarchicalStreamWriter createWriter(Writer out) {
        return new NoneEscapePrettyPintWriter(out, getNameCoder());
    }

}