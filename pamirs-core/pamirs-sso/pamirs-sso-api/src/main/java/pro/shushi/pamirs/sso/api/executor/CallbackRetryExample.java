package pro.shushi.pamirs.sso.api.executor;

import pro.shushi.pamirs.core.common.HttpRequestBuilder;
import pro.shushi.pamirs.core.common.enmu.HttpRequestTypeEnum;
import pro.shushi.pamirs.sso.api.model.SsoOauth2ClientDetails;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CallbackRetryExample {
    private static volatile ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
    private static final Object lock = new Object();

    public static void callBackLogout(List<SsoOauth2ClientDetails> list, Map<String, Object> param) {
        if (list != null && list.size() > 0) {
            for (SsoOauth2ClientDetails ssoOauth2ClientDetails : list) {
                if (!map.containsKey(ssoOauth2ClientDetails.getClientId())) {
                    synchronized (lock) {
                        if (!map.containsKey(ssoOauth2ClientDetails.getClientId())) {
                            map.put(ssoOauth2ClientDetails.getClientId(), ssoOauth2ClientDetails.getLogoutUrl());
                        }
                    }
                }
            }
            scheduleCallback(param, 1, 3, 10, 60, 300, 600);
        }
    }

    private static void scheduleCallback(Map<String, Object> param, int... intervals) {
        for (int interval : intervals) {
            ScheduledFuture<?> future = SsoScheduledExecutor.getScheduledExecutorService().scheduleAtFixedRate(() -> {
                for (String key : map.keySet()) {
                    try {
                        String result = HttpRequestBuilder.newInstance(map.get(key), HttpRequestTypeEnum.POST)
                                .addParams(param)
                                .request();
                        if ("SUCCESS".equalsIgnoreCase(result)) {
                            removeKey(key);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, interval, TimeUnit.SECONDS);

            stopScheduledExecutor(future);

        }

    }

    public static void stopScheduledExecutor(ScheduledFuture<?> future) {
        if (map.isEmpty()) {
            synchronized (lock) {
                if (map.isEmpty()) {
                    future.cancel(true);
                }
            }
        }
    }


    public static void removeKey(String clientId) {
        if (map.containsKey(clientId)) {
            synchronized (lock) {
                if (map.containsKey(clientId)) {
                    map.remove(clientId);
                }
            }
        }
    }

}
