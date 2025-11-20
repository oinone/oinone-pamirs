package pro.shushi.pamirs.grouping.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 统计帮助类
 *
 * @author Adamancy Zhang at 15:46 on 2025-11-20
 */
public class StatisticHelper {

    private StatisticHelper() {
        // reject create object
    }

    public static String computePercent(long count, long total) {
        return BigDecimal.valueOf(count)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100L))
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}
