package pro.shushi.pamirs.framework.gateways.graph.longpolling;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.async.DeferredResult;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.List;
import java.util.Objects;

@Slf4j
@Data
public abstract class PamirsLongPolling implements LongPollingConstants {

    private String traceId;

    private DeferredResult<String> deferredResult;

    private Function function;

    /**
     * 初始化LongPolling对象
     */
    public PamirsLongPolling(String gql) {

        function = GraphQLParser.getServerAction(gql);
        String type = null;
        String key = null;
        Long timeout = null;
//        function = PamirsSession.getContext().getFunction(model, fun);
        try {
            if (null != function) {
                type = function.getNamespace();
                Boolean isLongPolling = function.getLongPolling();
                if (isLongPolling) {
                    timeout = Long.valueOf(function.getLongPollingTimeout());
                    //判断如果是userId，后端自己获取
                    String longPollingKey = function.getLongPollingKey();
                    if (LongPollingConstants.userId.equalsIgnoreCase(longPollingKey)) {
                        PamirsUserDTO currentUser = Fun.run(Objects.requireNonNull(PamirsSession.getContext()).getFunction("user.PamirsUserTransient", "currentUser"));
                        if (Objects.nonNull(currentUser) && Objects.nonNull(currentUser.getUserId())) {
                            key = String.valueOf(currentUser.getUserId());
                            PamirsSession.setUserId(currentUser.getUserId());
//                            PamirsSession.setUserName(currentUser.getLogin());
                        } else {
                            log.error("long polling failed to get user info");
                        }
                    } else {
                        key = String.valueOf(PamirsSession.getRequestVariables().getVariables().get(longPollingKey));
                    }
                }
            }
        } catch (Exception e) {
            log.error("{}:{}", "long polling not supported", e);
        }
        init(type, key, timeout);
    }

    public PamirsLongPolling(String type, String key, Long timeOut) {
        init(type, key, timeOut);
    }

    protected void init(String type, String key, Long timeout) {
        if (ObjectUtils.isEmpty(timeout)) {
            timeout = defaultTimeOut;
        }
        String uniqueKey = LongPollingContainer.getKey(type, key);
        String traceId = null;
//        this.setTraceId((String) PamirsRequestSession.getRequestVariables().getVariables().get("traceId"));
//        this.setTraceId(requestId);
        String traceId1 = LongPollingContainer.getTraceId();
        this.setTraceId(traceId1);
        DeferredResult<String> deferredResult = new DeferredResult<>(timeout, null);
        Runnable runnable = () -> LongPollingContainer.remove(uniqueKey, traceId1);
        deferredResult.onCompletion(runnable);
        Long userId = PamirsSession.getUserId();
//        String userName = PamirsSession.getUserName();
//        if (null == userId) return;
        Runnable timeoutRunnable = () -> {
            PamirsSession.setUserId(userId);
//            PamirsSession.setUserName(userName);
            close(type, key);
            LongPollingContainer.remove(uniqueKey, traceId1);
        };
        deferredResult.onTimeout(timeoutRunnable);
        this.setDeferredResult(deferredResult);
        LongPollingContainer.add(uniqueKey, traceId1, this);
    }

    /**
     * 保证单个用户复用长连接
     *
     * @param type 可以传modelModel
     * @param key  唯一key
     */
    public static void close(String type, String key) {
        List<PamirsLongPolling> pamirsLongPollingList = LongPollingContainer.getAll(type, key);
        if (CollectionUtils.isNotEmpty(pamirsLongPollingList)) {
            for (PamirsLongPolling pamirsLongPolling : pamirsLongPollingList) {
                DeferredResult<String> deferredResult = pamirsLongPolling.getDeferredResult();
                if (deferredResult != null) {
                    deferredResult.setResult(pamirsLongPolling.execute());
                }
            }
        }
    }

    protected abstract String execute();

}
