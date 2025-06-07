package pro.shushi.pamirs.eip.view.manager.edit;


import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;

import java.util.Stack;
import java.util.function.Function;

public class EipInterfaceEditPrepareChain<R> implements EipInterfaceEditPreparer<R> {

    private Stack<EipInterfaceEditPreparer<R>> preparerList;

    private Function<EipIntegrationInterface, R> function;

    private EipIntegrationInterface eipIntegrationInterface;
    private EipIntegrationInterfaceEdit eipIntegrationInterfaceEdit;


    private EipInterfaceEditPrepareChain(Function<EipIntegrationInterface, R> function, EipIntegrationInterface eipIntegrationInterface) {
        this.function = function;
        this.eipIntegrationInterface = eipIntegrationInterface;
    }

    private EipInterfaceEditPrepareChain(EipIntegrationInterfaceEdit eipIntegrationInterfaceEdit) {
        this.eipIntegrationInterfaceEdit = eipIntegrationInterfaceEdit;
    }

    public void addPreparer(EipInterfaceEditPreparer<R> preparer) {
        if (preparerList == null) {
            preparerList = new Stack<>();
        }
        preparerList.push(preparer);
    }

    public EipIntegrationInterface getEipIntegrationInterface() {
        return eipIntegrationInterface;
    }

    public EipIntegrationInterfaceEdit getEipIntegrationInterfaceEdit() {
        return eipIntegrationInterfaceEdit;
    }

    public static <R> EipInterfaceEditPrepareChain<R> build(Function<EipIntegrationInterface, R> function) {
        return new EipInterfaceEditPrepareChain<R>(function, new EipIntegrationInterface());
    }

    public static <R> EipInterfaceEditPrepareChain<R> build() {
        return new EipInterfaceEditPrepareChain<R>(new EipIntegrationInterfaceEdit());
    }


    @Override
    public R prepare(EipIntegrationInterfaceEdit interfaceEdit, EipInterfaceEditPrepareChain<R> chain) {
        if (preparerList.empty()) {
            this.eipIntegrationInterface.construct();
            return function.apply(this.eipIntegrationInterface);
        }
        return preparerList.pop().prepare(interfaceEdit, chain);
    }

    @Override
    public R construct(EipIntegrationInterface integrationInterface, EipInterfaceEditPrepareChain<R> constructChain) {
        if (preparerList.empty()) {
            return this.eipIntegrationInterfaceEdit.construct();
        }
        return preparerList.pop().construct(integrationInterface, constructChain);
    }
}
