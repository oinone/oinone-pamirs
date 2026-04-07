package pro.shushi.pamirs.eip.api.service;

import java.util.List;

/**
 * @author yeshenyue on 2026/4/3 16:15.
 */
public interface EipLogRetryService {

    String RETRY_LOCK_KEY_PREFIX = "eip:retry:lock:logId:";

    void retryOne(Long logId);

    void retryBatch(List<Long> logIds);
}
