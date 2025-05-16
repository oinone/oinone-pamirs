package pro.shushi.pamirs.message.model.third;


import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

@Data
public class ThirdMailMessageTransientModel {
    private Long sendPartnerId;

    private List<Long>  receivePartnerIds;

    private String inputBody;

    public ThirdMailMessageTransientModel() {
    }

    public ThirdMailMessageTransientModel(Long sendPartnerId, List<Long> receivePartnerIds, String inputBody) {
        this.sendPartnerId = sendPartnerId;
        this.receivePartnerIds = receivePartnerIds;
        this.inputBody = inputBody;
    }
}
