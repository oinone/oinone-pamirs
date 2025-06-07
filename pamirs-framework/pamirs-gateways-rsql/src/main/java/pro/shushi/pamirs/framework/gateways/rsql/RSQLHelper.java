package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.framework.gateways.rsql.computer.RSQLNodeComputer;
import pro.shushi.pamirs.framework.gateways.rsql.connector.NodeConnector;
import pro.shushi.pamirs.framework.gateways.rsql.connector.NodeConnectorType;
import pro.shushi.pamirs.framework.gateways.rsql.connector.RSQLNodeConnector;
import pro.shushi.pamirs.framework.gateways.rsql.optimizer.NodeOptimizer;
import pro.shushi.pamirs.framework.gateways.rsql.optimizer.NodeOptimizerType;
import pro.shushi.pamirs.framework.gateways.rsql.visitor.ModelRSQLParseVisitor;
import pro.shushi.pamirs.framework.gateways.rsql.visitor.NormalRSQLParseVisitor;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import java.util.*;

/**
 * RSQL帮助类
 *
 * @author Adamancy Zhang at 09:23 on 2021-10-21
 */
@Slf4j
public class RSQLHelper {

    private RSQLHelper() {
        //reject create object
    }

    /**
     * Rsql解析（当无法根据{@link String model}获取{@link ModelConfig}时将通过无模型方式解析）
     *
     * @param model 模型编码
     * @param rsql  rsql字符串
     * @return 根节点
     */
    public static TreeNode<RSQLNodeInfo> parse(String model, String rsql) {
        ModelConfig modelConfig = Optional.ofNullable(model).filter(StringUtils::isNotBlank).map(v -> PamirsSession.getContext().getModelConfig(v)).orElse(null);
        if (modelConfig == null) {
            log.warn("RSQL parser error. Invalid model config. model: {}, rsql: {}", model, rsql);
            return parse(rsql, new NormalRSQLParseVisitor<>(), null);
        }
        return parse(modelConfig, rsql);
    }

    /**
     * Rsql解析
     *
     * @param modelConfig 模型配置
     * @param rsql        rsql字符串
     * @return 根节点
     */
    public static TreeNode<RSQLNodeInfo> parse(ModelConfig modelConfig, String rsql) {
        return parse(rsql, new ModelRSQLParseVisitor(), modelConfig);
    }

    /**
     * Rsql解析
     *
     * @param rsql rsql字符串
     * @return 根节点
     */
    public static <T> TreeNode<RSQLNodeInfo> parse(String rsql, RSQLVisitor<TreeNode<RSQLNodeInfo>, T> visitor, T param) {
        Node root = new RSQLParser(RsqlSearchOperation.getOperators()).parse(rsql);
        return root.accept(visitor, param);
    }

    /**
     * 将根节点优化后转换为字符串
     *
     * @param root 根节点
     * @return 优化后的RSQL
     */
    public static String optimize(TreeNode<RSQLNodeInfo> root) {
        return optimize(root, NodeOptimizerType.RSQL.instance());
    }

    /**
     * 将根节点优化后转换为字符串
     *
     * @param root      根节点
     * @param optimizer 优化器
     * @return 优化后的RSQL
     */
    public static String optimize(TreeNode<RSQLNodeInfo> root, NodeOptimizer optimizer) {
        RSQLNodeInfoType type = root.getValue().getType();
        switch (type) {
            case AND:
            case OR:
                List<String> values = new ArrayList<>();
                for (TreeNode<RSQLNodeInfo> child : root.getChildren()) {
                    String value = optimize(child, optimizer);
                    if (value == null) {
                        continue;
                    }
                    values.add(value);
                }
                return optimizer.logicOptimizer(type, values);
            case COMPARISON:
                return optimizer.comparisonOptimizer(root.getValue());
            default:
                throw new IllegalArgumentException("Invalid node info type. value=" + type);
        }
    }

