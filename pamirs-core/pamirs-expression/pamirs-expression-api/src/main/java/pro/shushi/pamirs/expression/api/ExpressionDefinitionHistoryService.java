package pro.shushi.pamirs.expression.api;

import pro.shushi.pamirs.expression.model.ExpressionDHistory;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;

import java.util.List;

/**
 * DesignerFunctionDefinition
 *
 * @author yakir on 2021/08/09 11:38.
 */
@Base
@Fun(ExpressionDefinitionHistoryService.FUN_NAMESPACE)
public interface ExpressionDefinitionHistoryService {

    String FUN_NAMESPACE = "expression.ExpressionDefinitionHistoryService";

    @Function
    List<ExpressionDHistory> createByKeyPrefix(String keyPrefix, String version);

    @Function
    Boolean restoreByKeyPrefix(String keyPrefix, String version);
}
