package pro.shushi.pamirs.framework.compute.process.definition.field;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.FieldComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;

/**
 * 字段数据字典计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class FieldOptionsComputer<T> implements FieldComputer<Meta, T> {

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, ModelField field, T data) {
        Result<Void> result = new Result<>();
        String dictionary = field.getDictionary();
        if (null != dictionary) {
            DataDictionary dataDictionary = meta.getDataItem(DataDictionary.MODEL_MODEL, dictionary);
            if (null == dataDictionary) {
                throw PamirsException.construct(ComputeExpEnumerate.BASE_DATA_DICTIONARY_MODULE_DEPENDENT_ERROR)
                        .appendMsg("model:" + field.getModel() + ", field:" + field.getField() + ", dictionary:" + dictionary).errThrow();
            }
            field.setSelection(dataDictionary);
            if (null == field.getOptions()) {
                field.setOptions(dataDictionary.getOptions());
            }
            if (null != dataDictionary.getBit() && dataDictionary.getBit()
                    && null != field.getMulti() && field.getMulti()) {
                boolean isLongType = Long.class.getName().equals(field.getLtype());
                if (!isLongType && StringUtils.isBlank(field.getStoreSerialize())) {
                    field.setStoreSerialize(SerializeEnum.BIT.value());
                } else if (isLongType && StringUtils.isBlank(field.getRequestSerialize())) {
                    field.setRequestSerialize(SerializeEnum.BIT.value());
                }
            }
        }
        return result;
    }

}
