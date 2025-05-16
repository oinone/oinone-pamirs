package pro.shushi.pamirs.framework.configure.simulate.api;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.Runner;
import pro.shushi.pamirs.meta.api.RunnerWithoutResult;

import java.util.Map;

/**
 * 元数据模拟环境计算服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
public interface MetaSimulateService extends CommonApi {

    void transientStaticExecuteWithoutResult(Map<String/*model*/, String/*simulate model*/> modelMap, RunnerWithoutResult runner);

    <T> T transientStaticExecute(Map<String/*model*/, String/*simulate model*/> modelMap, Runner<T> runner);

}
