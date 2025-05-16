package pro.shushi.pamirs.framework.connectors.data.api.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

/**
 * 排序工具类
 * <p>
 * 2020/12/4 5:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SortUtils {

    public static Sort sort(String sortString) {
        if (StringUtils.isBlank(sortString)) {
            return null;
        }
        String[] sorts = sortString.split(CharacterConstants.SEPARATOR_COMMA);
        if (ArrayUtils.isEmpty(sorts)) {
            return null;
        }
        Sort sort = new Sort();
        for (String singleSort : sorts) {
            String sortScript = singleSort.trim();
            String[] sortScripts = sortScript.split(CharacterConstants.SEPARATOR_BLANK);
            if (ArrayUtils.isEmpty(sortScripts)) {
                continue;
            }
            if (StringUtils.isBlank(sortScripts[0])) {
                continue;
            }
            if (sortScripts.length == 1) {
                sort.addOrder(SortDirectionEnum.ASC, sortScripts[0].trim());
            } else if (sortScripts.length == 2) {
                sort.addOrder(SortDirectionEnum.valueOf(sortScripts[1].toUpperCase()), sortScripts[0].trim());
            } else {
                throw new RuntimeException("Sort syntax error. Sort:" + sortString);
            }
        }
        return sort;
    }

}