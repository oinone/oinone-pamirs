package pro.shushi.pamirs.ux.filling.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.ux.filling.service.QuickFillingService;
import pro.shushi.pamirs.ux.filling.model.QuickFilling;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * @author Gesi at 17:33 on 2025/9/10
 */
@Base
@Component
@Model.model(QuickFilling.MODEL_MODEL)
public class QuickFillingAction {

    @Autowired
    private QuickFillingService quickFillingService;

    @Function.Advanced(displayName = "快速填报加载数据", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = FunctionOpenEnum.API)
    public QuickFilling loadData(QuickFilling quickFilling) {
        return quickFillingService.loadData(quickFilling);
    }

}
