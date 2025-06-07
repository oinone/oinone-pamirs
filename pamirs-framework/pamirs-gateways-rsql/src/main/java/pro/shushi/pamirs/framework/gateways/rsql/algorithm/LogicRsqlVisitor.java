package pro.shushi.pamirs.framework.gateways.rsql.algorithm;

import cz.jirutka.rsql.parser.ast.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shier
 * date  2021/3/26 3:21 下午
 */
public class LogicRsqlVisitor implements RSQLVisitor<RsqlTreeNode, RsqlTreeNode> {
    private RsqlAlgorithm algorithm = new RsqlAlgorithm();

    @Override
    public RsqlTreeNode visit(AndNode andNode, RsqlTreeNode s) {
        return handleLogicNode(andNode, s);
    }

    @Override
    public RsqlTreeNode visit(OrNode orNode, RsqlTreeNode s) {
        return handleLogicNode(orNode, s);
    }

    @Override
    public RsqlTreeNode visit(ComparisonNode comparisonNode, RsqlTreeNode s) {
        RsqlTreeLeafNode treeLeafNode = new RsqlTreeLeafNode();
        if (s == null) {
            //说明只有这一个节点
            Set<Node> comparisonNodes = new HashSet<>();
            comparisonNodes.add(comparisonNode);
            RsqlTreeLeafNode leafNode = new RsqlTreeLeafNode();
            leafNode.setQueries(comparisonNodes);
            return leafNode;
        }
        if (s instanceof RsqlTreeLeafNode) {
            Set<Node> queries = ((RsqlTreeLeafNode) s).getQueries();
            if (queries == null) {
                ((RsqlTreeLeafNode) s).setQueries(new HashSet<>());
            }
            ((RsqlTreeLeafNode) s).getQueries().add(comparisonNode);

            treeLeafNode.setQueries(queries);
            return treeLeafNode;
        } else if (s instanceof RsqlTreeLogicalNode) {
            RsqlTreeLeafNode simpleTreeLeafNode = new RsqlTreeLeafNode();
            simpleTreeLeafNode.getQueries().add(comparisonNode);
            return simpleTreeLeafNode;
        }
        return null;
    }

    RsqlTreeNode handleLogicNode(LogicalNode node, RsqlTreeNode s) {
        Boolean isAnd = node instanceof AndNode;
        Boolean isRoot = Boolean.FALSE;
        if (s == null) {
            //构建出一个空的根root节点
            s = new RsqlTreeNode();//根节点
            isRoot = Boolean.TRUE;
        }
        List<Node> children = node.getChildren();
        for (Node child : children) {
            RsqlTreeNode childNode;
            if (child instanceof OrNode) {
                RsqlTreeLeafNode currentNode = new RsqlTreeLeafNode();
                childNode = visit((OrNode) child, currentNode);
            } else if (child instanceof AndNode) {
                childNode = visit((AndNode) child, new RsqlTreeLogicalNode());
            } else {
                if (isRoot) {
                    childNode = visit((ComparisonNode) child, new RsqlTreeLeafNode());
                } else {
                    childNode = visit((ComparisonNode) child, s);
                }
            }
            if (isRoot) {
                if (isAnd) {
                    if (CollectionUtils.isEmpty(s.getChildrenNodes())) {
                        RsqlTreeLogicalNode firstNode = new RsqlTreeLogicalNode();
                        firstNode.getChildrenNodes().add(childNode);
                        s.setChildrenNodes(Arrays.asList(firstNode));
                    } else {
                        RsqlTreeLogicalNode logicalNode = (RsqlTreeLogicalNode) s.getChildrenNodes().get(0);
                        logicalNode.getChildrenNodes().add(childNode);
                    }
                } else {
                    s.getChildrenNodes().add(childNode);
                }
            } else if (isAnd) {
                s.getChildrenNodes().add(childNode);
            } else if (!isAnd && child instanceof AndNode) {
                ((RsqlTreeLeafNode) s).getAndTreeNodes().add((RsqlTreeLogicalNode) childNode);
            }
        }
        return s;
    }

    //优化逻辑语句
    public String optimizeLogicalStatementRsql(RsqlTreeNode rsqlTreeNode) {
        return buildRsql(optimizeLogicalStatements(rsqlTreeNode));
    }

