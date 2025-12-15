package pro.shushi.pamirs.resource.api.quickfilling;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceAddress;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverterFactory;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverterRegister;

/**
 * 资源模块快速填报转换注册
 *
 * @author Adamancy Zhang at 11:50 on 2025-11-28
 */
@Component
public class QuickFillingResourceModuleRegister implements QuickFillingConverterRegister {

    @Override
    public void register(QuickFillingConverterFactory factory) {
        factory.registerByReferences(TtypeEnum.M2O, ResourceAddress.MODEL_MODEL, ResourceAddressConverter.class);
    }
}
