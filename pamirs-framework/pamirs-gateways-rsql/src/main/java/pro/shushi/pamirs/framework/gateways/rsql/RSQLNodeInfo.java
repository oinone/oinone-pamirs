package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.List;

/**
 * RSQL节点信息
 *
 * @author Adamancy Zhang at 09:46 on 2021-10-21
 */
public class RSQLNodeInfo {

    private final ModelConfig modelConfig;

    private RSQLNodeInfoType type;

    private ModelFieldConfig modelFieldConfig;

    private String field;

    private ComparisonOperator operator;

    private List<String> arguments;

    public RSQLNodeInfo(RSQLNodeInfoType type) {
        this(type, null, null, null);
    }

    public RSQLNodeInfo(RSQLNodeInfoType type, ModelConfig modelConfig, ModelFieldConfig modelFieldConfig, ComparisonNode comparisonNode) {
        this.type = type;
        this.modelConfig = modelConfig;
        this.modelFieldConfig = modelFieldConfig;
        if (comparisonNode == null) {
            this.field = null;
            this.operator = null;
            this.arguments = null;
        } else {
            this.field = comparisonNode.getSelector();
            this.operator = comparisonNode.getOperator();
            this.arguments = comparisonNode.getArguments();
        }
    }

    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    public RSQLNodeInfoType getType() {
        return type;
    }

    public void setType(RSQLNodeInfoType type) {
        this.type = type;
    }

    public ModelFieldConfig getModelFieldConfig() {
        return modelFieldConfig;
    }

    public void setModelFieldConfig(ModelFieldConfig modelFieldConfig) {
        this.modelFieldConfig = modelFieldConfig;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public RSQLNodeInfo clone() {
        RSQLNodeInfo newInfo = new RSQLNodeInfo(this.getType(), this.getModelConfig(), this.getModelFieldConfig(), null);
        newInfo.setField(this.getField());
        newInfo.setOperator(this.getOperator());
        newInfo.setArguments(this.getArguments());
        return newInfo;
    }
}
