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
public class TranslateErrorManager {

    public void translateError(PamirsRequestResult result) {

        try {
            List<Map<String, Object>> errors = result.getErrors();

            if (CollectionUtils.isEmpty(errors)) {
                return;
            }

            for (Map<String, Object> error : errors) {
                String message = null;
                String target = null;
                Object messageObj = error.get(ClientGraphQLError.MESSAGE);
                if (messageObj != null) {
                    message = (String) messageObj;
                    target = placeholder(message);
                    if (StringUtils.isNotBlank(target)) {
                        error.put(ClientGraphQLError.MESSAGE, target);
                    }
                }
                Object extObj = error.get(ClientGraphQLError.EXTENSIONS);
                if (extObj != null) {
                    Map<?, ?> ext = (Map<?, ?>) extObj;
                    @SuppressWarnings("unchecked")
                    List<Message> msgList = (List<Message>) ext.get(ClientGraphQLError.MESSAGES);
                    if (CollectionUtils.isEmpty(msgList)) {
                        continue;
                    }
                    for (Message msg : msgList) {
                        String message0 = msg.getMessage();
                        String target0 = null;
                        target0 = placeholder(message0);
                        if (StringUtils.isNotBlank(target0)) {
                            msg.setMessage(target);
                        }
                    }
                }
            }
            result.setErrors(errors);
        } catch (Throwable exp) {
            log.error("翻译Exp发生异常", exp);
        }
    }
}
