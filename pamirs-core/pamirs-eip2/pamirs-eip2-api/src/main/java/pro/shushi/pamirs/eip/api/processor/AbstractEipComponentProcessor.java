package pro.shushi.pamirs.eip.api.processor;

import pro.shushi.pamirs.eip.api.IEipProcessor;
import pro.shushi.pamirs.eip.api.model.EipComponentDefinition;
import pro.shushi.pamirs.eip.api.model.EipRouteDefinition;

@SuppressWarnings({"rawtypes"})
public abstract class AbstractEipComponentProcessor extends AbstractProcessor<EipRouteDefinition> implements IEipProcessor<EipRouteDefinition> {

    protected EipComponentDefinition eipComponentDefinition;

    public AbstractEipComponentProcessor(EipRouteDefinition routeDefinition, EipComponentDefinition eipComponentDefinition) {
        super(routeDefinition);
        this.eipComponentDefinition = eipComponentDefinition;
    }
}
