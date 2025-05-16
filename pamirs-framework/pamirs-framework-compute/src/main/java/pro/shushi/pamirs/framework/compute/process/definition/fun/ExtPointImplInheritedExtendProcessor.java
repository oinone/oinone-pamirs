package pro.shushi.pamirs.framework.compute.process.definition.fun;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedExtendProcessor;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedProcessor;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import javax.annotation.Resource;
import java.util.List;

/**
 * 扩展点继承逻辑扩展
 * <p>
 * 2020/4/26 11:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Order(1)
@Component
public class ExtPointImplInheritedExtendProcessor implements InheritedExtendProcessor {

    @Resource
    private InheritedProcessor inheritedProcessor;

    @Override
    public void abstractInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

    @Override
    public void transientInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

    @Override
    public void multiTableInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

    @Override
    public void extend(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        String module = meta.whichModule(modelDefinition.getModel());
        if (!meta.getModule().equals(module)) {
            return;
        }
        List<ExtPointImplementation> extPointImplementations = meta.getExtPointImplementationList(modelDefinition.getModel());
        List<ExtPointImplementation> superExtPointImplementations = meta.getExtPointImplementationList(superModel.getModel());
        for (ExtPointImplementation superExtPointImplementation : superExtPointImplementations) {
            if (superExtPointImplementation.isMetaCompleted()) {
                continue;
            }
            String namespace = modelDefinition.getModel();
            String name = superExtPointImplementation.getName();
            String executeNamespace = superExtPointImplementation.getExecuteNamespace();
            String executeFun = superExtPointImplementation.getExecuteFun();
            String superSign = superExtPointImplementation.getSign();
            ExtPointImplementation existExtPointImplementation = findExistExtPointImplementation(extPointImplementations, superExtPointImplementation);
            if (null != existExtPointImplementation && !existExtPointImplementation.isMetaCompleted()) {
                continue;
            }
            ExtPointImplementation newExtPointImplementation = ObjectUtils.clone(superExtPointImplementation);
            newExtPointImplementation.disableMetaCompleted();
            if (null != existExtPointImplementation) {
                newExtPointImplementation.setId(existExtPointImplementation.getId());
                newExtPointImplementation.setHash(existExtPointImplementation.getHash());
                newExtPointImplementation.setStringify(existExtPointImplementation.getStringify());
            } else {
                newExtPointImplementation.setId(null);
                newExtPointImplementation.setHash(null);
                newExtPointImplementation.setStringify(null);
            }

            newExtPointImplementation.setSystemSource(SystemSourceEnum.EXTEND_INHERITED);
            String newSign = namespace + CharacterConstants.SEPARATOR_DOT + name + CharacterConstants.SEPARATOR_OCTOTHORPE
                    + executeNamespace + CharacterConstants.SEPARATOR_DOT + executeFun;
            newExtPointImplementation.setNamespace(namespace);
            newExtPointImplementation.setSign(newSign);
            if (null != newExtPointImplementation.getExtPoint()) {
                newExtPointImplementation.getExtPoint().setNamespace(namespace);
            }
            MetaData metaData = meta.getData().get(modelDefinition.getModule());
            metaData.addData(newExtPointImplementation);
            String sourceModule = meta.getCrossingModule(ExtPointImplementation.MODEL_MODEL, superSign);
            if (null != sourceModule) {
                metaData.addCrossingExtendData(ExtPointImplementation.MODEL_MODEL, newSign, sourceModule);
                meta.placeCrossingMetadata(sourceModule, newExtPointImplementation);
            }
        }
    }

    private ExtPointImplementation findExistExtPointImplementation(List<ExtPointImplementation> extPointImplementations,
                                                                   ExtPointImplementation superExtPointImplementation) {
        for (ExtPointImplementation extPointImplementation : extPointImplementations) {
            if (superExtPointImplementation.getExecuteNamespace().equals(extPointImplementation.getExecuteNamespace())
                    && superExtPointImplementation.getExecuteFun().equals(extPointImplementation.getExecuteFun())
            ) {
                return extPointImplementation;
            }
        }
        return null;
    }

    @Override
    public void proxyExtend(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

}
