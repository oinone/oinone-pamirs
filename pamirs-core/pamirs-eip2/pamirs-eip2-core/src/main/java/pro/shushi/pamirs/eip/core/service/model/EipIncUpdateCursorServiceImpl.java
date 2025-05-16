package pro.shushi.pamirs.eip.core.service.model;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.EipIncUpdateCursor;
import pro.shushi.pamirs.eip.api.service.model.EipIncUpdateCursorService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@Fun(EipIncUpdateCursorService.FUN_NAMESPACE)
public class EipIncUpdateCursorServiceImpl implements EipIncUpdateCursorService {

    @Function
    @Override
    public EipIncUpdateCursor fetchIncUpdateLog(String interfaceName, Date defaultTime) {
        EipIncUpdateCursor incUpdateLog = Optional
                .ofNullable(queryByInterfaceName(interfaceName))
                .orElse(new EipIncUpdateCursor());
        if (null == incUpdateLog.getStartTime()) {
            if (null == defaultTime) {
                //不指定就当前时间吧
                defaultTime = new DateTime().toDate();
            }
            // 设置&创建默认Log
            incUpdateLog.setInterfaceName(interfaceName)
                    .setStartTime(defaultTime)
                    .setEndTime(new DateTime().toDate())
                    .createOrUpdate();
        }
        if (null == incUpdateLog.getEndTime()) {
            incUpdateLog.setEndTime(new DateTime().toDate());
        }
        return incUpdateLog;
    }

    @Function
    @Override
    public EipIncUpdateCursor queryByInterfaceName(String interfaceName) {
        return new EipIncUpdateCursor().queryOneByWrapper(Pops.<EipIncUpdateCursor>lambdaQuery()
                .from(EipIncUpdateCursor.MODEL_MODEL)
                .eq(EipIncUpdateCursor::getInterfaceName, interfaceName));
    }

}
