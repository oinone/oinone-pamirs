package pro.shushi.pamirs.eip.api.endpoint.function;

import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.eip.api.camel.IEipRegister;
import pro.shushi.pamirs.eip.api.camel.RegistryComponentBody;

import java.util.List;

/**
 * {@link PamirsFunctionComponent}注册器
 *
 * @author Adamancy Zhang at 17:00 on 2022-03-31
 */
public class PamirsFunctionRegister implements IEipRegister {

    public static String COMPONENT_NAME = "function";

    @Override
    public List<RegistryComponentBody> registers() {
        return CollectionHelper.<RegistryComponentBody>newInstance().add(new RegistryComponentBody(COMPONENT_NAME, new PamirsFunctionComponent())).build();
    }
}
