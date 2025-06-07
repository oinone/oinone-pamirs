package pro.shushi.pamirs.core.common.pipeline.extension;

import pro.shushi.pamirs.core.common.pipeline.PamirsTransferExchange;

/**
 * @author Adamancy Zhang on 2021-05-23 19:01
 */
public class DefaultPamirsTransferExchange extends DefaultPamirsExchange implements PamirsTransferExchange {

    private Object originBody;

    @Override
    public Object getOriginBody() {
        return originBody;
    }

    @Override
    public void setOriginBody(Object body) {
        originBody = body;
    }
}
