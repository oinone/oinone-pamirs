package pro.shushi.pamirs.grouping.statistic;

import java.util.List;

/**
 * 统计API
 *
 * @author Adamancy Zhang at 14:32 on 2025-11-20
 */
public interface StatisticApi<T> {

    void compute(List<T> data);

    String getResult();

}
