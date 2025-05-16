package pro.shushi.pamirs.boot.base.ux.model.auth;

import com.alibaba.fastjson.JSON;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Slf4j
public class UIAuth implements Serializable {

    private static final long serialVersionUID = -110387805859302639L;

    private Set<String> canReadAccessFields;

    private Set<String> canUpdateAccessFields;

    public Set<String> getCanReadAccessFields() {
        return canReadAccessFields;
    }

    public Set<String> getCanUpdateAccessFields() {
        return canUpdateAccessFields;
    }

    public static UIAuth generatorUIAuth(String model) {
        UIAuth uiAuth = new UIAuth();
        Result<Set<String>> readFieldResult = AuthApi.get().canReadableFields(model);
        if (readFieldResult.getSuccess()) {
            uiAuth.canReadAccessFields = readFieldResult.getData();
        } else {
            uiAuth.canReadAccessFields = Collections.emptySet();
        }

        Result<Set<String>> writeFieldResult = AuthApi.get().canWritableFields(model);
        if (writeFieldResult.getSuccess()) {
            uiAuth.canUpdateAccessFields = writeFieldResult.getData();
        } else {
            uiAuth.canUpdateAccessFields = Collections.emptySet();
        }

        if (log.isDebugEnabled()) {
            log.debug("Fetch model field access permission. model: {}, data: {}", model, JSON.toJSONString(uiAuth));
        }

        return uiAuth;
    }
}
