package pro.shushi.pamirs.framework.configure.simulate.service;

import java.util.Map;
import java.util.Set;

/**
 * 元数据模拟器
 * <p>
 * 2020/10/20 5:21 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class MetaSimulator {

    public static Map<String, String> simulate() {
        return DefaultMetaSimulateService.getSimulateModelMap();
    }

    public static Set<String> preTable() {
        return DefaultMetaSimulateService.getPreCreateTableModels();
    }

}
