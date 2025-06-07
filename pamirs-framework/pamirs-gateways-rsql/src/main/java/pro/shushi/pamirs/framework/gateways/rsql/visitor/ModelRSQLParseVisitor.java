package pro.shushi.pamirs.framework.gateways.rsql.visitor;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * 模型RSQL解析
 *
 * @author Adamancy Zhang at 09:43 on 2021-10-21
 */
public class ModelRSQLParseVisitor extends AbstractRSQLParseVisitor<ModelConfig, RSQLNodeInfo> implements RSQLVisitor<TreeNode<RSQLNodeInfo>, ModelConfig> {

    @Override
    protected RSQLNodeInfo generatorNodeInfo(RSQLNodeInfoType type, ComparisonNode node, ModelConfig modelConfig) {
        RSQLNodeInfo newNodeInfo;
        if (node == null) {
            newNodeInfo = new RSQLNodeInfo(type);
        } else {
            String field = node.getSelector();
            if (StringUtils.isBlank(field)) {
                throw new IllegalArgumentException("Invalid model field.");
            }
            newNodeInfo = new RSQLNodeInfo(type, modelConfig, PamirsSession.getContext().getModelField(modelConfig.getModel(), field), node);
        }
        return newNodeInfo;
    }
}
