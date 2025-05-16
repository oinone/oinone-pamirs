package pro.shushi.pamirs.framework.compute.process.definition.model;

import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.core.faas.FunApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.Map;

/**
 * 默认构造函数计算函数
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI.Service(ConstructComputer.SPI_NAME)
public class ConstructComputer<T> implements ModelComputer<Meta, T> {

    public static final String SPI_NAME = "construct";

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, String model, T data, Map<String, Object> computeContext) {
        Result<Void> result = new Result<>();
        FunctionDefinition functionDefinition = meta.findFunction(model, FunctionConstants.construct);
        Function construct = CommonApiFactory.getApi(FunApi.class).generate(functionDefinition);
        // 默认值计算函数
        if (null != construct) {
            Models.directive().run(() -> Fun.run(construct, data));
        }
        return result;
    }

}
