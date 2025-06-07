//package pro.shushi.pamirs;
//
//import cz.jirutka.rsql.parser.ast.*;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import pro.shushi.pamirs.meta.annotation.fun.Data;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
/// **
// * @author shier
// * date  2021/3/26 3:21 下午
// */
//public class LogicRsqlVisitor implements RSQLVisitor<LogicRsqlVisitor.TreeNode, LogicRsqlVisitor.TreeNode> {
//    private AlgorithmTest algorithm = new AlgorithmTest();
//
//    @Override
//    public TreeNode visit(AndNode andNode, TreeNode s) {
//        return handleLogicNode(andNode, s);
//    }
//
//    @Override
//    public TreeNode visit(OrNode orNode, TreeNode s) {
//        return handleLogicNode(orNode, s);
//    }
//
//    @Override
//    public TreeNode visit(ComparisonNode comparisonNode, TreeNode s) {
//        TreeLeafNode treeLeafNode = new TreeLeafNode();
//        if (s == null) {
//            //说明只有这一个节点
//            Set<Node> comparisonNodes = new HashSet<>();
//            comparisonNodes.add(comparisonNode);
//            TreeLeafNode leafNode = new TreeLeafNode();
//            leafNode.setQueries(comparisonNodes);
//            return leafNode;
//        }
//        if (s instanceof TreeLeafNode) {
//            Set<Node> queries = ((TreeLeafNode) s).getQueries();
//            if (queries == null) {
//                ((TreeLeafNode) s).setQueries(new HashSet<>());
//            }
//            ((TreeLeafNode) s).getQueries().add(comparisonNode);
//
//            treeLeafNode.setQueries(queries);
//            return treeLeafNode;
//        } else if (s instanceof TreeLogicalNode) {
//            TreeLeafNode simpleTreeLeafNode = new TreeLeafNode();
//            simpleTreeLeafNode.getQueries().add(comparisonNode);
//            return simpleTreeLeafNode;
//        }
//        return null;
//    }
//
//    TreeNode handleLogicNode(LogicalNode node, TreeNode s) {
//        Boolean isAnd = node instanceof AndNode;
//        Boolean isRoot = Boolean.FALSE;
//        if (s == null) {
//            //构建出一个空的根root节点
//            s = new TreeNode();//根节点
//            isRoot = Boolean.TRUE;
//        }
//        List<Node> children = node.getChildren();
//        for (Node child : children) {
//            TreeNode childNode;
//            if (child instanceof OrNode) {
//                TreeLeafNode currentNode = new TreeLeafNode();
//                childNode = visit((OrNode) child, currentNode);
//            } else if (child instanceof AndNode) {
//                childNode = visit((AndNode) child, new TreeLogicalNode());
//            } else {
//                if (isRoot) {
//                    childNode = visit((ComparisonNode) child, new TreeLeafNode());
//                } else {
//                    childNode = visit((ComparisonNode) child, s);
//                }
//            }
//            if (isRoot) {
//                if (isAnd) {
//                    if (CollectionUtils.isEmpty(s.getChildrenNodes())) {
//                        TreeLogicalNode firstNode = new TreeLogicalNode();
//                        firstNode.getChildrenNodes().add(childNode);
//                        s.setChildrenNodes(Arrays.asList(firstNode));
//                    } else {
//                        TreeLogicalNode logicalNode = (TreeLogicalNode) s.getChildrenNodes().get(0);
//                        logicalNode.getChildrenNodes().add(childNode);
//                    }
//                } else {
//                    s.getChildrenNodes().add(childNode);
//                }
//            } else if (isAnd) {
//                ((TreeLogicalNode) s).getChildrenNodes().add(childNode);
//            } else if (!isAnd && child instanceof AndNode) {
//                ((TreeLeafNode) s).getAndTreeNodes().add((TreeLogicalNode) childNode);
//            }
//          /*  if (isRoot && !isAnd) {
//                ((TreeNode) s).getChildrenNodes().add(childNode);
//            } else if (isAnd) {
//                ((TreeLogicalNode) s).getChildrenNodes().add(childNode);
//            }*/
//        }
//        return s;
//    }
//
//    @Data
//    class TreeLogicalNode extends TreeNode {
//
//    }
//
//    @Data
//    class TreeLeafNode extends TreeNode {
//
//        Set<Node> queries = new HashSet<>();
//
//        List<TreeLogicalNode> andTreeNodes = new ArrayList<>();
//
//        String operator;
//
//        @Override
//        public boolean equals(Object obj) {
//            TreeLeafNode realObj = (TreeLeafNode) obj;
//            if (realObj.getOperator() != null && realObj.getOperator() == this.getOperator()) {
//                //暂时不加入operator的计算
//            }
//            if (realObj.getQueries().size() != this.getQueries().size()) {
//                return false;
//            }
//            return this.queries.containsAll(realObj.getQueries());
//        }
//
//        @Override
//        public int hashCode() {
//            int result = 0;
//            if (this.operator != null) {
//                result = 31 * this.operator.hashCode();
//            }
//            for (Node node : this.getQueries()) {
//                result = result + node.hashCode();
//            }
//            return result;
//        }
//    }
//
//    @Data
//    class TreeNode {
//        List<TreeNode> childrenNodes = new ArrayList<>();
//    }
//
//    //优化逻辑语句
//    String optimizeLogicalStatementRsql(TreeNode treeNode) {
//        return buildRsql(optimizeLogicalStatements(treeNode));
//    }
//
//    TreeNode optimizeLogicalStatements(TreeNode treeNode) {
//        if (treeNode instanceof TreeLogicalNode) {
//            optimizeAndStatements((TreeLogicalNode) treeNode);
//        } else if (treeNode instanceof TreeLeafNode) {
//            //另一颗树
//            //将当前的叶子节点拆分成为多个Logical节点
//            List<TreeNode> treeLeafNodes = optimizeOrStatements((TreeLeafNode) treeNode);
//            treeNode = new TreeLogicalNode().setChildrenNodes(treeLeafNodes);
//            optimizeAndStatements((TreeLogicalNode) treeNode);
//        } else if (treeNode instanceof TreeNode) {
//            List<TreeNode> childrenNodes = treeNode.getChildrenNodes();
//            if (childrenNodes.size() == 1) {
//                //说明这是一棵树
//                TreeNode treeNode2 = childrenNodes.get(0);
//                optimizeLogicalStatements(treeNode2);
//                List<TreeNode> childrenNodes1 = treeNode2.getChildrenNodes();
//                //去重复
//                List<List<Node>> lists = new ArrayList<>();
//                for (TreeNode node : childrenNodes1) {
//                    ArrayList<Node> nodes = new ArrayList<>(((TreeLeafNode) node).getQueries());
//                    lists.add(nodes);
//                }
//                List<List<Node>> list = algorithm.removeBigger(lists);
//                List<TreeNode> treeLeafNodes = new ArrayList<>();
//                for (List<Node> l : list) {
//                    TreeLeafNode leafNode = new TreeLeafNode();
//                    HashSet<Node> nodes = new HashSet<>();
//                    nodes.addAll(l);
//                    leafNode.setQueries(nodes);
//                    treeLeafNodes.add(leafNode);
//                }
//                treeNode2.setChildrenNodes(treeLeafNodes);
//                return treeNode2;
//            }
//            //这是多棵树，最终的结果需要合并
//            for (TreeNode tree : treeNode.getChildrenNodes()) {
//                //分别处理
//                optimizeLogicalStatements(tree);
//            }
//            /**
//             * 此时AB为一个AND节点 C为一个OR节点 D为一个OR节点
//             * 使用此公式
//             * AB+C+D=(A+C+D)(B+C+D)
//             */
//            //todo 多个树之间怎么合并呢 AB+C+D=(A+C+D)(B+C+D)
//            List<TreeNode> rootTrees = treeNode.getChildrenNodes();
//            TreeLeafNode convertLeafNode = new TreeLeafNode();
//
//            for (TreeNode tree : rootTrees) {
//                if (tree instanceof TreeLeafNode) {
//                    if (((TreeLeafNode) tree).getAndTreeNodes() == null || ((TreeLeafNode) tree).getAndTreeNodes().size() == 0) {
//                        //convertLeafNode.getQueries().addAll(((TreeLeafNode) tree).getQueries());
//                    } else {
//                        convertLeafNode.getAndTreeNodes().addAll(((TreeLeafNode) tree).getAndTreeNodes());
//                    }
//                    convertLeafNode.getQueries().addAll(((TreeLeafNode) tree).getQueries());
//                } else {
//                    if (tree.getChildrenNodes().size() == 1) {
//                        convertLeafNode.getQueries().addAll(((TreeLeafNode) tree.getChildrenNodes().get(0)).getQueries());
//                    } else if (tree.getChildrenNodes().size() == 0) {
//                        convertLeafNode.getQueries().addAll(((TreeLeafNode) tree).getQueries());
//                    } else {
//                        //不能直接加
//                        convertLeafNode.getAndTreeNodes().add((TreeLogicalNode) tree);
//                    }
//                }
//            }
//            /**
//             * A+B+C+AB= A+B+C
//             */
//            TreeLeafNode leafNode = removeLogicRepeatableNode(convertLeafNode);
//            treeNode.setChildrenNodes(Arrays.asList(leafNode));
//            //finish
//            //todo 转化回到RSQL 怎么办啊啊啊
//            return treeNode;
//        }
//        return treeNode;
//    }
//
//    private Set<TreeLeafNode> convertLogical2LeafStatements(TreeNode logicalNode, HashSet result) {
//        List<TreeNode> childrenNodes = logicalNode.getChildrenNodes();
//        for (TreeNode childNode : childrenNodes) {
//            if (childNode instanceof TreeLeafNode) {
//                //需要处理leaf里面的逻辑关系
//                List<TreeNode> treeNodes = optimizeOrStatements((TreeLeafNode) childNode);
//                removeRepeatableLeafData(treeNodes, result);
//            } else {
//                convertLogical2LeafStatements(childNode, result);
//            }
//        }
//        return result;
//    }
//
//    void removeRepeatableLeafData(List<TreeNode> treeNodes, HashSet result) {
//        result.addAll(treeNodes);
//    }
//
//    /**
//     * 简化or节点
//     * 公式如下
//     * A+BC=(A+B)(A+C)
//     * A+B+C+AB= A+B+C
//     *
//     * @param treeNode
//     * @return
//     */
//    List<TreeNode> optimizeOrStatements(TreeLeafNode treeNode) {
//        Set<Node> orQueries = treeNode.getQueries();
//        List<TreeLogicalNode> andTreeNodes = treeNode.getAndTreeNodes();
//        if (CollectionUtils.isEmpty(andTreeNodes)) {
//            return Arrays.asList(treeNode);
//        }
//        List<TreeNode> treeLeafNodes = new ArrayList<>();
//        List<List<Node>> leafNodes = new ArrayList<>();
//        for (TreeLogicalNode node : andTreeNodes) {
//            /**
//             * 将and节点遍历铺开成为单层
//             * 核心在于去重和平层
//             * A((BC)AD)=ABCD
//             */
//            optimizeAndStatements(node);
//            //CORE：此处只剩下平铺的leaf节点
//            List<TreeNode> childrenNodes = node.getChildrenNodes();
//            List<Node> logicalChildLeafNodes = new ArrayList<>();
//            Boolean isMeaning = Boolean.TRUE;
//            for (TreeNode leaf : childrenNodes) {
//                //CORE：判断该节点是否需要被删除
//                Set<Node> queries = ((TreeLeafNode) leaf).getQueries();
//                if (containsAny(orQueries, queries)) {
//                    isMeaning = Boolean.FALSE;
//                    continue;
//                }
//                logicalChildLeafNodes.addAll(new ArrayList(queries));
//            }
//            if (!isMeaning) {
//                continue;
//            }
//            leafNodes.add(logicalChildLeafNodes);
//        }
//        leafNodes.add(new ArrayList<>(orQueries));
//        //分配法进行运算
//        algorithm.composition(leafNodes, new ArrayList(), 0);
//        List<List<Node>> result = algorithm.getResult();
//        //todo 对result去重可以吗
//        for (List<Node> list : result) {
//            HashSet<Node> nodes = new HashSet<>();
//            nodes.addAll(list);
//            TreeLeafNode newLeafNode = new TreeLeafNode().setQueries(nodes);
//            treeLeafNodes.add(newLeafNode);
//        }
//        //对叶子节点去重
//        return treeLeafNodes;
//    }
//
//    /**
//     * 将and节点遍历铺开成为单层
//     * 核心在于去重
//     * A((BC)AD)=ABCD
//     */
//    void optimizeAndStatements(TreeLogicalNode treeNode) {
//        List<TreeNode> childrenNodes = treeNode.getChildrenNodes();
//        //比较所有的叶子节点
//        HashSet<TreeLeafNode> childrenLeafNodeSet = new HashSet<>();
//        for (TreeNode childNode : childrenNodes) {
//            if (childNode instanceof TreeLogicalNode) {
//                //将and节点向上缩
//                childrenLeafNodeSet.addAll(convertLogical2LeafStatements(childNode, new HashSet<>()));
//            } else {
//                /**
//                 * 1.叶子节点先处理里面的and
//                 * A+BC-> AB+AC
//                 * A+BCA-> A
//                 */
//                List<TreeNode> treeLeafNodes = optimizeOrStatements((TreeLeafNode) childNode);
//                removeRepeatableLeafData(treeLeafNodes, childrenLeafNodeSet);
//                //convertLeaf2LeafStatements(childNode, childrenLeafNodeSet);
//            }
//        }
//        treeNode.setChildrenNodes(new ArrayList<>(childrenLeafNodeSet));
//    }
//
//    /**
//     * @param treeNode
//     * @return
//     */
//    TreeLeafNode removeLogicRepeatableNode(TreeLeafNode treeNode) {
//        Set<Node> orQueries = treeNode.getQueries();
//        //如果都是and节点会有问题
//        List<TreeLogicalNode> andTreeNodes = treeNode.getAndTreeNodes();
//        if (CollectionUtils.isEmpty(andTreeNodes)) {
//            return treeNode;
//        }
//
//        //判断是否移除所有的AND节点
//        if (orQueries.size() == 0 && andTreeNodes.size() > 1) {
//            List<List<TreeLeafNode>> lists = new ArrayList<>();
//
//            for (TreeLogicalNode node : andTreeNodes) {
//                List<TreeLeafNode> treeLeafNodes = new ArrayList<>();
//                List<TreeNode> childrenNodes = node.getChildrenNodes();
//                for (TreeNode node1 : childrenNodes) {
//                    treeLeafNodes.add((TreeLeafNode) node1);
//                }
//                lists.add(treeLeafNodes);
//            }
//            List<List<TreeNode>> list = algorithm.removeBigger(lists);
//            andTreeNodes = new ArrayList<>();
//            for (List<TreeNode> treeLeafNodes : list) {
//                if (CollectionUtils.isNotEmpty(treeLeafNodes)) {
//                    TreeLogicalNode logicalNode = new TreeLogicalNode();
//                    logicalNode.setChildrenNodes(treeLeafNodes);
//                    andTreeNodes.add(logicalNode);
//                }
//            }
//        } else {
//            //对and节点进行去逻辑上的一个删除操作 A+AB+ABC=A
//            List<TreeLogicalNode> result = new ArrayList<>();
//            for (TreeLogicalNode node : andTreeNodes) {
//                Boolean judgeRemoveAllAnd = judgeRemoveAnd(node, orQueries);
//                if (judgeRemoveAllAnd) {
//                    continue;
//                }
//                result.add(node);
//            }
//            treeNode.setAndTreeNodes(result);
//        }
//        //合并所有的andTree
//        List<TreeLogicalNode> result = new ArrayList<>();
//        for (TreeLogicalNode node : treeNode.getAndTreeNodes()) {
//            Set<Node> mergeQueries = new HashSet<>();
//            //CORE：此处只剩下平铺的leaf节点
//            List<TreeNode> childrenNodes = node.getChildrenNodes();
//            for (TreeNode leaf : childrenNodes) {
//                //CORE：判断该节点是否需要被删除
//                Set<Node> queries = ((TreeLeafNode) leaf).getQueries();
//                Set<Node> queriesResult = new HashSet<>();
//                for (Node queryNode : queries) {
//                    HashSet<Node> nodes = new HashSet<>();
//                    nodes.add(queryNode);
//                    if (containsAny(orQueries, nodes)) {
//                        continue;
//                    }
//                    queriesResult.add(queryNode);
//                }
//                mergeQueries.addAll(queriesResult);
//            }
//            TreeLeafNode leafNode = new TreeLeafNode().setQueries(mergeQueries);
//            TreeLogicalNode logicalNode = new TreeLogicalNode();
//            logicalNode.setChildrenNodes(Arrays.asList(leafNode));
//            result.add(logicalNode);
//        }
//        result = repeatSameLogicalNode(result);
//        result = compareAndNode(result);
//        //重构And节点
//        //改变原有的And节点
//        treeNode.setAndTreeNodes(result);
//        return treeNode;
//    }
//
//    private List<TreeLogicalNode> repeatSameLogicalNode(List<TreeLogicalNode> origins) {
//        List<TreeLogicalNode> result = new ArrayList<>();
//        xxx:
//        for (TreeLogicalNode node : origins) {
//            if (result.size() == 0) {
//                result.add(node);
//                continue;
//            }
//            for (TreeLogicalNode target : result) {
//                //完全相同或者是完全被包含的时候
//                Boolean sameLogicalNode = isSameLogicalNode(target, node);
//                if (sameLogicalNode) { //
//                    continue xxx;
//                }
//            }
//            result.add(node);
//
//        }
//        return result;
//    }
//
//    Boolean isSameLogicalNode(TreeLogicalNode origin, TreeLogicalNode target) {
//        if (origin != null && target != null) {
//            if (origin.getChildrenNodes().size() == target.getChildrenNodes().size()) {
//                List<TreeNode> originChildrenNodes = origin.getChildrenNodes();
//                List<TreeNode> targetChildrenNodes = target.getChildrenNodes();
//                if (originChildrenNodes.containsAll(targetChildrenNodes)) {
//                    return Boolean.TRUE;
//                }
//            }
//        }
//        return false;
//    }
//
//    Boolean containsAny(Set<Node> f, Set<Node> c) {
//        Set<Node> result = new HashSet<>();
//        result.addAll(c);
//        result.retainAll(f);
//        return c.equals(f) || result.size() != 0;
//    }
//
//    Boolean judgeRemoveAllAnd(List<TreeLogicalNode> treeLogicalNodes, Set<Node> ors) {
//        xx:
//        for (TreeLogicalNode andNode : treeLogicalNodes) {
//            Set<Node> singleNodeSet = new HashSet<>();
//            if (andNode != null) {
//                for (TreeNode node : andNode.getChildrenNodes()) {
//                    Set<Node> queries = ((TreeLeafNode) node).getQueries();
//                    if (queries.size() > 1) {
//                        //break xx;
//                        continue;
//                    }
//                    singleNodeSet.addAll(queries);
//                }
//                if (containsAny(ors, singleNodeSet)) {
//                    return Boolean.TRUE;
//                }
//            }
//        }
//        return Boolean.FALSE;
//    }
//
//    Boolean judgeRemoveAnd(TreeLogicalNode andNode, Set<Node> ors) {
//        Set<Node> singleNodeSet = new HashSet<>();
//        if (andNode != null) {
//            for (TreeNode node : andNode.getChildrenNodes()) {
//                Set<Node> queries = ((TreeLeafNode) node).getQueries();
//                if (queries.size() > 1) {
//                    //break xx;
//                    continue;
//                }
//                singleNodeSet.addAll(queries);
//            }
//            if (containsAny(ors, singleNodeSet)) {
//                return Boolean.TRUE;
//            }
//        }
//        return Boolean.FALSE;
//    }
//
//    /**
//     * ABC+BC=ABC
//     *
//     * @param nodes
//     * @return
//     */
//    //多个and节点之间比较
//    List<TreeLogicalNode> compareAndNode(List<TreeLogicalNode> nodes) {
//        //List<TreeLogicalNode> result=new ArrayList<>();
//
//        List<TreeLogicalNode> newR = new ArrayList<>();
//        List<List<Node>> lists = new ArrayList<>();
//        for (TreeLogicalNode node : nodes) {
//            if (node.getChildrenNodes().size() == 1) {
//                TreeNode treeNode = node.getChildrenNodes().get(0);
//                Set<Node> queries = ((TreeLeafNode) treeNode).getQueries();
//                ArrayList<Node> list1 = new ArrayList<>();
//                list1.addAll(queries);
//                lists.add(list1);
//            } else {
//                newR.add(node);
//            }
//        }
//        List<List<Node>> result = algorithm.removeBigger(lists);
//        if (result.size() == nodes.size()) {
//            return nodes;
//        }
//        for (List<Node> r : result) {
//            TreeLogicalNode node = new TreeLogicalNode();
//            TreeLeafNode leafNode = new TreeLeafNode();
//            leafNode.setQueries(new HashSet<>(r));
//            List<TreeNode> treeLeafNodes = Arrays.asList(leafNode);
//            node.setChildrenNodes(treeLeafNodes);
//            newR.add(node);
//        }
//        return newR;
//    }
//
//    String buildRsql(TreeNode root) {
//        StringBuffer stringBuffer = new StringBuffer();
//        ArrayList<String> orList = new ArrayList<>();
//        ArrayList<String> andList = new ArrayList<>();
//        List<TreeNode> trees = root.getChildrenNodes();
//        TreeNode treeNode = trees.get(0);
//        if (root instanceof TreeLogicalNode) {
//            List<TreeNode> childrenNodes = root.getChildrenNodes();
//            for (TreeNode childNode : childrenNodes) {
//                Set<Node> queries = ((TreeLeafNode) childNode).getQueries();
//                List<String> list = queries.stream().map(v -> ((ComparisonNode) v).toString()).collect(Collectors.toList());
//                if (list.size() > 1) {
//                    andList.add("(" + StringUtils.join(list, " or ") + ")");
//                } else if (list.size() == 1) {
//                    andList.add(list.get(0));
//                }
//            }
//
//        } else if (trees.size() == 1 && treeNode instanceof TreeLeafNode) {
//            //转化以后这里都是Com
//            Set<Node> queries = ((TreeLeafNode) treeNode).getQueries();
//            for (Node node : queries) {
//                orList.add(((ComparisonNode) node).toString());
//            }
//            Set<Node> andQueries = new HashSet<>();
//            List<TreeLogicalNode> andTreeNodes = ((TreeLeafNode) treeNode).getAndTreeNodes();
//            for (TreeLogicalNode logicalNode : andTreeNodes) {
//                Set<Node> orQueries = new HashSet<>();
//                logicalNode.getChildrenNodes().forEach(v -> orQueries.addAll(((TreeLeafNode) v).getQueries()));
//                List<String> list = orQueries.stream().map(v -> ((ComparisonNode) v).toString()).collect(Collectors.toList());
//                orList.add(StringUtils.join(list, " and "));
//            }
//        } else {
//            //如果是and节点的话 我想不到了
//            Set<Node> queries = new HashSet<>();
//            trees.forEach(v -> queries.addAll(((TreeLeafNode) v).getQueries()));
//            for (Node node : queries) {
//                andList.add(((ComparisonNode) node).toString());
//            }
//        }
//        String and = StringUtils.join(andList, " and ");
//        String or = StringUtils.join(orList, " or ");
//        if (StringUtils.isNotBlank(and) && StringUtils.isNotBlank(or)) {
//            return and + " or " + or;
//        }
//        return and + or;
//    }
//
//}
