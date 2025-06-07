package pro.shushi.pamirs.user.api.login;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shier
 * date 2020/4/11
 */
public class UserLoginFactory {

    public static final String DEFAULT_LOGIN = "_defaultLoginService";

    private static Map<String, List<IUserLogin>> innerMap;

    private static IUserLogin defaultService;

    static void init() {
        Map<String, List<IUserLogin>> innerMap = new HashMap<>();
        List<IUserLogin> services = BeanDefinitionUtils.getBeansOfTypeByOrdered(IUserLogin.class);
        for (IUserLogin service : services) {
            String type = service.type();
            List<IUserLogin> existList = innerMap.getOrDefault(type, new ArrayList<>());
            existList.add(service);
            innerMap.put(service.type(), existList);
        }
        defaultService = (IUserLogin) BeanDefinitionUtils.getBean(DEFAULT_LOGIN);
        UserLoginFactory.innerMap = innerMap;
    }

    public static IUserLogin getUserLoginThroughRequest() {
        return getUserLogin(LoginTypeParser.getLoginType());
    }

    public static IUserLogin getUserLogin() {
        return getUserLogin(CharacterConstants.SEPARATOR_EMPTY);
    }

    public static IUserLogin getUserLogin(String key) {
        if (null == innerMap) {
            synchronized (UserLoginFactory.class) {
                if (null == innerMap) {
                    init();
                }
            }
        }
        if (StringUtils.isEmpty(key)) {
            return defaultService;
        }
        List<IUserLogin> services = innerMap.get(key);
        if (CollectionUtils.isEmpty(services)) {
            return defaultService;
        }
        return services.get(0);
    }
}