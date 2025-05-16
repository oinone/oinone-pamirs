package pro.shushi.pamirs.meta.api.core.compute.context;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.CheckStrategyEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

/**
 * 计算上下文
 * <p>
 * 2022/1/13 1:25 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ComputeContext {

    private CheckStrategyEnum checkStrategy;

    private InformationLevelEnum msgLevel;

    private boolean checkField;

    private ComputeContext() {

    }

    public static ComputeContext init() {
        return new ComputeContext()
                .setCheckStrategy(CheckStrategyEnum.RETURN_WHEN_COMPLETED)
                .setMsgLevel(InformationLevelEnum.ERROR);
    }

    public static ComputeContext requestInit() {
        CheckStrategyEnum checkStrategy = PamirsSession.getRequestVariables().getRequestInfo().getCheckStrategy();
        InformationLevelEnum msgLevel = PamirsSession.getRequestVariables().getRequestInfo().getMsgLevel();
        return new ComputeContext().setCheckStrategy(checkStrategy).setMsgLevel(msgLevel);
    }

    public boolean returnWhenError() {
        return !CheckStrategyEnum.RETURN_WHEN_COMPLETED.equals(getCheckStrategy());
    }

}
