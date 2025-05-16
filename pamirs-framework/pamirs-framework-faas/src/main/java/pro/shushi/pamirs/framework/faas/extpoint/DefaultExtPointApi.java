package pro.shushi.pamirs.framework.faas.extpoint;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.configure.PamirsFrameworkExtPointConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.faas.ExtPointApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.constant.ExtPointConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.util.ClassUtils;
import pro.shushi.pamirs.meta.util.FunctionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 扩展点API默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SuppressWarnings("unused")
@Slf4j
@SPI.Service
@Component
public class DefaultExtPointApi implements ExtPointApi {

    // FIXME: zbh 20240725 临时处理协同开发设计器扩展点的方案，在前端全部换为设计器对应模型查询元数据后移除
    private static final Set<String> DESIGNER_EXT_POINT_FILTER = new HashSet<>();

    private static final boolean isCallDesignerExtPoint;

    static {
        String designerModuleQueryExtPointImpl = "pro.shushi.pamirs.designer.common.core.extpoint.impl.DesignerModuleQueryExtPointImpl";
        String designerModelQueryExtPointImpl = "pro.shushi.pamirs.designer.common.core.extpoint.impl.DesignerModelQueryExtPointImpl";
        String designerModelFieldQueryExtPointImpl = "pro.shushi.pamirs.designer.common.core.extpoint.impl.DesignerModelFieldQueryExtPointImpl";
        String queryPageOverride = CharacterConstants.SEPARATOR_DOT + FunctionConstants.queryPage + ExtPointConstants.OVERRIDE;
        String queryOneOverride = CharacterConstants.SEPARATOR_DOT + FunctionConstants.queryByEntity + ExtPointConstants.OVERRIDE;

        isCallDesignerExtPoint = !ClassUtils.isNoClass(designerModuleQueryExtPointImpl);

        DESIGNER_EXT_POINT_FILTER.add(designerModuleQueryExtPointImpl);
        DESIGNER_EXT_POINT_FILTER.add(ModuleDefinition.MODEL_MODEL + queryPageOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModuleQueryExtPointImpl + queryPageOverride);
        DESIGNER_EXT_POINT_FILTER.add(ModuleDefinition.MODEL_MODEL + queryOneOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModuleQueryExtPointImpl + queryOneOverride);
        DESIGNER_EXT_POINT_FILTER.add(ModuleDefinition.UE_MODEL_MODEL + queryPageOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModuleQueryExtPointImpl + queryPageOverride);
        DESIGNER_EXT_POINT_FILTER.add(ModuleDefinition.UE_MODEL_MODEL + queryOneOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModuleQueryExtPointImpl + queryOneOverride);
        DESIGNER_EXT_POINT_FILTER.add(designerModelQueryExtPointImpl);
        DESIGNER_EXT_POINT_FILTER.add(ModelDefinition.MODEL_MODEL + queryPageOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModelQueryExtPointImpl + queryPageOverride);
        DESIGNER_EXT_POINT_FILTER.add(ModelDefinition.MODEL_MODEL + queryOneOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModelQueryExtPointImpl + queryOneOverride);
        DESIGNER_EXT_POINT_FILTER.add(ModelDefinition.UE_MODEL_MODEL + queryPageOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModelQueryExtPointImpl + queryPageOverride);
        DESIGNER_EXT_POINT_FILTER.add(ModelDefinition.UE_MODEL_MODEL + queryOneOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModelQueryExtPointImpl + queryOneOverride);
        DESIGNER_EXT_POINT_FILTER.add(designerModelFieldQueryExtPointImpl);
        DESIGNER_EXT_POINT_FILTER.add(ModelField.MODEL_MODEL + queryPageOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModelFieldQueryExtPointImpl + queryPageOverride);
        DESIGNER_EXT_POINT_FILTER.add(ModelField.MODEL_MODEL + queryOneOverride + CharacterConstants.SEPARATOR_OCTOTHORPE + designerModelFieldQueryExtPointImpl + queryOneOverride);
    }

