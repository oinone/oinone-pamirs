package pro.shushi.pamirs.framework.connectors.data.mapper.method.api;

/**
 * @author Adamancy Zhang at 12:18 on 2023-06-26
 */
public interface UpdateMethod extends SelectMethod {

    UpdateMethod setJudgeEntityNull(boolean judgeEntityNull);

    UpdateMethod setWrapperSet(boolean wrapperSet);

    UpdateMethod setValuePrefix(String valuePrefix);

    UpdateMethod setBatch(boolean batch);

    String sqlSet();
}