    /**
     * Rsql计算
     *
     * @param model 模型编码
     * @param rsql  rsql字符串
     * @param data  数据
     * @param <T>   任意模型类型
     * @return 当前数据是否与rsql匹配
     */
    public static <T extends D> Boolean compute(String model, String rsql, T data) {
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            return null;
        }
        return compute(modelConfig, rsql, data);
    }

    public static <T extends D> Boolean compute(ModelConfig modelConfig, String rsql, T data) {
        TreeNode<RSQLNodeInfo> root = parse(modelConfig, rsql);
        return compute(root, data);
    }

    public static <T extends D> boolean compute(TreeNode<RSQLNodeInfo> root, T data) {
        RSQLNodeInfoType type = root.getValue().getType();
        switch (type) {
            case AND:
                for (TreeNode<RSQLNodeInfo> child : root.getChildren()) {
                    boolean value = compute(child, data);
                    if (!value) {
                        return false;
                    }
                }
                return true;
            case OR:
                for (TreeNode<RSQLNodeInfo> child : root.getChildren()) {
                    boolean value = compute(child, data);
                    if (value) {
                        return true;
                    }
                }
                return false;
            case COMPARISON:
                return RSQLNodeComputer.getInstance().comparisonCompute(root.getValue(), data);
            default:
                throw new IllegalArgumentException("Invalid node info type. value=" + type);
        }
    }

    /**
     * 将根节点转换为rsql字符串
     *
     * @param root 根节点
     * @return rsql字符串
     */
    public static String toRsql(TreeNode<RSQLNodeInfo> root) {
        return toTargetString(root, NodeConnectorType.RSQL.instance());
    }

    public static String toSql(TreeNode<RSQLNodeInfo> root) {
        return toTargetString(root, NodeConnectorType.SQL.instance());
    }

    /**
     * 将根节点转换为指定格式字符串
     *
     * @param root      根节点
     * @param connector 连接器
     * @return 指定格式字符串
     */
    public static String toTargetString(TreeNode<RSQLNodeInfo> root, NodeConnector connector) {
        RSQLNodeInfoType type = root.getValue().getType();
        switch (type) {
            case AND:
            case OR:
                List<String> values = new ArrayList<>();
                for (TreeNode<RSQLNodeInfo> child : root.getChildren()) {
                    String value = toTargetString(child, connector);
                    if (value == null) {
                        continue;
                    }
                    values.add(value);
                }
                return connector.logicConnector(root, type, values);
            case COMPARISON:
                return connector.comparisonConnector(root);
            default:
                throw new IllegalArgumentException("Invalid node info type. value=" + type);
        }
    }

    @SafeVarargs
    public static <T> Map<String, Object> getRsqlValues(String rsql, Getter<T, ?>... getters) {
        Set<String> fields = new HashSet<>();
        for (Getter<T, ?> getter : getters) {
            fields.add(LambdaUtil.fetchFieldName(getter));
        }
        return getRsqlValues(rsql, fields);
    }

    public static Map<String, Object> getRsqlValues(String rsql, Set<String> fields) {
        if (StringUtils.isBlank(rsql) || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        TreeNode<RSQLNodeInfo> root = RSQLHelper.parse(rsql, new NormalRSQLParseVisitor<>(), null);
        if (root == null) {
            log.warn("rsql parse error.");
            return Collections.emptyMap();
        }
        Map<String, Object> values = new HashMap<>();
        RSQLHelper.toTargetString(root, new RSQLNodeConnector() {
            @Override
            public String comparisonConnector(RSQLNodeInfo nodeInfo) {
                String field = nodeInfo.getField();
                if (fields.contains(field)) {
                    values.put(field, nodeInfo.getArguments().get(0));
                }
                return super.comparisonConnector(nodeInfo);
            }
        });
        return values;
    }

    @SafeVarargs
    public static <T> Map<String, Object> getQueryData(IWrapper<?> queryWrapper, Getter<T, ?>... getters) {
        String rsql = queryWrapper.getOriginRsql();
        Map<String, Object> queryData = RSQLHelper.getRsqlValues(rsql, getters);
        if (queryWrapper.getQueryData() != null) {
            queryData.putAll(queryWrapper.getQueryData());
        }
        return queryData;
    }

    @Deprecated
    public static class RSQLNodeStringConnector implements NodeConnector {

        public RSQLNodeStringConnector(String fieldName) {
            this.fieldName = fieldName;
        }

        private final String fieldName;

        @Override
        public String logicConnector(RSQLNodeInfoType type, List<String> values) {
            if (CollectionUtils.isNotEmpty(values)) {
                return values.get(0);
            } else {
                return null;
            }
        }

        @Override
        public String comparisonConnector(RSQLNodeInfo nodeInfo) {
            if (StringUtils.equals(nodeInfo.getField(), this.fieldName)) {
                List<String> args = nodeInfo.getArguments();
                if (CollectionUtils.isNotEmpty(args)) {
                    return args.get(0);
                }
            }
            return null;
        }
    }

    @Deprecated
    public static <T extends AbstractModel> Optional<String> getFieldValue(String model, String rsql, Getter<T, ?> fieldGetter) {
        String field = LambdaUtil.fetchFieldName(fieldGetter);
        if (StringUtils.isAnyBlank(model, rsql, field)) {
            return Optional.empty();
        }
        return Optional.ofNullable(RSQLHelper.parse(model, rsql))
                .map(_treeNode -> RSQLHelper.toTargetString(_treeNode, new RSQLNodeStringConnector(field)));
    }

    /**
     * Rsql解析 将属性字段值从QueryWrapper中originRsql属性值的Rsql解出来
     *
     * @param queryWrapper 查询Wrapper
     * @param fields       需要解析出来的属性字段列表
     * @param valeMap      属性对应值的Map
     * @return 返回生产单Id列表
     */
    @Deprecated
    public static QueryWrapper convertWrapper(QueryWrapper queryWrapper, List<String> fields, Map<String, Object> valeMap) {
        if (StringUtils.isNotBlank(queryWrapper.getOriginRsql())) {
            String rsql = RSQLHelper.toTargetString(RSQLHelper.parse(queryWrapper.getModel(), queryWrapper.getOriginRsql()), new RSQLNodeConnector() {
                @Override
                public String comparisonConnector(RSQLNodeInfo nodeInfo) {
                    //判断字段为unStore，则进行替换
                    String field = nodeInfo.getField();
                    if (fields.contains(field)) {
                        valeMap.put(field, nodeInfo.getArguments().get(0));
                        RSQLNodeInfo newNode = new RSQLNodeInfo(nodeInfo.getType());
                        //设置查询字段为name
                        newNode.setField("1");
                        newNode.setOperator(RsqlSearchOperation.EQUAL.getOperator());
                        newNode.setArguments(Collections.singletonList("1"));
                        return super.comparisonConnector(newNode);
                    }
                    return super.comparisonConnector(nodeInfo);
                }
            });
            queryWrapper = Pops.f(Pops.query().from(queryWrapper.getModel())).get();
            //把RSQL转换成SQL
            String sql = RsqlParseHelper.parseRsql2Sql(queryWrapper.getModel(), rsql);
            if (StringUtils.isNotBlank(sql)) {
                queryWrapper.apply(sql);
            }
            return queryWrapper;
        }
        return queryWrapper;
    }
}
