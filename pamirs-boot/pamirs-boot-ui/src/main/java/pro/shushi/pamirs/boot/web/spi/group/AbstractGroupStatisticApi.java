package pro.shushi.pamirs.boot.web.spi.group;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;
import pro.shushi.pamirs.boot.web.utils.GroupStatisticUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gesi at 9:44 on 2025/9/9
 */
public abstract class AbstractGroupStatisticApi implements GroupStatisticApi {

    protected long total(List<?> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        return dataList.size();
    }

    protected long filled(List<?> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        return dataList.stream()
                .filter(Objects::nonNull)
                .filter(i -> !i.equals(""))
                .filter(i -> (!(i instanceof Map) || !((Map<?, ?>) i).isEmpty()))
                .count();
    }

    protected long notFilled(List<?> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        return total(dataList) - filled(dataList);
    }

    protected long unique(List<?> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }
        return dataList.stream().distinct().count();
    }

    protected Pair<Date, Date> earliestTimeAndLatestTime(List<?> dataList) {
        return GroupStatisticUtils.earliestTimeAndLatestTime(dataList);
    }

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
        return (BigDecimal) GroupStatisticUtils.formatNumber(
                sum(list).divide(
                        BigDecimal.valueOf(list.size()), RoundingMode.HALF_UP),
                2
        );
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
            return (BigDecimal) GroupStatisticUtils.formatNumber(values.get(mid), 2);
        } else {
            BigDecimal a = values.get(mid - 1);
            BigDecimal b = values.get(mid);
            return (BigDecimal) GroupStatisticUtils.formatNumber(a.add(b).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP), 2);
        }
    }

}