    public RsqlTreeNode optimizeLogicalStatements(RsqlTreeNode rsqlTreeNode) {
        if (rsqlTreeNode instanceof RsqlTreeLogicalNode) {
            optimizeAndStatements((RsqlTreeLogicalNode) rsqlTreeNode);
        } else if (rsqlTreeNode instanceof RsqlTreeLeafNode) {
            //另一颗树
            //将当前的叶子节点拆分成为多个Logical节点
            List<RsqlTreeNode> treeLeafNodes = optimizeOrStatements((RsqlTreeLeafNode) rsqlTreeNode);
            rsqlTreeNode = new RsqlTreeLogicalNode().setChildrenNodes(treeLeafNodes);
            optimizeAndStatements((RsqlTreeLogicalNode) rsqlTreeNode);
        } else if (rsqlTreeNode instanceof RsqlTreeNode) {
            List<RsqlTreeNode> childrenNodes = rsqlTreeNode.getChildrenNodes();
            if (childrenNodes.size() == 1) {
                //说明这是一棵树
                RsqlTreeNode rsqlTreeNode2 = childrenNodes.get(0);
                optimizeLogicalStatements(rsqlTreeNode2);
                List<RsqlTreeNode> childrenNodes1 = rsqlTreeNode2.getChildrenNodes();
                //去重复
                List<List<Node>> lists = new ArrayList<>();
                for (RsqlTreeNode node : childrenNodes1) {
                    ArrayList<Node> nodes = new ArrayList<>(((RsqlTreeLeafNode) node).getQueries());
                    lists.add(nodes);
                }
                List<List<Node>> list = algorithm.removeBigger(lists);
                List<RsqlTreeNode> treeLeafNodes = new ArrayList<>();
                for (List<Node> l : list) {
                    RsqlTreeLeafNode leafNode = new RsqlTreeLeafNode();
                    HashSet<Node> nodes = new HashSet<>();
                    nodes.addAll(l);
                    leafNode.setQueries(nodes);
                    treeLeafNodes.add(leafNode);
                }
                rsqlTreeNode2.setChildrenNodes(treeLeafNodes);
                return rsqlTreeNode2;
            }
            //这是多棵树，最终的结果需要合并
            for (RsqlTreeNode tree : rsqlTreeNode.getChildrenNodes()) {
                //分别处理
                optimizeLogicalStatements(tree);
            }
            /**
             * 此时AB为一个AND节点 C为一个OR节点 D为一个OR节点
             * 使用此公式
             * AB+C+D=(A+C+D)(B+C+D)
             */
            //todo 多个树之间怎么合并呢 AB+C+D=(A+C+D)(B+C+D)
            List<RsqlTreeNode> rootTrees = rsqlTreeNode.getChildrenNodes();
            RsqlTreeLeafNode convertLeafNode = new RsqlTreeLeafNode();

            for (RsqlTreeNode tree : rootTrees) {
                if (tree instanceof RsqlTreeLeafNode) {
                    if (((RsqlTreeLeafNode) tree).getAndTreeNodes() == null || ((RsqlTreeLeafNode) tree).getAndTreeNodes().size() == 0) {
                        //convertLeafNode.getQueries().addAll(((TreeLeafNode) tree).getQueries());
                    } else {
                        convertLeafNode.getAndTreeNodes().addAll(((RsqlTreeLeafNode) tree).getAndTreeNodes());
                    }
                    convertLeafNode.getQueries().addAll(((RsqlTreeLeafNode) tree).getQueries());
                } else {
                    if (tree.getChildrenNodes().size() == 1) {
                        convertLeafNode.getQueries().addAll(((RsqlTreeLeafNode) tree.getChildrenNodes().get(0)).getQueries());
                    } else if (tree.getChildrenNodes().size() == 0) {
                        convertLeafNode.getQueries().addAll(((RsqlTreeLeafNode) tree).getQueries());
                    } else {
                        convertLeafNode.getAndTreeNodes().add((RsqlTreeLogicalNode) tree);
                    }
                }
            }
            /**
             * A+B+C+AB= A+B+C
             */
            RsqlTreeLeafNode leafNode = removeLogicRepeatableNode(convertLeafNode);
            rsqlTreeNode.setChildrenNodes(Arrays.asList(leafNode));
            //finish
            return rsqlTreeNode;
        }
        return rsqlTreeNode;
    }

    private Set<RsqlTreeLeafNode> convertLogical2LeafStatements(RsqlTreeNode logicalNode, HashSet result) {
        List<RsqlTreeNode> childrenNodes = logicalNode.getChildrenNodes();
        for (RsqlTreeNode childNode : childrenNodes) {
            if (childNode instanceof RsqlTreeLeafNode) {
                //需要处理leaf里面的逻辑关系
                List<RsqlTreeNode> rsqlTreeNodes = optimizeOrStatements((RsqlTreeLeafNode) childNode);
                removeRepeatableLeafData(rsqlTreeNodes, result);
            } else {
                convertLogical2LeafStatements(childNode, result);
            }
        }
        return result;
    }

