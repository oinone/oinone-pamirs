package pro.shushi.pamirs.framework.gateways.rsql.algorithm;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RsqlTreeNode {

    List<RsqlTreeNode> childrenNodes = new ArrayList<>();
}