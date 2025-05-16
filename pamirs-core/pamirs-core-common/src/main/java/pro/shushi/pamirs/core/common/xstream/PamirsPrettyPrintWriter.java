package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.Writer;

/**
 * 重写{@link PrettyPrintWriter}
 *
 * @author Adamancy Zhang at 16:15 on 2021-08-27
 */
public class PamirsPrettyPrintWriter extends PrettyPrintWriter {

    public PamirsPrettyPrintWriter(Writer writer, int mode, char[] lineIndenter, NameCoder nameCoder) {
        super(writer, mode, lineIndenter, nameCoder);
    }

    public PamirsPrettyPrintWriter(Writer writer, int mode, char[] lineIndenter) {
        super(writer, mode, lineIndenter);
    }

    public PamirsPrettyPrintWriter(Writer writer, char[] lineIndenter) {
        super(writer, lineIndenter);
    }

    public PamirsPrettyPrintWriter(Writer writer, int mode, String lineIndenter) {
        super(writer, mode, lineIndenter);
    }

    public PamirsPrettyPrintWriter(Writer writer, String lineIndenter) {
        super(writer, lineIndenter);
    }

    public PamirsPrettyPrintWriter(Writer writer, int mode, NameCoder nameCoder) {
        super(writer, mode, nameCoder);
    }

    public PamirsPrettyPrintWriter(Writer writer, NameCoder nameCoder) {
        super(writer, nameCoder);
    }

    public PamirsPrettyPrintWriter(Writer writer, int mode) {
        super(writer, mode);
    }

    public PamirsPrettyPrintWriter(Writer writer) {
        super(writer);
    }
}
