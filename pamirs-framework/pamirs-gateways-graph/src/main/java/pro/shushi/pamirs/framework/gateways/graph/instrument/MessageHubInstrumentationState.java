package pro.shushi.pamirs.framework.gateways.graph.instrument;

import graphql.execution.instrumentation.InstrumentationState;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;

@Data
class MessageHubInstrumentationState implements InstrumentationState {

    private MessageHub messageHub;

    public MessageHubInstrumentationState() {
        this.messageHub = new MessageHub();
    }

}
