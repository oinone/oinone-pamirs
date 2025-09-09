package pro.shushi.pamirs.boot.web.spi.group;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.base.tmodel.GroupField;
import pro.shushi.pamirs.boot.base.tmodel.GroupInfo;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Gesi at 9:44 on 2025/9/9
 */
public abstract class AbstractGroupStatisticApi implements GroupStatisticApi {

    protected List<BigDecimal> formatNumber(List<?> dataList) {
        if (dataList == null) {
            return Lists.newArrayList(BigDecimal.ZERO);
        }
        List<BigDecimal> numberList = dataList.stream().filter(Objects::nonNull).map(data -> new BigDecimal(data.toString())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(numberList)) {
            return Lists.newArrayList(BigDecimal.ZERO);
        }
        return numberList;
    }

    protected void sort(List<BigDecimal> list) {
        list.sort(Comparator.nullsFirst(BigDecimal::compareTo));
    }

    protected BigDecimal sum(List<BigDecimal> list) {
        return list.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected BigDecimal avg(List<BigDecimal> list) {
        return sum(list).divide(
                BigDecimal.valueOf(list.size()), RoundingMode.HALF_UP);
    }

    protected BigDecimal max(List<BigDecimal> list) {
        return list.stream().filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(null);
    }

    protected BigDecimal min(List<BigDecimal> list) {
        return list.stream().filter(Objects::nonNull).min(BigDecimal::compareTo).orElse(null);
    }

    protected BigDecimal median(List<BigDecimal> list) {
        if (list == null || list.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<BigDecimal> values = list.stream()
                .map(v -> v == null ? BigDecimal.ZERO : v)
                .sorted()
                .collect(Collectors.toList());

        int size = values.size();
        int mid = size / 2;

        if (size % 2 == 1) {
            return values.get(mid);
        } else {
            BigDecimal a = values.get(mid - 1);
            BigDecimal b = values.get(mid);
            return a.add(b).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        }
    }

}
