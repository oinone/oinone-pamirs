package pro.shushi.pamirs.framework.orm.submit;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.plugin.sequence.IdGeneratorInterceptor;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 提交记录处理器
 * <p>
 * 2022/5/11 5:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class SubmitRecordManager {

    public void collect(String model, Object data) {
        if (Models.modelDirective().isReentry(data)) {
            return;
        }
        Models.modelDirective().enableReentry(data);
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(model);
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            if (modelFieldConfig.getRelationStore() && TtypeEnum.isRelationType(modelFieldConfig.getTtype())) {
                if (data instanceof Collection) {
                    Collection<?> dataList = (Collection<?>) data;
                    if (CollectionUtils.isEmpty(dataList)) {
                        break;
                    }
                    List<Object> fieldValues = new ArrayList<>();
                    for (Object item : dataList) {
                        Object fieldValue = FieldUtils.getFieldValue(item, modelFieldConfig.getLname());
                        if (fieldValue instanceof Collection) {
                            Collection<?> itemFieldValues = (Collection<?>) fieldValue;
                            fieldValues.addAll(itemFieldValues);
                            prepareSubmitRecordContext(modelFieldConfig, item, itemFieldValues);
                        } else {
                            fieldValues.add(fieldValue);
                            prepareSubmitRecordContext(modelFieldConfig, item, fieldValue);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(fieldValues)) {
                        collect(modelFieldConfig.getReferences(), fieldValues);
                    }
                } else {
                    if (null == data) {
                        break;
                    }
                    Object fieldValue = FieldUtils.getFieldValue(data, modelFieldConfig.getLname());
                    prepareSubmitRecordContext(modelFieldConfig, data, fieldValue);
                    collect(modelFieldConfig.getReferences(), fieldValue);
                }
            }
        }
    }

    public void prepareSubmitRecordContext(ModelFieldConfig modelFieldConfig, Object mainData, Object fieldData) {
        final String ttype = modelFieldConfig.getTtype();
        TtypeEnum.switches(ttype,
                TtypeEnum.cases(TtypeEnum.O2M).to(() -> {

                }),
                TtypeEnum.cases(TtypeEnum.M2M).to(() -> {

                }),
                TtypeEnum.cases(TtypeEnum.M2O).to(() -> {

                }),
                TtypeEnum.cases(TtypeEnum.O2O).to(() -> {

                })
        );

        IdGeneratorInterceptor.fillAndFetchId(modelFieldConfig.getModel(), mainData);
    }

}
