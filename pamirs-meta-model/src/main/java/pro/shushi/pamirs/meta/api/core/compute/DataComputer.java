package pro.shushi.pamirs.meta.api.core.compute;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.model.FieldComputer;
import pro.shushi.pamirs.meta.api.core.compute.model.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;

/**
 * 数据计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface DataComputer<C, T> extends CommonApi {

    /**
     * 自定义计算
     *
     * @param model 模型编码
     * @param config 配置
     * @param data 数据
     * @param modelComputer 模型计算
     * @param fieldComputers 字段计算器列表
     * @return
     */
    Result<Void> compute(String model, C config, T data, ModelComputer modelComputer, FieldComputer... fieldComputers);

}
