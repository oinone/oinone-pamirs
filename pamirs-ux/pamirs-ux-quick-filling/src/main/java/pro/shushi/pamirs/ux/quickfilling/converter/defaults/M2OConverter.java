package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import com.google.common.collect.Sets;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.util.*;

/**
 * 多对一类型转换
 *
 * @author Adamancy Zhang at 09:51 on 2025-11-28
 */
public class M2OConverter extends AbstractRelationConverter implements QuickFillingConverter {

    protected final Map<String, List<QuickFillingRow>> matchValues;

    public M2OConverter(QuickFillingColumn column) {
        super(column);
        this.matchValues = new HashMap<>();
    }

    @Override
    public void collect(QuickFillingRow row, String value) {
        if (isSkip(row, value)) {
            return;
        }
        matchValues.computeIfAbsent(value, k -> new ArrayList<>()).add(row);
    }

    @Override
    public void fill() {
        if (matchValues.isEmpty()) {
            return;
        }
        Set<String> matchedValues = new HashSet<>();
        Set<String> values = matchValues.keySet();
        QuickFillingColumn column = getColumn();
        String references = column.getReferences();
        List<Object> list = DataShardingHelper.build().collectionSharding(values, (sublist) -> {
            QueryWrapper<Object> wrapper = Pops.query().from(references);
            // FIXME: zbh 20251128 此处不能使用 or 表达式，如果配置多个 labelFields 则需要进行多次查询
            wrapper.in(labelFieldColumns.get(0), sublist);
            wrapper.setSortable(false);
            wrapper.setBatchSize(-1);
            return Models.origin().queryListByWrapper(wrapper);
        });
        for (Object item : list) {
            // FIXME: zbh 20251210 此处可能出现遗漏匹配的问题
            for (String labelField : labelFields) {
                Object target = FieldUtils.getFieldValue(item, labelField);
                if (target == null) {
                    continue;
                }
                String maybeMatchedValue = String.valueOf(target);
                List<QuickFillingRow> matchedRows = matchValues.get(maybeMatchedValue);
                if (matchedRows == null) {
                    continue;
                }
                matchedValues.add(maybeMatchedValue);
                for (QuickFillingRow matchedRow : matchedRows) {
                    setValue(matchedRow, item);
                }
                break;
            }
        }
        for (String nonMatchedValue : Sets.difference(values, matchedValues)) {
            List<QuickFillingRow> nonMatchedRows = matchValues.get(nonMatchedValue);
            if (nonMatchedRows == null) {
                continue;
            }
            for (QuickFillingRow nonMatchedRow : nonMatchedRows) {
                validateError(nonMatchedRow, QuickFillingExpEnumerate.NON_MATCH_RELATION_DATA_ERROR.msg());
            }
        }
    }
}
