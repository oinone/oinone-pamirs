package pro.shushi.pamirs.framework.gateways.rsql.algorithm;

import cz.jirutka.rsql.parser.ast.Node;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RsqlTreeLeafNode extends RsqlTreeNode {

    Set<Node> queries = new HashSet<>();

    List<RsqlTreeLogicalNode> andTreeNodes = new ArrayList<>();

    /**
     * 比较规则是queries是否是完全一致
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        RsqlTreeLeafNode realObj = (RsqlTreeLeafNode) obj;
        if (realObj.getQueries().size() != this.getQueries().size()) {
            return false;
        }
        return this.queries.containsAll(realObj.getQueries());
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Node node : this.getQueries()) {
            result = result + node.hashCode();
        }
        return result;
    }
}