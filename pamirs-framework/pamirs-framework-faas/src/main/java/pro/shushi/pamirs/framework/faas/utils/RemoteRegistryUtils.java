package pro.shushi.pamirs.framework.faas.utils;

import com.google.common.collect.Lists;
import org.apache.dubbo.config.ArgumentConfig;
import org.apache.dubbo.config.MethodConfig;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * 远程服务注册工具类
 * <p>
 * 2020/7/27 7:45 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class RemoteRegistryUtils {

    public static MethodConfig generate(Function function) {
        MethodConfig methodConfig = new MethodConfig();
        methodConfig.setService(function.getClazz());
        methodConfig.setName(function.getFun());
        methodConfig.setArguments(generate(function.getArguments()));
        return methodConfig;
    }

    public static List<ArgumentConfig> generate(List<Arg> argList) {
        if (CollectionUtils.isEmpty(argList)) {
            return Lists.newArrayList();
        } else {
            List<ArgumentConfig> argumentConfigList = new ArrayList<>();
            ArgumentConfig argumentConfig;
            int index = 0;
            for (Arg arg : argList) {
                argumentConfig = new ArgumentConfig();
                argumentConfig.setIndex(index++);
                argumentConfig.setType(arg.getLtype());
                argumentConfigList.add(argumentConfig);
            }
            return argumentConfigList;
        }
    }

}
