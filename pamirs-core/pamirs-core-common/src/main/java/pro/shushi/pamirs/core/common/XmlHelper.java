package pro.shushi.pamirs.core.common;

import org.apache.commons.lang3.StringEscapeUtils;
import pro.shushi.pamirs.core.common.map.DefaultEntityMap;
import pro.shushi.pamirs.core.common.map.EntityMap;

import java.io.StringWriter;
import java.util.Stack;

/**
 * XML 帮助类
 *
 * @author Adamancy Zhang at 15:52 on 2021-08-27
 */
public class XmlHelper {

    private static final EntityMap<String, Integer> XML;

    static {
        XML = new DefaultEntityMap<>();
        XML.put("quot", 34);
        XML.put("amp", 38);
        XML.put("lt", 60);
        XML.put("gt", 62);
        XML.put("apos", 39);
    }

    private XmlHelper() {
        //reject create object
    }

    /**
     * 使用括号匹配算法正确识别content区域并进行编码
     *
     * @param xml xml字符串
     * @return 编码后的xml字符串
     */
    public static String encode(String xml) {
        StringBuilder builder = new StringBuilder();
        Stack<Node> stack = new Stack<>();
        Node node = null;
        int length = xml.length();
        char c;
        for (int i = 0; i < length; i++) {
            c = xml.charAt(i);
            switch (c) {
                case '<':
                    i++;
                    if (i >= length) {
                        break;
                    }
                    c = xml.charAt(i);
                    if (c == '/') {
                        node = new Node(Type.END);
                    } else {
                        node = new Node(Type.BEGIN);
                        node.builder.append(c);
                    }
                    stack.push(node);
                    break;
                case '>':
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Invalid xml string.");
                    }
                    node = stack.pop();
                    if (stack.isEmpty()) {
                        if (Type.CONTENT.equals(node.type)) {
                            node.builder.append(c);
                            stack.push(node);
                            break;
                        } else {
                            builder.append(node.getValue());
                        }
                    } else {
                        if (Type.END.equals(node.type)) {
                            clearStackToList(stack, node, builder);
                        } else {
                            if (Type.BEGIN.equals(node.type)) {
                                node.isFinished = true;
                            }
                            stack.push(node);
                        }
                    }
                    node = null;
                    break;
                default:
                    if (node == null) {
                        node = new Node(Type.CONTENT);
                        stack.push(node);
                    }
                    node.builder.append(c);
                    break;
            }
        }
        if (stack.isEmpty()) {
            return builder.toString();
        }
        throw new IllegalArgumentException("Invalid xml string.");
    }

    public static String decode(String xml) {
        return StringEscapeUtils.unescapeXml(xml);
    }

    private static void clearStackToList(Stack<Node> stack, Node node, StringBuilder builder) {
        Stack<Node> temporaryStack = new Stack<>();
        Node lastNode, temporaryNode;
        while (true) {
            lastNode = stack.pop();
            if (stack.isEmpty()) {
                break;
            }
            temporaryStack.push(lastNode);
        }
        while (!temporaryStack.isEmpty()) {
            temporaryNode = temporaryStack.pop();
            switch (temporaryNode.type) {
                case BEGIN:
                    lastNode.builder.append("<").append(temporaryNode.builder);
                    if (temporaryNode.isFinished) {
                        lastNode.builder.append(">");
                    }
                    break;
                case CONTENT:
                    lastNode.builder.append(temporaryNode.builder);
                    break;
                case END:
                    lastNode.builder.append(">").append(temporaryNode.builder);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type value.");
            }
        }
        if (stack.isEmpty()) {
            builder.append(lastNode.getValue());
            builder.append(node.getValue());
        } else {
            throw new IllegalArgumentException("Invalid xml string.");
        }
    }

    private static class Node {

        private final Type type;

        private final StringBuilder builder;

        private boolean isFinished;

        public Node(Type type) {
            this.type = type;
            this.builder = new StringBuilder();
            this.isFinished = false;
        }

        private String getValue() {
            String value = builder.toString();
            switch (type) {
                case BEGIN:
                    return "<" + value + ">";
                case CONTENT:
                    return escape(value);
                case END:
                    return "</" + value + ">";
                default:
                    throw new IllegalArgumentException("Invalid type value.");
            }
        }
    }

    public static String escape(String str) {
        return escape(str, str.length());
    }

    public static String escape(String str, int length) {
        StringWriter writer = new StringWriter((int) ((double) length + (double) length * 0.1D));
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            String decodeValue = XML.getKeyByValue((int) c);
            if (decodeValue == null) {
                writer.write(c);
            } else {
                writer.write(38);
                writer.write(decodeValue);
                writer.write(59);
            }
        }
        return writer.toString();
    }

    private enum Type {
        BEGIN, CONTENT, END
    }
}