    void removeRepeatableLeafData(List<RsqlTreeNode> rsqlTreeNodes, HashSet result) {
        result.addAll(rsqlTreeNodes);
    }

    /**
     * 简化or节点
     * 公式如下
     * A+BC=(A+B)(A+C)
     * A+B+C+AB= A+B+C
     *
     * @param treeNode
     * @return
     */
    List<RsqlTreeNode> optimizeOrStatements(RsqlTreeLeafNode treeNode) {
        Set<Node> orQueries = treeNode.getQueries();
        List<RsqlTreeLogicalNode> andTreeNodes = treeNode.getAndTreeNodes();
        if (CollectionUtils.isEmpty(andTreeNodes)) {
            return Arrays.asList(treeNode);
        }
        List<RsqlTreeNode> treeLeafNodes = new ArrayList<>();
        List<List<Node>> leafNodes = new ArrayList<>();
        for (RsqlTreeLogicalNode node : andTreeNodes) {
            /**
             * 将and节点遍历铺开成为单层
             * 核心在于去重和平层
             * A((BC)AD)=ABCD
             */
            optimizeAndStatements(node);
            //CORE：此处只剩下平铺的leaf节点
            List<RsqlTreeNode> childrenNodes = node.getChildrenNodes();
            List<Node> logicalChildLeafNodes = new ArrayList<>();
            Boolean isMeaning = Boolean.TRUE;
            for (RsqlTreeNode leaf : childrenNodes) {
                //CORE：判断该节点是否需要被删除
                Set<Node> queries = ((RsqlTreeLeafNode) leaf).getQueries();
                if (containsAny(orQueries, queries)) {
                    isMeaning = Boolean.FALSE;
                    continue;
                }
                logicalChildLeafNodes.addAll(new ArrayList(queries));
            }
            if (!isMeaning) {
                continue;
            }
            leafNodes.add(logicalChildLeafNodes);
        }
        leafNodes.add(new ArrayList<>(orQueries));
        //分配法进行运算
        algorithm.composition(leafNodes, new ArrayList(), 0);
        List<List<Node>> result = algorithm.getResult();
        //todo 对result去重可以吗
        for (List<Node> list : result) {
            HashSet<Node> nodes = new HashSet<>();
            nodes.addAll(list);
            RsqlTreeLeafNode newLeafNode = new RsqlTreeLeafNode().setQueries(nodes);
            treeLeafNodes.add(newLeafNode);
        }
        //对叶子节点去重
        return treeLeafNodes;
    }

    /**
     * 将and节点遍历铺开成为单层
     * 核心在于去重
     * A((BC)AD)=ABCD
     */
    void optimizeAndStatements(RsqlTreeLogicalNode treeNode) {
        List<RsqlTreeNode> childrenNodes = treeNode.getChildrenNodes();
        //比较所有的叶子节点
        HashSet<RsqlTreeLeafNode> childrenLeafNodeSet = new HashSet<>();
        for (RsqlTreeNode childNode : childrenNodes) {
            if (childNode instanceof RsqlTreeLogicalNode) {
                //将and节点向上缩
                childrenLeafNodeSet.addAll(convertLogical2LeafStatements(childNode, new HashSet<>()));
            } else {
                /**
                 * 1.叶子节点先处理里面的and
                 * A+BC-> AB+AC
                 * A+BCA-> A
                 */
                List<RsqlTreeNode> treeLeafNodes = optimizeOrStatements((RsqlTreeLeafNode) childNode);
                removeRepeatableLeafData(treeLeafNodes, childrenLeafNodeSet);
            }
        }
        treeNode.setChildrenNodes(new ArrayList<>(childrenLeafNodeSet));
    }

