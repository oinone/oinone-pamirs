package pro.shushi.pamirs.core.common.business.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.business.spi.ControlShowPartnersApi;
import pro.shushi.pamirs.core.common.business.tmodel.CurrentPartner;
import pro.shushi.pamirs.core.common.business.tmodel.ShowPartners;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

@Component
@Model.model(ShowPartners.MODEL_MODEL)
public class ShowPartnersAction {

    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API}, summary = "展示所有公司")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public ShowPartners queryAllPartners() {
        return Spider.getDefaultExtension(ControlShowPartnersApi.class).queryAllPartners();
    }

    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API}, summary = "切换公司")
    @Function.Advanced(type = FunctionTypeEnum.UPDATE)
    public ShowPartners changePartner(CurrentPartner currentPartner) {
        return Spider.getDefaultExtension(ControlShowPartnersApi.class).changePartner(currentPartner);
    }
}
