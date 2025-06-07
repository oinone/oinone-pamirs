package pro.shushi.pamirs.middleware.schedule.deployer.session;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.middleware.common.util.NumberHelper;
import pro.shushi.pamirs.middleware.common.util.StringHelper;
import pro.shushi.pamirs.middleware.schedule.domain.ScheduleEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adamancy Zhang on 2021-04-28 17:58
 */
public class ScheduleRpcSession {

    private static final String SESSION_ENV = "ENV";
    private static final String SESSION_USER_ID = "USER_ID";
    private static final String SESSION_USER_NAME = "USER_NAME";

    private static final String SESSION_TRANSMITTABLE_EXTEND = "TRANSMITTABLE_EXTEND";
    private static final String SESSION_TENANT = "TENANT";

    private ScheduleRpcSession() {
        //reject create object
    }

    private final static ThreadLocal<ScheduleEnvironment> LOCAL = new ThreadLocal<>();

    public static String getTenant() {
        init();
        return LOCAL.get().getTenant();
    }

    public static void setTenant(String tenant) {
        init();
        LOCAL.get().setTenant(tenant);
    }

    public static String getEnv() {
        init();
        return LOCAL.get().getEnv();
    }

    public static void setEnv(String env) {
        init();
        LOCAL.get().setEnv(env);
    }

    public static String getOwnSign() {
        init();
        return LOCAL.get().getOwnSign();
    }

    public static void setOwnSign(String ownSign) {
        init();
        LOCAL.get().setOwnSign(ownSign);
    }

    public static String getApplication() {
        init();
        return LOCAL.get().getApplication();
    }

    public static void setApplication(String application) {
        init();
        LOCAL.get().setApplication(application);
    }

    public static Integer getTableNum() {
        init();
        return LOCAL.get().getTableNum();
    }

    public static void setTableNum(Integer tableNum) {
        init();
        LOCAL.get().setTableNum(tableNum);
    }

    public static Integer getDayWeek() {
        init();
        return LOCAL.get().getDayWeek();
    }

    public static void setDayWeek(Integer dayWeek) {
        init();
        LOCAL.get().setDayWeek(dayWeek);
    }

    public static Integer getAmpm() {
        init();
        return LOCAL.get().getAmpm();
    }

    public static void setAmpm(Integer ampm) {
        init();
        LOCAL.get().setAmpm(ampm);
    }

    public static Long getUserId() {
        init();
        return LOCAL.get().getUserId();
    }

    public static void setUserId(Long userId) {
        init();
        LOCAL.get().setUserId(userId);
    }

    public static String getUsername() {
        init();
        return LOCAL.get().getUsername();
    }

    public static void setUsername(String username) {
        init();
        LOCAL.get().setUsername(username);
    }

    public static Map<String, String> fetchSessionMap() {
        Map<String, String> sessionMap = new HashMap<>();
        sessionMap.put(SESSION_ENV, getEnv());
        sessionMap.put(SESSION_USER_ID, serialize(getUserId()));
        sessionMap.put(SESSION_USER_NAME, getUsername());

        Map<String, String> transmittable = new HashMap<>();
        transmittable.put(SESSION_TENANT, getTenant());
        sessionMap.put(SESSION_TRANSMITTABLE_EXTEND, JSONObject.toJSONString(transmittable));

        return sessionMap;
    }

    public static void fillSessionFromMap(Map<String, String> sessionMap) {
        if (MapUtils.isEmpty(sessionMap)) {
            return;
        }
        setEnv(sessionMap.get(SESSION_ENV));
        setUserId(deserialize(sessionMap.get(SESSION_USER_ID)));
        setUsername(sessionMap.get(SESSION_USER_NAME));

        String transmittableString = sessionMap.get(SESSION_TRANSMITTABLE_EXTEND);
        if (StringUtils.isNotBlank(transmittableString)) {
            Map<String, String> transmittable = JSONObject.parseObject(transmittableString, new TypeReference<Map<String, String>>() {
            }.getType());
            setTenant(transmittable.get(SESSION_TENANT));
        }
    }

    public static void clear() {
        LOCAL.remove();
    }

    private static void init() {
        if (LOCAL.get() == null) {
            ScheduleEnvironment environment = new ScheduleEnvironment();
            environment.init();
            LOCAL.set(environment);
        }
    }

    private static Long deserialize(String data) {
        if (data == null) {
            return null;
        }
        String[] ss = data.split(CharacterConstants.SEPARATOR_OCTOTHORPE);
        if (ss.length != 2) {
            return null;
        }
        String dataString = ss[1];
        return NumberHelper.longValueOfNullable(dataString);
    }

    private static String serialize(Long object) {
        if (object == null) {
            return null;
        }
        String type = object.getClass().getName();
        String dataString = StringHelper.valueOf(object);
        return type + CharacterConstants.SEPARATOR_OCTOTHORPE + dataString;
    }
}