    /**
     * @param treeNode
     * @return
     */
    RsqlTreeLeafNode removeLogicRepeatableNode(RsqlTreeLeafNode treeNode) {
        Set<Node> orQueries = treeNode.getQueries();
        //如果都是and节点会有问题
        List<RsqlTreeLogicalNode> andTreeNodes = treeNode.getAndTreeNodes();
        if (CollectionUtils.isEmpty(andTreeNodes)) {
            return treeNode;
        }

        //判断是否移除所有的AND节点
        if (orQueries.size() == 0 && andTreeNodes.size() > 1) {
            List<List<RsqlTreeLeafNode>> lists = new ArrayList<>();

            for (RsqlTreeLogicalNode node : andTreeNodes) {
                List<RsqlTreeLeafNode> treeLeafNodes = new ArrayList<>();
                List<RsqlTreeNode> childrenNodes = node.getChildrenNodes();
                for (RsqlTreeNode node1 : childrenNodes) {
                    treeLeafNodes.add((RsqlTreeLeafNode) node1);
                }
                lists.add(treeLeafNodes);
            }
            List<List<RsqlTreeNode>> list = algorithm.removeBigger(lists);
            andTreeNodes = new ArrayList<>();
            for (List<RsqlTreeNode> treeLeafNodes : list) {
                if (CollectionUtils.isNotEmpty(treeLeafNodes)) {
                    RsqlTreeLogicalNode logicalNode = new RsqlTreeLogicalNode();
                    logicalNode.setChildrenNodes(treeLeafNodes);
                    andTreeNodes.add(logicalNode);
                }
            }
        } else {
            //对and节点进行去逻辑上的一个删除操作 A+AB+ABC=A
            List<RsqlTreeLogicalNode> result = new ArrayList<>();
            for (RsqlTreeLogicalNode node : andTreeNodes) {
                Boolean judgeRemoveAllAnd = judgeRemoveAnd(node, orQueries);
                if (judgeRemoveAllAnd) {
                    continue;
                }
                result.add(node);
            }
            treeNode.setAndTreeNodes(result);
        }
        //合并所有的andTree
        List<RsqlTreeLogicalNode> result = new ArrayList<>();
        for (RsqlTreeLogicalNode node : treeNode.getAndTreeNodes()) {
            Set<Node> mergeQueries = new HashSet<>();
            //CORE：此处只剩下平铺的leaf节点
            List<RsqlTreeNode> childrenNodes = node.getChildrenNodes();
            for (RsqlTreeNode leaf : childrenNodes) {
                //CORE：判断该节点是否需要被删除
                Set<Node> queries = ((RsqlTreeLeafNode) leaf).getQueries();
                Set<Node> queriesResult = new HashSet<>();
                for (Node queryNode : queries) {
                    HashSet<Node> nodes = new HashSet<>();
                    nodes.add(queryNode);
                    if (containsAny(orQueries, nodes)) {
                        continue;
                    }
                    queriesResult.add(queryNode);
                }
                mergeQueries.addAll(queriesResult);
            }
            RsqlTreeLeafNode leafNode = new RsqlTreeLeafNode().setQueries(mergeQueries);
            RsqlTreeLogicalNode logicalNode = new RsqlTreeLogicalNode();
            logicalNode.setChildrenNodes(Arrays.asList(leafNode));
            result.add(logicalNode);
        }
        result = repeatSameLogicalNode(result);
        result = compareAndNode(result);
        //重构And节点
        //改变原有的And节点
        treeNode.setAndTreeNodes(result);
        return treeNode;
    }

    private List<RsqlTreeLogicalNode> repeatSameLogicalNode(List<RsqlTreeLogicalNode> origins) {
        List<RsqlTreeLogicalNode> result = new ArrayList<>();
        xxx:
        for (RsqlTreeLogicalNode node : origins) {
            if (result.size() == 0) {
                result.add(node);
                continue;
            }
            for (RsqlTreeLogicalNode target : result) {
                //完全相同或者是完全被包含的时候
                Boolean sameLogicalNode = isSameLogicalNode(target, node);
                if (sameLogicalNode) { //
                    continue xxx;
                }
            }
            result.add(node);

        }
        return result;
    }

    Boolean isSameLogicalNode(RsqlTreeLogicalNode origin, RsqlTreeLogicalNode target) {
        if (origin != null && target != null) {
            if (origin.getChildrenNodes().size() == target.getChildrenNodes().size()) {
                List<RsqlTreeNode> originChildrenNodes = origin.getChildrenNodes();
                List<RsqlTreeNode> targetChildrenNodes = target.getChildrenNodes();
                if (originChildrenNodes.containsAll(targetChildrenNodes)) {
                    return Boolean.TRUE;
                }
            }
        }
        return false;
    }

    Boolean containsAny(Set<Node> f, Set<Node> c) {
        Set<Node> result = new HashSet<>();
        result.addAll(c);
        result.retainAll(f);
        return c.equals(f) || result.size() != 0;
    }

