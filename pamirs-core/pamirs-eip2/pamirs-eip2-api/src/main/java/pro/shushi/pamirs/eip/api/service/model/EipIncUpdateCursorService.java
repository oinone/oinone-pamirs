package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.model.EipIncUpdateCursor;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

import java.util.Date;

@Fun(EipIncUpdateCursorService.FUN_NAMESPACE)
public interface EipIncUpdateCursorService {

    String FUN_NAMESPACE = "pamirs.eip.EipIncUpdateCursorService";

    @Function
    EipIncUpdateCursor fetchIncUpdateLog(String interfaceName, Date defaultTime);

    @Function
    EipIncUpdateCursor queryByInterfaceName(String interfaceName);
}
