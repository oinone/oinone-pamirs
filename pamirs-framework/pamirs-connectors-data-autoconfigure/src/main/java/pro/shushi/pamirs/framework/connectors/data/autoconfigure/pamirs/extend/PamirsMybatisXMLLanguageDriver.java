package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs.extend;

import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;
import org.aspectj.util.SoftHashMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 继承 {@link MybatisXMLLanguageDriver} 重写Script脚本解析，增加缓存
 *
 * @author Adamancy Zhang at 13:08 on 2024-07-10
 */
public class PamirsMybatisXMLLanguageDriver extends MybatisXMLLanguageDriver {

    private static final Map<String, ParseScriptResult> CACHE_STORAGE = new SoftHashMap<>();

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        // issue #3
        if (script.startsWith("<script>")) {
            ParseScriptResult result = parseScript(configuration, script, parameterType);
            return result.builder.parseScriptNode(result.mixedSqlNode);
        } else {
            // issue #127
            script = PropertyParser.parse(script, configuration.getVariables());
            TextSqlNode textSqlNode = new TextSqlNode(script);
            if (textSqlNode.isDynamic()) {
                return new DynamicSqlSource(configuration, textSqlNode);
            } else {
                return new RawSqlSource(configuration, script, parameterType);
            }
        }
    }

    private ParseScriptResult parseScript(Configuration configuration, String script, Class<?> parameterType) {
        ParseScriptResult result = CACHE_STORAGE.get(script);
        if (result == null) {
            XPathParser parser = new XPathParser(script, false, configuration.getVariables(), new XMLMapperEntityResolver());
            XNode node = parser.evalNode("/script");
            PamirsXMLScriptBuilder builder = new PamirsXMLScriptBuilder(configuration, parameterType);
            result = new ParseScriptResult(builder, builder.parseDynamicTags(node));
            CACHE_STORAGE.put(script, result);
        }
        return result;
    }

    private static class PamirsXMLScriptBuilder extends BaseBuilder {

        private boolean isDynamic;
        private final Class<?> parameterType;
        private final Map<String, NodeHandler> nodeHandlerMap = new HashMap<>();

        public PamirsXMLScriptBuilder(Configuration configuration, Class<?> parameterType) {
            super(configuration);
            this.parameterType = parameterType;
            initNodeHandlerMap();
        }


        private void initNodeHandlerMap() {
            nodeHandlerMap.put("trim", new TrimHandler());
            nodeHandlerMap.put("where", new WhereHandler());
            nodeHandlerMap.put("set", new SetHandler());
            nodeHandlerMap.put("foreach", new ForEachHandler());
            nodeHandlerMap.put("if", new IfHandler());
            nodeHandlerMap.put("choose", new ChooseHandler());
            nodeHandlerMap.put("when", new IfHandler());
            nodeHandlerMap.put("otherwise", new OtherwiseHandler());
            nodeHandlerMap.put("bind", new BindHandler());
        }

        public SqlSource parseScriptNode(MixedSqlNode rootSqlNode) {
            SqlSource sqlSource;
            if (isDynamic) {
                sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
            } else {
                sqlSource = new RawSqlSource(configuration, rootSqlNode, parameterType);
            }
            return sqlSource;
        }

        protected MixedSqlNode parseDynamicTags(XNode node) {
            List<SqlNode> contents = new ArrayList<>();
            NodeList children = node.getNode().getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                XNode child = node.newXNode(children.item(i));
                if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE || child.getNode().getNodeType() == Node.TEXT_NODE) {
                    String data = child.getStringBody("");
                    TextSqlNode textSqlNode = new TextSqlNode(data);
                    if (textSqlNode.isDynamic()) {
                        contents.add(textSqlNode);
                        isDynamic = true;
                    } else {
                        contents.add(new StaticTextSqlNode(data));
                    }
                } else if (child.getNode().getNodeType() == Node.ELEMENT_NODE) { // issue #628
                    String nodeName = child.getNode().getNodeName();
                    NodeHandler handler = nodeHandlerMap.get(nodeName);
                    if (handler == null) {
                        throw new BuilderException("Unknown element <" + nodeName + "> in SQL statement.");
                    }
                    handler.handleNode(child, contents);
                    isDynamic = true;
                }
            }
            return new MixedSqlNode(contents);
        }

        private interface NodeHandler {
            void handleNode(XNode nodeToHandle, List<SqlNode> targetContents);
        }

        private class BindHandler implements NodeHandler {
            public BindHandler() {
                // Prevent Synthetic Access
            }

            @Override
            public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
                final String name = nodeToHandle.getStringAttribute("name");
                final String expression = nodeToHandle.getStringAttribute("value");
                final VarDeclSqlNode node = new VarDeclSqlNode(name, expression);
                targetContents.add(node);
            }
        }

        private class TrimHandler implements NodeHandler {
            public TrimHandler() {
                // Prevent Synthetic Access
            }

            @Override
            public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
                MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
                String prefix = nodeToHandle.getStringAttribute("prefix");
                String prefixOverrides = nodeToHandle.getStringAttribute("prefixOverrides");
                String suffix = nodeToHandle.getStringAttribute("suffix");
                String suffixOverrides = nodeToHandle.getStringAttribute("suffixOverrides");
                TrimSqlNode trim = new TrimSqlNode(configuration, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
                targetContents.add(trim);
            }
        }

        private class WhereHandler implements NodeHandler {
            public WhereHandler() {
                // Prevent Synthetic Access
            }

            @Override
            public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
                MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
                WhereSqlNode where = new WhereSqlNode(configuration, mixedSqlNode);
                targetContents.add(where);
            }
        }

        private class SetHandler implements NodeHandler {
            public SetHandler() {
                // Prevent Synthetic Access
            }

            @Override
            public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
                MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
                SetSqlNode set = new SetSqlNode(configuration, mixedSqlNode);
                targetContents.add(set);
            }
        }

        private class ForEachHandler implements NodeHandler {
            public ForEachHandler() {
                // Prevent Synthetic Access
            }

            @Override
            public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
                MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
                String collection = nodeToHandle.getStringAttribute("collection");
                String item = nodeToHandle.getStringAttribute("item");
                String index = nodeToHandle.getStringAttribute("index");
                String open = nodeToHandle.getStringAttribute("open");
                String close = nodeToHandle.getStringAttribute("close");
                String separator = nodeToHandle.getStringAttribute("separator");
                ForEachSqlNode forEachSqlNode = new ForEachSqlNode(configuration, mixedSqlNode, collection, index, item, open, close, separator);
                targetContents.add(forEachSqlNode);
            }
        }

        private class IfHandler implements NodeHandler {
            public IfHandler() {
                // Prevent Synthetic Access
            }

            @Override
            public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
                MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
                String test = nodeToHandle.getStringAttribute("test");
                IfSqlNode ifSqlNode = new IfSqlNode(mixedSqlNode, test);
                targetContents.add(ifSqlNode);
            }
        }

        private class OtherwiseHandler implements NodeHandler {
            public OtherwiseHandler() {
                // Prevent Synthetic Access
            }

            @Override
            public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
                MixedSqlNode mixedSqlNode = parseDynamicTags(nodeToHandle);
                targetContents.add(mixedSqlNode);
            }
        }

        private class ChooseHandler implements NodeHandler {
            public ChooseHandler() {
                // Prevent Synthetic Access
            }

            @Override
            public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
                List<SqlNode> whenSqlNodes = new ArrayList<>();
                List<SqlNode> otherwiseSqlNodes = new ArrayList<>();
                handleWhenOtherwiseNodes(nodeToHandle, whenSqlNodes, otherwiseSqlNodes);
                SqlNode defaultSqlNode = getDefaultSqlNode(otherwiseSqlNodes);
                ChooseSqlNode chooseSqlNode = new ChooseSqlNode(whenSqlNodes, defaultSqlNode);
                targetContents.add(chooseSqlNode);
            }

            private void handleWhenOtherwiseNodes(XNode chooseSqlNode, List<SqlNode> ifSqlNodes, List<SqlNode> defaultSqlNodes) {
                List<XNode> children = chooseSqlNode.getChildren();
                for (XNode child : children) {
                    String nodeName = child.getNode().getNodeName();
                    NodeHandler handler = nodeHandlerMap.get(nodeName);
                    if (handler instanceof IfHandler) {
                        handler.handleNode(child, ifSqlNodes);
                    } else if (handler instanceof OtherwiseHandler) {
                        handler.handleNode(child, defaultSqlNodes);
                    }
                }
            }

            private SqlNode getDefaultSqlNode(List<SqlNode> defaultSqlNodes) {
                SqlNode defaultSqlNode = null;
                if (defaultSqlNodes.size() == 1) {
                    defaultSqlNode = defaultSqlNodes.get(0);
                } else if (defaultSqlNodes.size() > 1) {
                    throw new BuilderException("Too many default (otherwise) elements in choose statement.");
                }
                return defaultSqlNode;
            }
        }
    }

    private static class ParseScriptResult {

        private final PamirsXMLScriptBuilder builder;

        private final MixedSqlNode mixedSqlNode;

        public ParseScriptResult(PamirsXMLScriptBuilder builder, MixedSqlNode mixedSqlNode) {
            this.builder = builder;
            this.mixedSqlNode = mixedSqlNode;
        }
    }
}