    @Resource
    private PamirsFrameworkExtPointConfiguration pamirsFrameworkExtPointConfiguration;

    @SuppressWarnings("unchecked")
    @Override
    public Object run(String namespace, String extPointName, Object... args) {
        return run(namespace, extPointName, null, args);
    }

    @Override
    public Object run(String namespace, String extPointName, java.util.function.Function<Object[], Object> defaultConsumer, Object... args) {
        int paramCount = args.length;
        Function runFunction = null;
        List<ExtPointImplementation> executeExtPointInstances =
                getExecuteExtPoints(Objects.requireNonNull(PamirsSession.getContext()).getExtPointImplementationList(namespace, extPointName));
        if (!CollectionUtils.isEmpty(executeExtPointInstances)) {
            //解决引用传递问题
            List<ExtPointImplementation> newExtPointInstances = new ArrayList<>(executeExtPointInstances);
            newExtPointInstances.sort(Comparator.comparingInt(ExtPointImplementation::getPriority));// 表达式过滤
            List<ExtPointImplementation> validExtPoints = newExtPointInstances.stream()
                    .filter(v -> (boolean) Optional.ofNullable(PamirsSession.getContext()
                            .getFunctionAllowNull(v.getExecuteNamespace(), v.getExecuteFun()))
                            .map(fun -> pro.shushi.pamirs.meta.api.Exp.run(v.getExpression(),
                                    FunctionUtils.fetchArgNameList(fun.getArguments()), args)).orElse(false))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(validExtPoints)) {
                for(ExtPointImplementation runExtPointInstance:validExtPoints ){
//                    ExtPointImplementation runExtPointInstance = validExtPoints.get(0);
                    runFunction = PamirsSession.getContext().getFunction(runExtPointInstance.getExecuteNamespace(), runExtPointInstance.getExecuteFun());
                    if(!pamirsFrameworkExtPointConfiguration.isSupportRemote()){
                        if(ClassUtils.isNoClass(runFunction.getClazz())){
                            //类不存在
                            runFunction = null;
                            continue;
                        }
                    }
                    break;
                }
             }
        }
        Object result;
        if (null == runFunction) {
            if (null == defaultConsumer) {
                return FunctionUtils.fetchDynamicParameter(args, paramCount);
            } else {
                result = defaultConsumer.apply(args);
            }
        } else {
            final Function finalFunction = runFunction;
            result = Models.directive().run(() -> Fun.run(finalFunction, args));
        }
        result = FunctionUtils.fetchDynamicParameter(result, paramCount);
        if (null == PamirsSession.getContext().getModelConfig(namespace)) {
            return result;
        }
        if (StringUtils.isNotBlank(Models.api().getModel(result))) {
            return result;
        }
        return Models.orm().modeling(namespace, result);
    }

    public List<ExtPointImplementation> getExecuteExtPoints(List<ExtPointImplementation> extPointInstances) {
        if (CollectionUtils.isEmpty(extPointInstances)) {
            return extPointInstances;
        }

        Set<String> excludeExtPoints = pamirsFrameworkExtPointConfiguration.getExcludes();
        List<ExtPointImplementation> executeExtPoints = new ArrayList<>();
        for (ExtPointImplementation extPoint : extPointInstances) {
            String executeNamespace = extPoint.getExecuteNamespace();
            if (excludeExtPoints != null && excludeExtPoints.contains(executeNamespace)) {
                continue;
            }
            if (DESIGNER_EXT_POINT_FILTER.contains(executeNamespace)) {
                if (!isCallDesignerExtPoint) {
                    if (DESIGNER_EXT_POINT_FILTER.contains(extPoint.getSign())) {
                        executeExtPoints.add(extPoint);
                    }
                    continue;
                }
                if (!DESIGNER_EXT_POINT_FILTER.contains(extPoint.getSign())) {
                    continue;
                }
            }
            executeExtPoints.add(extPoint);
        }

        return executeExtPoints;
    }
}
