package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.io.naming.NameCoder;

import java.io.Writer;

/**
 * @deprecated please using {@link pro.shushi.pamirs.framework.common.utils.xstream.PamirsPrettyPrintWriter}
 */
@Deprecated
public class PamirsPrettyPrintWriter extends pro.shushi.pamirs.framework.common.utils.xstream.PamirsPrettyPrintWriter {

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
