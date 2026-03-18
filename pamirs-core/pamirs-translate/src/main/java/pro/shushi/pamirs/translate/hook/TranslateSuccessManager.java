package pro.shushi.pamirs.translate.hook;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;

import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.translate.manager.base.TranslateMetaBaseService.log;
import static pro.shushi.pamirs.translate.utils.TranslatePlaceholder.placeholder;

@Component
public class TranslateSuccessManager {

    public void translateSuccess(PamirsRequestResult result) {

        try {
            Map<Object, Object> extObj = result.getExtensions();
            if (extObj != null) {
                Map<?, ?> ext = (Map<?, ?>) extObj;
                @SuppressWarnings("unchecked")
                List<Message> msgList = (List<Message>) ext.get(ClientGraphQLError.MESSAGES);
                if (CollectionUtils.isEmpty(msgList)) {
                    return;
                }
                for (Message msg : msgList) {
                    String message0 = msg.getMessage();
                    String target0 = null;
                    target0 = placeholder(message0);
                    if (StringUtils.isNotBlank(target0)) {
                        msg.setMessage(target0);
                    }
                }
                extObj.put(ClientGraphQLError.MESSAGES, msgList);
            }
        } catch (Throwable exp) {
            log.error("Translate Exp exception occurred", exp);
        }
    }
}
