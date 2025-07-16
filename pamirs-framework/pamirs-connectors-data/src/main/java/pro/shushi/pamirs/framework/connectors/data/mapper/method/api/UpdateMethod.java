package pro.shushi.pamirs.framework.connectors.data.mapper.method.api;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import pro.shushi.pamirs.framework.connectors.data.constant.StatementConstants;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.ScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.SqlTemplate;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * @author Adamancy Zhang at 12:18 on 2023-06-26
 */
public interface UpdateMethod extends SelectMethod {

    UpdateMethod setJudgeEntityNull(boolean judgeEntityNull);

    UpdateMethod setWrapperSet(boolean wrapperSet);

    UpdateMethod setValuePrefix(String valuePrefix);

    UpdateMethod setBatch(boolean batch);

    boolean isBatch();

    default String update() {
        return String.format(ScriptTemplate.SCRIPT, String.format(SqlTemplate.UPDATE, table(), sqlSet(), sqlSegment()));
    }

    default String updateByUniqueKey() {
        String singleSql = String.format(SqlTemplate.UPDATE, table(), sqlSet(), sqlUnique());
        if (isBatch()) {
            return String.format(ScriptTemplate.SCRIPT, SqlScriptUtils.convertForeach(singleSql,
                    Constants.COLLECTION, StatementConstants.INDEX,
                    Constants.ENTITY, CharacterConstants.SEPARATOR_SEMICOLON));
        } else {
            return String.format(ScriptTemplate.SCRIPT, singleSql);
        }
    }

    default String updateByPk() {
        return String.format(ScriptTemplate.SCRIPT, String.format(SqlTemplate.UPDATE, table(), sqlSet(), sqlPk()));
    }

    default String updateByPks() {
        if (isUseOptimisticLocker()) {
            String singleSql = String.format(SqlTemplate.UPDATE, table(), sqlSet(), sqlPk());
            return String.format(ScriptTemplate.SCRIPT, SqlScriptUtils.convertForeach(singleSql,
                    StatementConstants.CONDITION_COLLECTION, StatementConstants.INDEX,
                    StatementConstants.ITEM, CharacterConstants.SEPARATOR_SEMICOLON));
        } else {
            setOptimisticLockerPrefix(Constants.ENTITY_DOT);
            return String.format(ScriptTemplate.SCRIPT, String.format(SqlTemplate.UPDATE, table(), sqlSet(), sqlPks()));
        }
    }

    String sqlSet();
}
