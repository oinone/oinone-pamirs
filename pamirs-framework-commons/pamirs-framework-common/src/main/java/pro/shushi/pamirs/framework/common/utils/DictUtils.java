package pro.shushi.pamirs.framework.common.utils;

import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.util.BitUtil;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典工具类
 * <p>
 * 2022/5/18 10:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DictUtils {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List fetchEnumsByBits(String dictionary, Long bits) {
        if (null == bits) {
            return null;
        }
        List itemList = new ArrayList<>();
        DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(dictionary);
        if (null == dataDictionary) {
            return null;
        }
        if (SystemSourceEnum.UI.equals(dataDictionary.getSystemSource())) {
            List<DataDictionaryItem> dictionaryItemList = dataDictionary.getOptions();
            for (DataDictionaryItem dictionaryItem : dictionaryItemList) {
                if (BitUtil.has(bits, Long.valueOf(dictionaryItem.getValue()))) {
                    BaseEnum item = BaseEnum.construct(dictionaryItem.getName(), dictionaryItem.getValue());
                    itemList.add(item);
                }
            }
        } else {
            return BaseEnum.getEnumsByBits(dictionary, bits);
        }
        return itemList;
    }

}
