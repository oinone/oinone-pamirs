package pro.shushi.pamirs.eip.view.manager.edit;

import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;


public interface EipInterfaceEditPreparer<R> {

    public R prepare(EipIntegrationInterfaceEdit interfaceEdit,EipInterfaceEditPrepareChain<R> prepareChain);
    public R construct(EipIntegrationInterface integrationInterface, EipInterfaceEditPrepareChain<R> prepareChain);

}
