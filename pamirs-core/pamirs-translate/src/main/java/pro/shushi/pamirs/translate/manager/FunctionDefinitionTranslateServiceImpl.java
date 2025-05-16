package pro.shushi.pamirs.translate.manager;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService;

import java.util.Set;

/**
 * @author xzf 2022/12/16 21:49
 **/
@Component
public class FunctionDefinitionTranslateServiceImpl implements TranslateMetaBaseService<FunctionDefinition> {

    @Override
    public Set<String> modelType() {
        return Sets.newHashSet(
                FunctionDefinition.MODEL_MODEL,
                "data.audit.DataAuditFunction",
                "designer.DesignerFunctionDefinition",
                "workflow.WorkflowCustomFun",
                "workflow.WorkflowPersonFun",
                "workflow.WorkflowFun"
        );
    }

    @Override
    public String initModelType() {
        return FunctionDefinition.MODEL_MODEL;
    }
}
