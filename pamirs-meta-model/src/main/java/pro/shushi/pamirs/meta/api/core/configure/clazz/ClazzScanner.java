package pro.shushi.pamirs.meta.api.core.configure.clazz;

import pro.shushi.pamirs.meta.api.CommonApi;

import java.util.Set;

/**
 * 类扫描器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface ClazzScanner extends CommonApi {

    /**
     * 扫描指定包下的类，并获取有效数据
     *
     * @param packages 包
     * @return 类
     */
    Set<Class<?>> scan(String... packages);

}
