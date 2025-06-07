package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.AbstractWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;

import java.io.Writer;

/**
 * 仅转义【&】字符
 */
public class NoneEscapePrettyPintWriter extends AbstractWriter {

    public static int XML_QUIRKS = -1;
    public static int XML_1_0 = 0;
    public static int XML_1_1 = 1;

    private final QuickWriter writer;
    private final FastStack elementStack = new FastStack(16);
    private final char[] lineIndenter;
    private final int mode;

    private boolean tagInProgress;
    protected int depth;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    private String newLine;

    private static final char[] NULL = "&#x0;".toCharArray();
    private static final char[] AMP = "&amp;".toCharArray();
    private static final char[] LT = "&lt;".toCharArray();
    private static final char[] GT = "&gt;".toCharArray();
    private static final char[] CR = "&#xd;".toCharArray();
    private static final char[] QUOT = "&quot;".toCharArray();
    private static final char[] APOS = "&apos;".toCharArray();
    private static final char[] CLOSE = "</".toCharArray();

    private NoneEscapePrettyPintWriter(
            Writer writer, int mode, char[] lineIndenter, NameCoder nameCoder,
            String newLine) {
        super(nameCoder);
        this.writer = new QuickWriter(writer);
        this.lineIndenter = lineIndenter;
        this.newLine = newLine;
        this.mode = mode;
        if (mode < XML_QUIRKS || mode > XML_1_1) {
            throw new IllegalArgumentException("Not a valid XML mode");
        }
    }

    /**
     * @since 1.4
     */
    public NoneEscapePrettyPintWriter(Writer writer, NameCoder nameCoder) {
        this(writer, XML_QUIRKS, new char[]{' ', ' '}, nameCoder, "\n");
    }

    @Override
    public void startNode(String name) {
        String escapedName = encodeNode(name);
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(escapedName);
        elementStack.push(escapedName);
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    @Override
    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    @Override
    public void setValue(String text) {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();
        writeText(writer, text);
    }

    @Override
    public void addAttribute(String key, String value) {
        writer.write(' ');
        writer.write(encodeAttribute(key));
        writer.write('=');
        writer.write('\"');
        writeAttributeValue(writer, value);
        writer.write('\"');
    }

    protected void writeAttributeValue(QuickWriter writer, String text) {
        writeText(text, true);
    }

    protected void writeText(QuickWriter writer, String text) {
        writeText(text, false);
    }

    private void writeText(String text, boolean isAttribute) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\0':
                    if (mode == XML_QUIRKS) {
                        this.writer.write(NULL);
                    } else {
                        throw new StreamException("Invalid character 0x0 in XML stream");
                    }
                    break;
                case '&':
                    if (isAttribute) {
                        this.writer.write(AMP);
                    } else {
                        this.writer.write(c);
                    }
                    break;
                case '<':
//                    if (isAttribute)
//                        this.writer.write(LT);
//                    else
                    this.writer.write(c);
                    break;
                case '>':
//                    if (isAttribute)
//                        this.writer.write(GT);
//                    else
                    this.writer.write(c);
                    break;
                case '"':
                    if (isAttribute) {
                        this.writer.write(QUOT);
                    } else {
                        this.writer.write(c);
                    }
                    break;
                case '\'':
//                    if (isAttribute)
//                        this.writer.write(APOS);
//                    else
                    this.writer.write(c);
                    break;
                case '\r':
//                    if (isAttribute)
//                        this.writer.write(CR);
//                    else
                    this.writer.write(c);
                    break;
                case '\t':
                case '\n':
                    if (!isAttribute) {
                        this.writer.write(c);
                        break;
                    }
                default:
                    if (Character.isDefined(c) && !Character.isISOControl(c)) {
                        if (mode != XML_QUIRKS) {
                            if (c > '\ud7ff' && c < '\ue000') {
                                throw new StreamException("Invalid character 0x"
                                        + Integer.toHexString(c)
                                        + " in XML stream");
                            }
                        }
                        this.writer.write(c);
                    } else {
                        if (mode == XML_1_0) {
                            if (c < 9
                                    || c == '\u000b'
                                    || c == '\u000c'
                                    || c == '\u000e'
                                    || (c >= '\u000f' && c <= '\u001f')) {
                                throw new StreamException("Invalid character 0x"
                                        + Integer.toHexString(c)
                                        + " in XML 1.0 stream");
                            }
                        }
                        if (mode != XML_QUIRKS) {
                            if (c == '\ufffe' || c == '\uffff') {
                                throw new StreamException("Invalid character 0x"
                                        + Integer.toHexString(c)
                                        + " in XML stream");
                            }
                        }
                        this.writer.write("&#x");
                        this.writer.write(Integer.toHexString(c));
                        this.writer.write(';');
                    }
            }
        }
    }

    @Override
    public void endNode() {
        depth--;
        if (tagIsEmpty) {
            writer.write('/');
            readyForNewLine = false;
            finishTag();
            elementStack.popSilently();
        } else {
            finishTag();
            writer.write(CLOSE);
            writer.write((String) elementStack.pop());
            writer.write('>');
        }
        readyForNewLine = true;
        if (depth == 0) {
            writer.flush();
        }
    }

    private void finishTag() {
        if (tagInProgress) {
            writer.write('>');
        }
        tagInProgress = false;
        if (readyForNewLine) {
            endOfLine();
        }
        readyForNewLine = false;
        tagIsEmpty = false;
    }

    protected void endOfLine() {
        writer.write(getNewLine());
        for (int i = 0; i < depth; i++) {
            writer.write(lineIndenter);
        }
    }

    @Override
    public void flush() {
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }

    /**
     * Retrieve the line terminator.
     * <p>
     * This method returns always a line feed, since according the XML specification any parser
     * must ignore a carriage return. Overload this method, if you need different behavior.
     *
     * @return the line terminator
     * @since 1.3
     */
    protected String getNewLine() {
        return newLine;
    }
}
