package pro.shushi.pamirs.framework.compute.process.definition.fun;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.meta.api.core.compute.definition.MetaDataExtendComputer;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.ExtNamespaceAndNameUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import jakarta.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_MODULE_DEPENDENT_ERROR;
import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_CLASS_IS_NOT_EXISTS_ERROR;

/**
 * 扩展点元数据计算逻辑扩展
 * <p>
 * 2020/4/26 11:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Order(1)
@Component
public class ExtPointExtendComputer implements MetaDataExtendComputer<ExtPointImplementation> {

    @Resource
    private InheritedProcessor inheritedProcessor;

    @Override
    public Result<Void> compute(Meta meta, String model, ExtPointImplementation data) {
        // 为没有对应扩展点的扩展点实现补充扩展点
        if (data.isMetaCompleted()) {
            return new Result<>();
        }
        String sign = data.getNamespace() + CharacterConstants.SEPARATOR_DOT + data.getName();
        ExtPoint extPoint = meta.getDataItem(ExtPoint.MODEL_MODEL, sign);
        if (null == extPoint || extPoint.isMetaCompleted()) {
            FunctionDefinition function = meta.findFunction(data.getExecuteNamespace(), data.getExecuteFun());
            Class<?> implClazz;
            try {
                implClazz = TypeUtils.getClass(function.getClazz());
            } catch (PamirsException e) {
                if (e.getCode() == BASE_CLASS_IS_NOT_EXISTS_ERROR.code()) {
                    if (extPoint != null) {
                        extPoint.disableMetaCompleted();
                    }
                    return new Result<>();
                }
                throw e;
            }
            List<Method> methodWithAnnotations = searchOnInterfaces(implClazz, pro.shushi.pamirs.meta.annotation.ExtPoint.class);
            for (Method methodWithAnnotation : methodWithAnnotations) {
                String namespace = ExtNamespaceAndNameUtils.namespace(methodWithAnnotation);
                String name = ExtNamespaceAndNameUtils.name(methodWithAnnotation);
                if (StringUtils.isBlank(namespace) || StringUtils.isBlank(name)) {
                    return new Result<>();
                }
                if (!name.equals(data.getName())) {
                    continue;
                }
                String extPointModule = meta.getModule();
                // 更新扩展点
                if (null == extPoint) {
                    String originSign = namespace + CharacterConstants.SEPARATOR_DOT + name;
                    extPoint = meta.getDataItem(ExtPoint.MODEL_MODEL, originSign);
                    if (null == extPoint) {
                        throw PamirsException.construct(BASE_MODULE_DEPENDENT_ERROR)
                                .appendMsg(MessageFormat.format("当前模块：{0}，需要依赖接口[{1}]所在的模块或者配置当前模块的扫描路径包含该接口所在包",
                                        meta.getCurrentModule().getName(), methodWithAnnotation.getDeclaringClass().getName())).errThrow();
                    }
                    extPoint = ObjectUtils.clone(extPoint);
                    extPoint.setId(null);
                    extPoint.setHash(null);
                    extPoint.setStringify(null);
                }

                extPoint.setNamespace(data.getNamespace());
                extPoint.setSign(sign);
                extPoint.setSystemSource(SystemSourceEnum.ABSTRACT_INHERITED);
                extPoint.disableMetaCompleted();
                String extendModel = data.getNamespace();
                ModelDefinition extendModelDefinition = meta.getModel(extendModel);
                MetaData extPointMetaData;
                if (null != extendModelDefinition) {
                    inheritedProcessor.convertSuperModelToCurrentModelForFunction(meta,
                            extPoint.getArgumentList(), extPoint.getReturnType(), extendModelDefinition);
                    extPointModule = meta.whichModule(extendModelDefinition.getModel());
                    extPointMetaData = meta.getData().get(extPointModule);
                } else {
                    extPointMetaData = meta.getCurrentModuleData();
                }
                extPointMetaData.addData(extPoint);
                String sourceModule = meta.getCurrentModuleData().getCrossingModule(ExtPointImplementation.MODEL_MODEL, data.getSign());
                boolean isCrossing = null != sourceModule;
                if (isCrossing) {
                    extPointMetaData.addCrossingExtendData(ExtPoint.MODEL_MODEL, extPoint.getSign(), sourceModule);
                    meta.placeCrossingMetadata(sourceModule, extPoint);
                }

                // 更新扩展点函数
                String funSign = extPoint.getNamespace() + CharacterConstants.SEPARATOR_DOT + extPoint.getName();
                FunctionDefinition extPointFunction = meta.getDataItem(FunctionDefinition.MODEL_MODEL, funSign);
                if (null == extPointFunction) {
                    extPointFunction = new FunctionDefinition();
                } else if (!extPointFunction.isMetaCompleted()) {
                    continue;
                }
                extPointFunction.setDisplayName(extPoint.getDisplayName())
                        .setModule(extPointModule)
                        .setNamespace(extPoint.getNamespace())
                        .setFun(extPoint.getName())
                        .setName(extPoint.getName())
                        .setLanguage(FunctionLanguageEnum.JAVA)
                        .setCategory(FunctionCategoryEnum.OTHER)
                        .setSource(FunctionSourceEnum.EXTPOINT)
                        .setOpenLevel(null)
                        .setDataManager(false)
                        .setDescription(extPoint.getDescription())
                        .setClazz(extPoint.getClazz())
                        .setMethod(extPoint.getMethod())
                        .setArgumentList(extPoint.getArgumentList())
                        .setReturnType(extPoint.getReturnType())
                        .setSystemSource(extPoint.getSystemSource())
                        .setSign(funSign)
                ;
                extPointFunction.disableMetaCompleted();

                extPointMetaData.addData(extPointFunction);
                if (isCrossing) {
                    extPointMetaData.addCrossingExtendData(FunctionDefinition.MODEL_MODEL, extPointFunction.getSign(), sourceModule);
                    meta.placeCrossingMetadata(sourceModule, extPointFunction);
                }

                break;
            }
        }
        return new Result<>();
    }

    @Override
    public boolean canCompute(String model) {
        return ExtPointImplementation.MODEL_MODEL.equals(model);
    }

    private static <A extends Annotation> List<Method> searchOnInterfaces(Class<?> clazz, Class<A> annotationClass) {
        List<Method> methodWithAnnotationList = new ArrayList<>();
        Class<?> superClazz = clazz.getSuperclass();
        if (!Object.class.equals(superClazz)) {
            List<Method> iMethodWithAnnotationList = searchOnInterfaces(superClazz, annotationClass);
            if (!CollectionUtils.isEmpty(iMethodWithAnnotationList)) {
                methodWithAnnotationList.addAll(iMethodWithAnnotationList);
            }
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (!ArrayUtils.isEmpty(interfaces)) {
            for (Class<?> interfaceClazz : interfaces) {
                for (Method method : interfaceClazz.getMethods()) {
                    pro.shushi.pamirs.meta.annotation.ExtPoint extPoint = AnnotationUtils.getAnnotation(method, pro.shushi.pamirs.meta.annotation.ExtPoint.class);
                    if (null != extPoint) {
                        methodWithAnnotationList.add(method);
                    }
                }
            }
        }
        return methodWithAnnotationList;
    }

}