    Boolean judgeRemoveAllAnd(List<RsqlTreeLogicalNode> treeLogicalNodes, Set<Node> ors) {
        xx:
        for (RsqlTreeLogicalNode andNode : treeLogicalNodes) {
            Set<Node> singleNodeSet = new HashSet<>();
            if (andNode != null) {
                for (RsqlTreeNode node : andNode.getChildrenNodes()) {
                    Set<Node> queries = ((RsqlTreeLeafNode) node).getQueries();
                    if (queries.size() > 1) {
                        continue;
                    }
                    singleNodeSet.addAll(queries);
                }
                if (containsAny(ors, singleNodeSet)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    Boolean judgeRemoveAnd(RsqlTreeLogicalNode andNode, Set<Node> ors) {
        Set<Node> singleNodeSet = new HashSet<>();
        if (andNode != null) {
            for (RsqlTreeNode node : andNode.getChildrenNodes()) {
                Set<Node> queries = ((RsqlTreeLeafNode) node).getQueries();
                if (queries.size() > 1) {
                    continue;
                }
                singleNodeSet.addAll(queries);
            }
            if (containsAny(ors, singleNodeSet)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * ABC+BC=ABC
     * 多个and节点之间比较
     *
     * @param nodes
     * @return
     */
    List<RsqlTreeLogicalNode> compareAndNode(List<RsqlTreeLogicalNode> nodes) {
        List<RsqlTreeLogicalNode> newR = new ArrayList<>();
        List<List<Node>> lists = new ArrayList<>();
        for (RsqlTreeLogicalNode node : nodes) {
            if (node.getChildrenNodes().size() == 1) {
                RsqlTreeNode rsqlTreeNode = node.getChildrenNodes().get(0);
                Set<Node> queries = ((RsqlTreeLeafNode) rsqlTreeNode).getQueries();
                List<Node> compareList = new ArrayList<>();
                compareList.addAll(queries);
                lists.add(compareList);
            } else {
                newR.add(node);
            }
        }
        List<List<Node>> result = algorithm.removeBigger(lists);
        if (result.size() == nodes.size()) {
            return nodes;
        }
        for (List<Node> r : result) {
            RsqlTreeLogicalNode node = new RsqlTreeLogicalNode();
            RsqlTreeLeafNode leafNode = new RsqlTreeLeafNode();
            leafNode.setQueries(new HashSet<>(r));
            List<RsqlTreeNode> treeLeafNodes = Arrays.asList(leafNode);
            node.setChildrenNodes(treeLeafNodes);
            newR.add(node);
        }
        return newR;
    }

    String buildRsql(RsqlTreeNode root) {
        ArrayList<String> orList = new ArrayList<>();
        ArrayList<String> andList = new ArrayList<>();
        List<RsqlTreeNode> trees = root.getChildrenNodes();
        RsqlTreeNode rsqlTreeNode = trees.get(0);
        if (root instanceof RsqlTreeLogicalNode) {
            List<RsqlTreeNode> childrenNodes = root.getChildrenNodes();
            for (RsqlTreeNode childNode : childrenNodes) {
                Set<Node> queries = ((RsqlTreeLeafNode) childNode).getQueries();
                List<String> list = queries.stream().map(v -> ((ComparisonNode) v).toString()).collect(Collectors.toList());
                if (list.size() > 1) {
                    andList.add("(" + StringUtils.join(list, " or ") + ")");
                } else if (list.size() == 1) {
                    andList.add(list.get(0));
                }
            }

        } else if (trees.size() == 1 && rsqlTreeNode instanceof RsqlTreeLeafNode) {
            //转化以后这里都是Com
            Set<Node> queries = ((RsqlTreeLeafNode) rsqlTreeNode).getQueries();
            for (Node node : queries) {
                orList.add(((ComparisonNode) node).toString());
            }
            List<RsqlTreeLogicalNode> andTreeNodes = ((RsqlTreeLeafNode) rsqlTreeNode).getAndTreeNodes();
            for (RsqlTreeLogicalNode logicalNode : andTreeNodes) {
                Set<Node> orQueries = new HashSet<>();
                logicalNode.getChildrenNodes().forEach(v -> orQueries.addAll(((RsqlTreeLeafNode) v).getQueries()));
                List<String> list = orQueries.stream().map(v -> ((ComparisonNode) v).toString()).collect(Collectors.toList());
                orList.add(StringUtils.join(list, " and "));
            }
        } else {
            //如果是and节点的话 我想不到了
            Set<Node> queries = new HashSet<>();
            trees.forEach(v -> queries.addAll(((RsqlTreeLeafNode) v).getQueries()));
            for (Node node : queries) {
                andList.add(((ComparisonNode) node).toString());
            }
        }
        String and = StringUtils.join(andList, " and ");
        String or = StringUtils.join(orList, " or ");
        if (StringUtils.isNotBlank(and) && StringUtils.isNotBlank(or)) {
            return and + " or " + or;
        }
        return and + or;
    }

}
