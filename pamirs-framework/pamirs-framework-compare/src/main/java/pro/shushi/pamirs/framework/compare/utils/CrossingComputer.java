package pro.shushi.pamirs.framework.compare.utils;

import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.List;
import java.util.function.Function;

/**
 * 跨模块计算处理
 * <p>
 * 2021/2/1 10:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class CrossingComputer {

    public static <T extends MetaBaseModel> void crossingFunCompute(Meta meta, String model,
                                                                    Function<T, String> modelResolver,
                                                                    Function<T, String> namespaceResolver,
                                                                    Function<T, String> funResolver) {
        List<T> dataList = meta.getCurrentModuleData().getDataList(model);
        if (!CollectionUtils.isEmpty(dataList)) {
            for (T data : dataList) {
                String modelModel = modelResolver.apply(data);
                ModelDefinition modelDefinition = meta.getModel(modelModel);
                if (null != modelDefinition) {
                    boolean isCrossing = null == meta.getCurrentModuleData().getModel(modelModel);
                    if (isCrossing) {
                        String sign = data.getSign();
                        MetaData metaData = meta.getData().get(modelDefinition.getModule());

                        if (!data.isMetaCompleted()) {
                            // 记录跨模块扩展被替换的元数据
                            metaData.addCrossingExtendData(model, sign, meta.getModule());

                            // 扩展替换元数据
                            metaData.addData(data);
                        }

                        String namespace = namespaceResolver.apply(data);
                        String fun = funResolver.apply(data);
                        FunctionDefinition existFunction = metaData.getFunction(namespace, fun);
                        if (null == existFunction || existFunction.isMetaCompleted()) {
                            FunctionDefinition functionDefinition = meta.getValidFunction(namespace, fun);
                            if (null == functionDefinition) {
                                functionDefinition = meta.getFunction(namespace, fun);
                            }
                            if (null == functionDefinition) {
                                log.error("配置错误，函数不存在。 module:" + meta.getModule() + ",namespace:" + namespace + ",fun:" + fun);
                                //throw PamirsException.construct(CompareExpEnumerate.BASE_FUNCTION_NOT_EXIST_ERROR)
                                //        .appendMsg(("module:" + meta.getModule() + ",namespace:" + namespace + ",fun:" + fun)).errThrow();
                                continue;
                            }
                            if (!functionDefinition.isMetaCompleted()) {
                                // 记录跨模块扩展被替换的函数元数据
                                metaData.addCrossingExtendData(FunctionDefinition.MODEL_MODEL,
                                        functionDefinition.getSign(), functionDefinition.getModule());

                                // 扩展替换函数元数据
                                metaData.placeFunction(functionDefinition.getNamespace(), functionDefinition);
                            }
                        }
                    }
                }
            }
        }
    }

}
