package pro.shushi.pamirs.eip.api.processor;

import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.IEipProcessor;

public abstract class AbstractOpenInterfaceProcessor<T> extends AbstractProcessor<IEipOpenInterface<T>> implements IEipProcessor<IEipOpenInterface<T>> {

    public AbstractOpenInterfaceProcessor(IEipOpenInterface<T> openInterface) {
        super(openInterface);
    }
}
