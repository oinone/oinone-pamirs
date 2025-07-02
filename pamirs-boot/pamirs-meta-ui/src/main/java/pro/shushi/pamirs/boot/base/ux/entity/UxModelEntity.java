package pro.shushi.pamirs.boot.base.ux.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxDetail;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTable;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxTableSearch;
import pro.shushi.pamirs.boot.base.ux.entity.annotation.UxDetailWrapper;
import pro.shushi.pamirs.boot.base.ux.entity.annotation.UxFormWrapper;
import pro.shushi.pamirs.boot.base.ux.entity.annotation.UxTableSearchWrapper;
import pro.shushi.pamirs.boot.base.ux.entity.annotation.UxTableWrapper;
import pro.shushi.pamirs.boot.base.ux.service.UxClassMetadataFetcher;
import pro.shushi.pamirs.boot.base.ux.utils.UxModelHelper;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UxModel 实体类
 *
 * @author Adamancy Zhang at 15:12 on 2025-06-16
 */
@Slf4j
@Data
public class UxModelEntity implements Serializable {

    private static final long serialVersionUID = 3952691943774550702L;

    private String clazz;

    private String model;

    private UxTableWrapper uxTable;

    private UxTableSearchWrapper uxTableSearch;

    private UxFormWrapper uxForm;

    private UxDetailWrapper uxDetail;

    private List<UxModelFieldEntity> fields;

    @JSONField(serialize = false)
    private transient ModelConfig modelConfig;

    @JSONField(serialize = false)
    private transient Iterator<UxModelFieldEntity> fieldIterator;

    @JSONField(serialize = false)
    private transient Map<String, UxModelFieldEntity> fieldCache;

    private Iterator<UxModelFieldEntity> getFieldIterator() {
        if (fieldIterator == null) {
            if (fields == null) {
                return Collections.emptyIterator();
            }
            fieldIterator = fields.iterator();
        }
        return fieldIterator;
    }

    private Map<String, UxModelFieldEntity> getFieldCache() {
        if (fieldCache == null) {
            fieldCache = new HashMap<>();
        }
        return fieldCache;
    }

    public UxModelFieldEntity getUxModelFieldEntity(String field) {
        Map<String, UxModelFieldEntity> fieldCache = getFieldCache();
        UxModelFieldEntity target = fieldCache.get(field);
        if (target == null) {
            Iterator<UxModelFieldEntity> fieldIterator = getFieldIterator();
            while (fieldIterator.hasNext()) {
                UxModelFieldEntity next = fieldIterator.next();
                String nextField = next.getField();
                fieldCache.put(nextField, next);
                if (field.equals(nextField)) {
                    return next;
                }
            }
        }
        return target;
    }

    public static UxModelEntity wrap(Class<?> clazz) {
        UxModelEntity uxModel = new UxModelEntity();
        uxModel.clazz = clazz.getName();
        uxModel.model = Models.api().getModel(clazz);
        Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, UxTable.class)).map(UxTableWrapper::wrap).ifPresent(uxModel::setUxTable);
        Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, UxTableSearch.class)).map(UxTableSearchWrapper::wrap).ifPresent(uxModel::setUxTableSearch);
        Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, UxForm.class)).map(UxFormWrapper::wrap).ifPresent(uxModel::setUxForm);
        Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, UxDetail.class)).map(UxDetailWrapper::wrap).ifPresent(uxModel::setUxDetail);
        uxModel.fields = getDeclaredFields(clazz, uxModel.model);
        return uxModel;
    }

    public static UxModelEntity wrap(ModelConfig modelConfig) {
        UxModelEntity uxModel = new UxModelEntity();
        uxModel.setModel(modelConfig.getModel());
        uxModel.setFields(Optional.ofNullable(modelConfig.getModelFieldConfigList())
                .map(v -> v.stream().map(vv -> {
                    UxModelFieldEntity uxModelField = new UxModelFieldEntity();
                    uxModelField.setField(vv.getField());
                    uxModelField.setModelFieldConfig(vv);
                    return uxModelField;
                }).collect(Collectors.toList()))
                .orElse(new ArrayList<>()));
        return uxModel;
    }

    private static List<UxModelFieldEntity> getDeclaredFields(Class<?> clazz, String model) {
        Set<String> fieldSet = new HashSet<>();
        return getDeclaredFields(clazz, model, fieldSet);
    }

    private static List<UxModelFieldEntity> getDeclaredFields(Class<?> clazz, String model, Set<String> fieldSet) {
        List<UxModelFieldEntity> list = new ArrayList<>();
        ModelDefinition modelDefinition = PamirsSession.getContext().getSimpleModelConfig(model).getModelDefinition();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.getName().contains(CharacterConstants.SEPARATOR_DOLLAR)
                    && !Modifier.isStatic(field.getModifiers())) {
                UxModelFieldEntity uxModelField = UxModelFieldEntity.wrap(field);
                if (uxModelField == null) {
                    continue;
                }
                String fieldName = uxModelField.getField();
                if (fieldSet.add(fieldName)) {
                    ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, uxModelField.getField());
                    if (modelFieldConfig == null) {
                        continue;
                    }
                    uxModelField.setModelFieldConfig(modelFieldConfig);
                    uxModelField.setRegisterSearchWidget(UxModelHelper.getRegisterSearchWidget(modelDefinition, modelFieldConfig.getModelField()));
                    list.add(uxModelField);
                }
            }
        }
        List<String> superModels = Optional.ofNullable(PamirsSession.getContext().getSimpleModelConfig(model)).map(ModelConfig::getSuperModels).orElse(null);
        if (CollectionUtils.isEmpty(superModels)) {
            return list;
        }
        for (String superModel : superModels) {
            ModelConfig superModelConfig = PamirsSession.getContext().getSimpleModelConfig(superModel);
            if (superModelConfig == null) {
                log.error("Invalid super model config. model: {}, superModel: {}", model, superModel);
                continue;
            }
            String lname = superModelConfig.getLname();
            if (!HashMap.class.getName().equals(lname) && !K2.class.getName().equals(lname) && !D.class.getName().equals(lname)) {
                UxModelEntity superUxModel = UxClassMetadataFetcher.getApi().getClassMetadataByModel(superModelConfig);
                if (superUxModel == null) {
                    log.error("Invalid super UxModelEntity. model: {}, superModel: {}", model, superModel);
                    continue;
                }
                for (UxModelFieldEntity uxModelField : superUxModel.fields) {
                    if (fieldSet.add(uxModelField.getField())) {
                        list.add(uxModelField);
                    }
                }
            }
        }
        return list;
    }
}
